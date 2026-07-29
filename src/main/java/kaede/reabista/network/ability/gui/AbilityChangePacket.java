package kaede.reabista.network.ability.gui;

import kaede.reabista.events.geterAbility;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public record AbilityChangePacket(int abilityId) {

    public static void encode(AbilityChangePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.abilityId());
    }

    public static AbilityChangePacket decode(FriendlyByteBuf buf) {
        return new AbilityChangePacket(buf.readInt());
    }

    public static void handle(AbilityChangePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            ServerLevel level = player.serverLevel();
            Item customItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("reabista", "abilitysetitem"));

            var attribute = player.getAttribute(ModAttributes.ABILITY.get());
            if (attribute == null) {
                player.sendSystemMessage(Component.literal("能力データが存在しません"));
                return;
            }

            attribute.setBaseValue(msg.abilityId());
            geterAbility.getterAbility(level, player);
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
                // アイテムが同じなら
                if (stack.getItem() == customItem) {
                    // 1個減らす
                    stack.shrink(1);
                    // 0個になったらスロットをクリア
                    if (stack.isEmpty()) player.getInventory().items.set(i, ItemStack.EMPTY);
                    break; // 1個だけ消したい場合はここでループ終了
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
