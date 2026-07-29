package kaede.reabista.entity;

import kaede.reabista.registry.ModEntities;
import kaede.reabista.registry.ModItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TokinosoraEntity extends Monster {
    private final ServerBossEvent bossInfo;

    public TokinosoraEntity(EntityType<TokinosoraEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), BossBarColor.BLUE, BossBarOverlay.PROGRESS);
        setMaxUpStep(2f);
        xpReward = 0;
        setPersistenceRequired();
        this.moveControl = new MoveControl(this); // 地上用固定
    }

    public TokinosoraEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.TOKINOSORA.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        // ターゲット追従 + 攻撃
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, true));

        // ランダム徘徊
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));

        // 見回し
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        // ターゲット設定
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35D;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        this.spawnAtLocation(new ItemStack(ModItems.WORLD_FRAGMENT.get()));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            float maxHP = this.getMaxHealth();
            if (maxHP > 0 && amount >= maxHP / 3f) return false;
        }

        if (source.is(DamageTypes.IN_FIRE)
                || source.getDirectEntity() instanceof AbstractArrow
                || source.getDirectEntity() instanceof ThrownPotion
                || source.getDirectEntity() instanceof AreaEffectCloud
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.LIGHTNING_BOLT)
                || source.is(DamageTypes.EXPLOSION)
                || source.is(DamageTypes.PLAYER_EXPLOSION)
                || source.is(DamageTypes.TRIDENT)
                || source.is(DamageTypes.FALLING_ANVIL)
                || source.is(DamageTypes.DRAGON_BREATH)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.WITHER_SKULL)) return false;

        return super.hurt(source, amount);
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // 自身強化
        if (!this.level().isClientSide()) {
            this.removeEffect(MobEffects.POISON);
            this.removeEffect(MobEffects.WITHER);
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            this.removeEffect(MobEffects.WEAKNESS);

            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 3, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 9, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 3, false, false));
        }

        // 周囲弱体化
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate(19.5), e -> e instanceof LivingEntity);
        for (Entity e : entities) {
            if (e == this) continue; // 自分自身をスキップ
            LivingEntity _ent = (LivingEntity) e;
            if (!_ent.level().isClientSide()) {
                _ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 255, false, false));
                _ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 255, false, false));
                _ent.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 3, false, false));
            }
            if (_ent instanceof Player _p) {
                _p.getAbilities().flying = false;
                _p.onUpdateAbilities();
            }
        }

        this.setNoGravity(false); // 地上移動なので重力有効
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 1.5)
                .add(Attributes.MAX_HEALTH, 20170907)
                .add(Attributes.ARMOR, 200)
                .add(Attributes.ATTACK_DAMAGE, 39000)
                .add(Attributes.FOLLOW_RANGE, 2048)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1000)
                .add(Attributes.ATTACK_KNOCKBACK, 10);
    }
}
