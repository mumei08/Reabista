package kaede.reabista.network.ability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 筋力増強能力の発動パケット(GUI無し即時発動)。
 * 攻撃力・移動速度・耐性を一定時間ブースト。
 */
public record StrengthClutch() {

    private static final int DURATION_TICKS = 600; // 30秒

    public static void encode(StrengthClutch msg, FriendlyByteBuf buf) {}

    public static StrengthClutch decode(FriendlyByteBuf buf) {
        return new StrengthClutch();
    }

    public static void handle(StrengthClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURATION_TICKS, 2, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION_TICKS, 1, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION_TICKS, 0, false, true));

            player.level().playSound(null, player.blockPosition(),
                    SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0F, 0.7F);
        });
        ctx.get().setPacketHandled(true);
    }
}
