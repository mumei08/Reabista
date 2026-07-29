package kaede.reabista.item.Crystal;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

/**
 * ヒーラー能力の結晶アイテム。
 * 所持しているだけで、自分の周囲にいる負傷したプレイヤーへ継続的に微回復を撒く
 * (アクティブな回復はHealAbilityPacket経由のHealerGUIから行う)
 */
public class HealerCrystal extends Item {

    private static final double PASSIVE_RADIUS = 6.0;
    private static final int PASSIVE_INTERVAL_TICKS = 40; // 2秒毎

    public HealerCrystal() {
        super(new Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity holder, int slot, boolean selected) {
        super.inventoryTick(stack, level, holder, slot, selected);

        if (!(level instanceof ServerLevel serverLevel)) return;
        if (level.getGameTime() % PASSIVE_INTERVAL_TICKS != 0) return;
        if (!(holder instanceof Player owner)) return;

        AABB box = new AABB(owner.position(), owner.position()).inflate(PASSIVE_RADIUS);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && e.getHealth() < e.getMaxHealth())) {
            target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0));
            serverLevel.sendParticles(ParticleTypes.HEART,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    2, 0.3, 0.3, 0.3, 0);
        }
    }
}
