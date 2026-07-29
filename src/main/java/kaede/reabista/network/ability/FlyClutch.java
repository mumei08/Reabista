package kaede.reabista.network.ability;

import kaede.reabista.events.FlyAbilitiesEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record FlyClutch() {

    public static void encode(FlyClutch msg, FriendlyByteBuf buf) {}

    public static FlyClutch decode(FriendlyByteBuf buf) {
        return new FlyClutch();
    }

    public static void handle(FlyClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            FlyAbilitiesEvent.clutchFly(player);
        });
        ctx.get().setPacketHandled(true);
    }
}