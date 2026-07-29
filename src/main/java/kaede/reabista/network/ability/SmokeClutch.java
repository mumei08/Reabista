package kaede.reabista.network.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 煙幕能力の発動パケット(GUI無し即時発動)。
 * 自身の周囲に煙を撒いて自分は一時的に透明化、近くの敵対Mobには暗闇を付与して視界を封じる。
 */
public record SmokeClutch() {

    private static final double RADIUS = 6.0;
    private static final int INVISIBILITY_TICKS = 100; // 5秒
    private static final int BLINDNESS_TICKS = 80;      // 4秒

    public static void encode(SmokeClutch msg, FriendlyByteBuf buf) {}

    public static SmokeClutch decode(FriendlyByteBuf buf) {
        return new SmokeClutch();
    }

    public static void handle(SmokeClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVISIBILITY_TICKS, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, INVISIBILITY_TICKS, 0, false, false));

            AABB box = new AABB(player.position(), player.position()).inflate(RADIUS);
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e instanceof Monster)) {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_TICKS, 0, false, false));
            }

            level.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    60, 1.5, 0.5, 1.5, 0.02);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    player.getX(), player.getY() + 0.2, player.getZ(),
                    30, 1.0, 0.2, 1.0, 0.01);
        });
        ctx.get().setPacketHandled(true);
    }
}
