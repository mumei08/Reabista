package kaede.reabista.network;

import kaede.reabista.registry.ModAttributes;
import kaede.reabista.util.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class StatusGuiButtonMessage {

    private final String type;
    private final int value;

    public StatusGuiButtonMessage(String type, int value) {
        this.type = type;
        this.value = value;
    }

    // ===== エンコード/デコード =====
    public static void encode(StatusGuiButtonMessage msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.type);
        buf.writeInt(msg.value);
    }

    public static StatusGuiButtonMessage decode(FriendlyByteBuf buf) {
        return new StatusGuiButtonMessage(buf.readUtf(), buf.readInt());
    }

    // ===== サーバーでの処理 =====
    public static void handle(StatusGuiButtonMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // 実際の能力値更新
            AttributeUtils.applyStatusPoint(player, msg.type, msg.value);
        });
        ctx.get().setPacketHandled(true);
    }
}
