package kaede.reabista.network;

import kaede.reabista.client.ClientData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncStoryModePacket {

    private final boolean storyMode;

    public SyncStoryModePacket(boolean storyMode) {
        this.storyMode = storyMode;
    }

    public static void encode(SyncStoryModePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.storyMode);
    }

    public static SyncStoryModePacket decode(FriendlyByteBuf buf) {
        return new SyncStoryModePacket(buf.readBoolean());
    }

    public static void handle(SyncStoryModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientData.storyMode = msg.storyMode;
        });
        ctx.get().setPacketHandled(true);
    }
}
