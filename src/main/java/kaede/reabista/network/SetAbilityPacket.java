package kaede.reabista.network;

import kaede.reabista.capabilities.AbilityDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import kaede.reabista.registry.ModAttributes;

import java.util.function.Supplier;

public class SetAbilityPacket {
    private final int ability;

    public SetAbilityPacket(int ability) {
        this.ability = ability;
    }

    public static void encode(SetAbilityPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.ability);
    }

    public static SetAbilityPacket decode(FriendlyByteBuf buf) {
        return new SetAbilityPacket(buf.readInt());
    }

    public static void handle(SetAbilityPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getAttribute(ModAttributes.ABILITY.get())
                        .setBaseValue(pkt.ability);
            }
            if (pkt.ability == 5){
                AbilityDataAPI.get(player).setGluttonyEnabled(true);
            }else {
                AbilityDataAPI.get(player).setGluttonyEnabled(false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}