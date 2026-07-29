package kaede.reabista.network.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 異消能力の発動パケット(GUI無し即時発動)。
 * 「異であるもの」=バニラMinecraftに存在しないブロック/エンティティ/アイテムを対象に消滅させる。
 * modIdが"minecraft"でないResourceLocationを持つものだけを対象にする。
 *
 * 【安全策】実プレイヤーはエンティティ自体を消滅させる対象からは除外し、
 * 代わりに手に持ってる非バニラアイテムだけを消す(PvPでの即死武器化を避けるため)。
 */
public record EraseClutch() {

    private static final double RANGE = 20.0;

    public static void encode(EraseClutch msg, FriendlyByteBuf buf) {}

    public static EraseClutch decode(FriendlyByteBuf buf) {
        return new EraseClutch();
    }

    public static void handle(EraseClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            Vec3 eyePos = player.getEyePosition();
            Vec3 viewVec = player.getViewVector(1.0F);
            Vec3 reachEnd = eyePos.add(viewVec.x * RANGE, viewVec.y * RANGE, viewVec.z * RANGE);

            Predicate<Entity> filter = e -> e != player && e.isAlive() && e.isPickable();
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level, player, eyePos, reachEnd,
                    player.getBoundingBox().expandTowards(viewVec.scale(RANGE)).inflate(1.0D),
                    filter
            );

            if (entityHit != null) {
                handleEntityTarget(player, level, entityHit.getEntity());
                return;
            }

            HitResult blockHit = player.pick(RANGE, 0.0F, false);
            if (blockHit instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS) {
                handleBlockTarget(player, level, blockHitResult);
                return;
            }

            player.sendSystemMessage(Component.literal("§7対象が見つかりません"));
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleEntityTarget(ServerPlayer player, ServerLevel level, Entity target) {
        if (target instanceof Player targetPlayer) {
            // 実プレイヤーは消滅させず、手持ちの非バニラアイテムだけ消す
            ItemStack main = targetPlayer.getMainHandItem();
            var id = BuiltInRegistries.ITEM.getKey(main.getItem());
            if (!main.isEmpty() && !id.getNamespace().equals("minecraft")) {
                targetPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                player.sendSystemMessage(Component.literal("§d異物を消滅させた: " + id));
                spawnEraseParticles(level, targetPlayer.position());
            } else {
                player.sendSystemMessage(Component.literal("§7消滅対象の異物がありません"));
            }
            return;
        }

        var entityId = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        if (entityId == null || entityId.getNamespace().equals("minecraft")) {
            player.sendSystemMessage(Component.literal("§7これはバニラの存在です。異消できません"));
            return;
        }

        spawnEraseParticles(level, target.position());
        player.sendSystemMessage(Component.literal("§d異物を消滅させた: " + entityId));
        target.discard();
    }

    private static void handleBlockTarget(ServerPlayer player, ServerLevel level, BlockHitResult hitResult) {
        BlockState state = level.getBlockState(hitResult.getBlockPos());
        var blockId = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null || blockId.getNamespace().equals("minecraft")) {
            player.sendSystemMessage(Component.literal("§7これはバニラの存在です。異消できません"));
            return;
        }

        level.removeBlock(hitResult.getBlockPos(), false);
        spawnEraseParticles(level, Vec3.atCenterOf(hitResult.getBlockPos()));
        player.sendSystemMessage(Component.literal("§d異物を消滅させた: " + blockId));
    }

    private static void spawnEraseParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.SQUID_INK, pos.x, pos.y, pos.z, 25, 0.4, 0.4, 0.4, 0.08);
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 25, 0.4, 0.4, 0.4, 0.2);
    }
}
