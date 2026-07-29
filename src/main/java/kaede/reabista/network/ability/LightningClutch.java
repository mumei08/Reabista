package kaede.reabista.network.ability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 雷を操る能力の発動パケット(GUI無し即時発動)。
 * プレイヤーの照準先(エンティティ優先、無ければ着弾点)に落雷を落とす。
 */
public record LightningClutch() {

    private static final double RANGE = 40.0;

    public static void encode(LightningClutch msg, FriendlyByteBuf buf) {}

    public static LightningClutch decode(FriendlyByteBuf buf) {
        return new LightningClutch();
    }

    public static void handle(LightningClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            Vec3 eyePos = player.getEyePosition();
            Vec3 viewVec = player.getViewVector(1.0F);
            Vec3 reachEnd = eyePos.add(viewVec.x * RANGE, viewVec.y * RANGE, viewVec.z * RANGE);

            // エンティティ優先で照準先を探す
            Predicate<Entity> filter = e -> e != player && e.isAlive() && e.isPickable();
            EntityHitResult entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                    level, player, eyePos, reachEnd,
                    player.getBoundingBox().expandTowards(viewVec.scale(RANGE)).inflate(1.0D),
                    filter
            );

            Vec3 strikePos;
            if (entityHit != null) {
                strikePos = entityHit.getLocation();
            } else {
                HitResult blockHit = player.pick(RANGE, 0.0F, false);
                strikePos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : reachEnd;
            }

            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt == null) return;
            bolt.moveTo(strikePos.x, strikePos.y, strikePos.z);
            bolt.setVisualOnly(false); // ダメージ・発火あり
            bolt.setCause(player);
            level.addFreshEntity(bolt);
        });
        ctx.get().setPacketHandled(true);
    }
}
