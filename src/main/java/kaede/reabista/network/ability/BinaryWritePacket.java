package kaede.reabista.network.ability;

import kaede.reabista.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * 「0と1」能力: ビット列書き込みパケット(サーバー側)。
 * 0/1の並びを8bit毎に区切ってバイト列に変換 → UTF-8文字列として復号 →
 * ResourceLocationとして解釈できればアイテム/エンティティとして具現化する。
 * 消費するのは入力ビット列に含まれる0の数だけZeroFragment、1の数だけOneFragment。
 */
public record BinaryWritePacket(String bits) {

    private static final int MAX_BITS = 4096;

    public static void encode(BinaryWritePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.bits, MAX_BITS);
    }

    public static BinaryWritePacket decode(FriendlyByteBuf buf) {
        return new BinaryWritePacket(buf.readUtf(MAX_BITS));
    }

    public static void handle(BinaryWritePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            String bits = msg.bits();
            if (bits.isEmpty() || !bits.chars().allMatch(c -> c == '0' || c == '1')) {
                player.sendSystemMessage(Component.literal("§c0と1のみで構成してください"));
                return;
            }

            int zerosNeeded = (int) bits.chars().filter(c -> c == '0').count();
            int onesNeeded = (int) bits.chars().filter(c -> c == '1').count();

            int zeroHave = player.getInventory().countItem(ModItems.ZERO_FRAGMENT.get());
            int oneHave = player.getInventory().countItem(ModItems.ONE_FRAGMENT.get());

            if (zeroHave < zerosNeeded || oneHave < onesNeeded) {
                player.sendSystemMessage(Component.literal(
                        "§c断片が足りません(必要: 0×" + zerosNeeded + " 1×" + onesNeeded
                                + " / 所持: 0×" + zeroHave + " 1×" + oneHave + ")"));
                return;
            }

            String decoded = decodeBitsToUtf8(bits);
            if (decoded == null) {
                player.sendSystemMessage(Component.literal("§c8bit単位に区切れないビット列です"));
                return;
            }

            ResourceLocation id = ResourceLocation.tryParse(decoded);
            if (id == null) {
                player.sendSystemMessage(Component.literal("§c解読結果が不正なIDです: " + decoded));
                return;
            }

            Item item = BuiltInRegistries.ITEM.get(id);
            if (item != null && item != net.minecraft.world.item.Items.AIR) {
                consumeFragments(player, zerosNeeded, onesNeeded);
                player.getInventory().add(new ItemStack(item, 1));
                player.sendSystemMessage(Component.literal("§a解読成功: " + id + " §7を1個生み出した"));
                return;
            }

            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(id);
            if (entityType != null) {
                Entity spawned = entityType.create(level);
                if (spawned != null) {
                    consumeFragments(player, zerosNeeded, onesNeeded);
                    spawned.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0);
                    level.addFreshEntity(spawned);
                    player.sendSystemMessage(Component.literal("§a解読成功: " + id + " §7を召喚した"));
                    return;
                }
            }

            player.sendSystemMessage(Component.literal("§c解読はできましたが、該当するアイテム/エンティティが存在しません: " + id));
        });
        ctx.get().setPacketHandled(true);
    }

    private static void consumeFragments(ServerPlayer player, int zeros, int ones) {
        removeItems(player, ModItems.ZERO_FRAGMENT.get(), zeros);
        removeItems(player, ModItems.ONE_FRAGMENT.get(), ones);
    }

    private static void removeItems(ServerPlayer player, Item item, int amount) {
        int remaining = amount;
        var inv = player.getInventory().items;
        for (int i = 0; i < inv.size() && remaining > 0; i++) {
            ItemStack stack = inv.get(i);
            if (stack.getItem() != item) continue;
            int take = Math.min(stack.getCount(), remaining);
            stack.shrink(take);
            remaining -= take;
        }
    }

    private static String decodeBitsToUtf8(String bits) {
        if (bits.length() % 8 != 0) return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < bits.length(); i += 8) {
            String byteStr = bits.substring(i, i + 8);
            out.write((byte) Integer.parseInt(byteStr, 2));
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
