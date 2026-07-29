package kaede.reabista.network.ability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * ヒーラー能力のアクティブ発動パケット。
 * SELF_HEAL   : 自分自身を即座に大回復
 * AREA_HEAL   : 周囲の負傷した味方(自分含む)を纏めて回復
 */
public class HealAbilityPacket {
    public enum Type {
        SELF_HEAL,
        AREA_HEAL
    }

    private static final double AREA_RADIUS = 8.0;

    private final Type type;

    public HealAbilityPacket(Type type) {
        this.type = type;
    }

    public static void encode(HealAbilityPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.type);
    }

    public static HealAbilityPacket decode(FriendlyByteBuf buf) {
        return new HealAbilityPacket(buf.readEnum(Type.class));
    }

    public static void handle(HealAbilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel serverLevel)) return;

            switch (msg.type) {
                case SELF_HEAL -> {
                    player.heal(player.getMaxHealth());
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                    spawnHealParticles(serverLevel, player);
                }
                case AREA_HEAL -> {
                    AABB box = new AABB(player.position(), player.position()).inflate(AREA_RADIUS);
                    for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box,
                            e -> e.isAlive() && e.getHealth() < e.getMaxHealth())) {
                        target.heal(target.getMaxHealth() * 0.5F);
                        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                        spawnHealParticles(serverLevel, target);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void spawnHealParticles(ServerLevel level, LivingEntity target) {
        level.sendParticles(ParticleTypes.HEART,
                target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                8, 0.4, 0.4, 0.4, 0);
    }
}
