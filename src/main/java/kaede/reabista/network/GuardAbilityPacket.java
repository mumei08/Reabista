package kaede.reabista.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GuardAbilityPacket {
    public enum Type {
        RESISTANCE_LV4,
        ATTACK_LV3
    }

    private final Type type;

    public GuardAbilityPacket(Type type) {
        this.type = type;
    }

    public static void encode(GuardAbilityPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.type);
    }

    public static GuardAbilityPacket decode(FriendlyByteBuf buf) {
        return new GuardAbilityPacket(buf.readEnum(Type.class));
    }

    public static void handle(GuardAbilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player == null) return;

            switch (msg.type) {
                case RESISTANCE_LV4 -> {
                    // TODO: サーバー側で耐性LV4を付与
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 36000, 3));
                }
                case ATTACK_LV3 -> {
                    // TODO: サーバー側で攻撃力LV3を付与
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 36000, 2));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
