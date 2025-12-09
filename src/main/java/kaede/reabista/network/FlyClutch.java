package kaede.reabista.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record FlyClutch(Boolean set) {
    public static void encode(FlyClutch msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.set);
    }

    public static FlyClutch decode(FriendlyByteBuf buf) {
        return new FlyClutch(buf.readBoolean());
    }
    public static void handle(FlyClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            player.getAbilities().mayfly = msg.set();
            player.onUpdateAbilities();
        });
    }
}
