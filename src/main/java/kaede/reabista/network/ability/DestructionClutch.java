package kaede.reabista.network.ability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 破壊能力(基本, GUI無し即時発動)。
 * 照準先のエンティティを即座に破壊し、通常の死亡ドロップを発生させる(足元に出現、拾うのはプレイヤー自身)。
 * 実プレイヤーは対象外(PvP即死の悪用防止)。
 */
public record DestructionClutch() {

    private static final double RANGE = 20.0;

    public static void encode(DestructionClutch msg, FriendlyByteBuf buf) {}

    public static DestructionClutch decode(FriendlyByteBuf buf) {
        return new DestructionClutch();
    }

    public static void handle(DestructionClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            Vec3 eyePos = player.getEyePosition();
            Vec3 viewVec = player.getViewVector(1.0F);
            Vec3 reachEnd = eyePos.add(viewVec.x * RANGE, viewVec.y * RANGE, viewVec.z * RANGE);

            Predicate<Entity> filter = e -> e != player && e.isAlive() && e.isPickable()
                    && e instanceof LivingEntity && !(e instanceof Player);
            EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                    level, player, eyePos, reachEnd,
                    player.getBoundingBox().expandTowards(viewVec.scale(RANGE)).inflate(1.0D),
                    filter
            );

            if (hit == null || !(hit.getEntity() instanceof LivingEntity target)) {
                player.sendSystemMessage(Component.literal("§7対象が見つかりません"));
                return;
            }

            String targetName = target.getName().getString();
            DamageSource source = level.damageSources().playerAttack(player);
            // hurt()経由だと無敵判定(isInvulnerableTo, ボス耐性等)に阻まれる可能性があるため、
            // 判定を一切通さず直接HPを0にしてdie()を呼ぶ(無敵貫通の強制破壊)
            target.setHealth(0.0F);
            target.die(source);

            player.sendSystemMessage(Component.literal("§c" + targetName + " §7を破壊した"));
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT,
                    hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 15, 0.3, 0.3, 0.3, 0.1);
        });
        ctx.get().setPacketHandled(true);
    }
}
