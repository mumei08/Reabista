package kaede.reabista.events;

import kaede.reabista.item.Crystal.FlyCrystal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 飛行の結晶化: FlyCrystalを所持している間、着弾しようとした飛び道具攻撃(矢・トライデント等)を
 * 命中させず空高くへ飛ばして無効化する。
 */
@Mod.EventBusSubscriber
public class FlyProjectileDeflectHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHit)) return;
        Entity victim = entityHit.getEntity();
        if (!(victim instanceof LivingEntity living)) return;

        boolean holdingFlyCrystal = living.getMainHandItem().getItem() instanceof FlyCrystal
                || living.getOffhandItem().getItem() instanceof FlyCrystal;
        if (!holdingFlyCrystal) return;

        Projectile projectile = event.getProjectile();
        event.setCanceled(true);

        // 飛び道具を空高くへ飛ばす(命中無効化 + 演出)
        projectile.setDeltaMovement(new Vec3(
                projectile.getDeltaMovement().x * 0.2,
                4.0,
                projectile.getDeltaMovement().z * 0.2
        ));
        projectile.hasImpulse = true;
    }
}
