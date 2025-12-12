package kaede.reabista.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record OpenAbilityGuiPacket(String ability) {

    public static void encode(OpenAbilityGuiPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.ability());
    }

    public static OpenAbilityGuiPacket decode(FriendlyByteBuf buf) {
        return new OpenAbilityGuiPacket(buf.readUtf(32767));
    }

    public static void handle(OpenAbilityGuiPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            switch (msg.ability().toLowerCase()) {
                case "edit" -> mc.setScreen(new kaede.reabista.client.gui.EditGUI(mc.player));
                case "copy" -> mc.setScreen(new kaede.reabista.client.gui.CopyGUI(mc.player));
                case "gluttony" -> mc.setScreen(new kaede.reabista.client.gui.GluttonyGUI(mc.player));
                case "guard" -> mc.setScreen(new kaede.reabista.client.gui.GuardGUI(mc.player));
                case "abilityset" -> mc.setScreen(new kaede.reabista.client.gui.AbilitySetGUI(mc.player));
                default -> {
                    mc.setScreen(new kaede.reabista.client.gui.CopyGUI(mc.player));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
