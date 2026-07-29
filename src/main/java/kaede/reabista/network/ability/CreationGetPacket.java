package kaede.reabista.network.ability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 創造能力(基本)のサーバー側処理。
 * クライアントで名前解決済みのアイテムResourceLocation文字列を受け取り、検証してから1個渡す。
 */
public record CreationGetPacket(String itemId) {

    public static void encode(CreationGetPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.itemId, 256);
    }

    public static CreationGetPacket decode(FriendlyByteBuf buf) {
        return new CreationGetPacket(buf.readUtf(256));
    }

    public static void handle(CreationGetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ResourceLocation id = ResourceLocation.tryParse(msg.itemId);
            if (id == null) return;

            Item item = BuiltInRegistries.ITEM.get(id);
            if (item == null || item == net.minecraft.world.item.Items.AIR) {
                player.sendSystemMessage(Component.literal("§c不明なアイテムです: " + msg.itemId));
                return;
            }

            player.getInventory().add(new ItemStack(item, 1));
            player.sendSystemMessage(Component.literal("§a創造した: " + id));
        });
        ctx.get().setPacketHandled(true);
    }
}
