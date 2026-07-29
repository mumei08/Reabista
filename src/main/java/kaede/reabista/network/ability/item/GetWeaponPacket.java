package kaede.reabista.network.ability.item;

import kaede.reabista.registry.ModAttributes;
import kaede.reabista.registry.ModItems;
import kaede.reabista.weapons.item.ModItemWom;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record GetWeaponPacket(int num) {

    public static void encode(GetWeaponPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.num());
    }

    public static GetWeaponPacket decode(FriendlyByteBuf buf) {
        return new GetWeaponPacket(buf.readInt());
    }

    public static void handle(GetWeaponPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            // ★ 現在持っている武器
            ItemStack oldStack = player.getMainHandItem();
            if (oldStack.isEmpty()) return;

            // ★ NBT をコピー
            // getOrCreateTag() にすると null じゃないから安全
            var oldNbt = oldStack.getOrCreateTag().copy();

            ItemStack newStack = null;
            String i = "";

            switch (msg.num()) {
                case 1 -> {
                    newStack = new ItemStack(ModItemWom.GOD_THEUSFALL_1.get());
                    i ="古代編集者の武器が呼びかけに答え、復活した";
                }
                case 2 -> {
                    newStack = new ItemStack(ModItemWom.GOD_THEUSFALL_2.get());
                    i ="古代編集者の武器が呼びかけに答え、復活した";
                }
                case 3 -> {
                    newStack = new ItemStack(ModItemWom.GOD_THAOSVENOM_1.get());
                    i ="古代複製者の武器が呼びかけに答え、復活した";
                }
                case 4 -> {
                    newStack = new ItemStack(ModItemWom.GOD_THAOSVENOM_2.get());
                    i ="古代複製者の武器が呼びかけに答え、復活した";
                }
            }

            // ★ 新しいスタックへ NBT をそのまま移植
            newStack.setTag(oldNbt);

            player.sendSystemMessage(
                    Component.literal(i));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

            // ★ 手に持たせる
            player.setItemInHand(InteractionHand.MAIN_HAND, newStack);

            player.inventoryMenu.broadcastChanges();
        });
        ctx.get().setPacketHandled(true);
    }
}
