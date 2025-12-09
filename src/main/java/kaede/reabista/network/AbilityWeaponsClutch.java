package kaede.reabista.network;

import kaede.reabista.weapons.item.ModItemWom;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AbilityWeaponsClutch(Integer ability, Integer set) {
    public static void encode(AbilityWeaponsClutch msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.ability);
        buf.writeInt(msg.set);
    }

    public static AbilityWeaponsClutch decode(FriendlyByteBuf buf) {
        return new AbilityWeaponsClutch(buf.readInt(), buf.readInt());
    }
    public static void handle(AbilityWeaponsClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // ★ 現在持っている武器
            ItemStack oldStack = player.getMainHandItem();
            if (oldStack.isEmpty()) return;

            // ★ NBT をコピー
            // getOrCreateTag() にすると null じゃないから安全
            var oldNbt = oldStack.getOrCreateTag().copy();

            ItemStack newStack;

            if (msg.ability == 1) {
                if (msg.set == 1) {
                    newStack = new ItemStack(ModItemWom.THEUSFALL_2.get());
                } else if (msg.set == 2) {
                    newStack = new ItemStack(ModItemWom.THEUSFALL_1.get());
                } else {
                    return;
                }
            } else if (msg.ability == 2) {
                if (msg.set == 1) {
                    newStack = new ItemStack(ModItemWom.THAOSVENOM_2.get());
                } else if (msg.set == 2) {
                    newStack = new ItemStack(ModItemWom.THAOSVENOM_1.get());
                } else {
                    return;
                }
            }else {
                return;
            }

            // ★ 新しいスタックへ NBT をそのまま移植
            newStack.setTag(oldNbt);

            // ★ 手に持たせる
            player.setItemInHand(InteractionHand.MAIN_HAND, newStack);

            player.inventoryMenu.broadcastChanges();
        });
    }
}
