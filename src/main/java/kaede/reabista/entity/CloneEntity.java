package kaede.reabista.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * 分身能力で召喚される複製体。
 * バニラのZombieを継承しているため、ZombieRendererをそのまま流用できる
 * (専用モデル・専用レンダラーは用意していない簡易実装)。
 * 召喚主には敵対せず、周囲の敵対Mob/敵プレイヤーには攻撃を仕掛ける。
 * 一定時間で自然消滅する。
 */
public class CloneEntity extends Zombie {

    private static final int LIFETIME_TICKS = 1200; // 60秒で消滅
    private UUID ownerUUID;
    private int age = 0;

    public CloneEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;
    }

    public void setOwner(LivingEntity owner) {
        this.ownerUUID = owner.getUUID();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new OpenDoorGoal(this, true));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true,
                e -> !(e instanceof CloneEntity)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                e -> ownerUUID == null || !e.getUUID().equals(ownerUUID)));
    }

    @Override
    public boolean isSunSensitive() {
        return false; // 昼間に焼失させない
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) return;

        age++;
        if (age >= LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false; // ドラウンド化しない
    }

    @Override
    public void checkDespawn() {
        // 通常のMob自然デスポーン抽選を無効化(寿命管理は自前で行う)
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D);
    }
}
