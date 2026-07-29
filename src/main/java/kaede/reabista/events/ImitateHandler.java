package kaede.reabista.events;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistries;

public class ImitateHandler {

    private static final String ILIS = "iLis";
    private static final String HOLDING = "holdingLis";

    /* =========================
       アイテム取得時処理
     ========================= */
    public static void onPickup(ServerPlayer player, ResourceLocation itemId) {

        CompoundTag data = player.getPersistentData();

        if (!data.contains(ILIS)) data.put(ILIS, new ListTag());
        if (!data.contains(HOLDING)) data.put(HOLDING, new ListTag());

        ListTag iLis = data.getList(ILIS, Tag.TAG_STRING);
        ListTag holding = data.getList(HOLDING, Tag.TAG_STRING);

        boolean inILis = contains(iLis, itemId.toString());
        boolean inHolding = contains(holding, itemId.toString());

        // 初回入手
        if (!inILis) {
            iLis.add(StringTag.valueOf(itemId.toString()));
            data.put(ILIS, iLis);
            return;
        }

        // 一度消えて再入手
        if (!inHolding) {
            addImitate(player);
        }
    }

    /* =========================
       毎Tick所持更新
     ========================= */
    public static void updateHolding(ServerPlayer player) {

        CompoundTag data = player.getPersistentData();
        if (!data.contains(HOLDING)) data.put(HOLDING, new ListTag());

        ListTag newHolding = new ListTag();

        player.getInventory().items.forEach(stack -> {
            if (!stack.isEmpty()) {
                ResourceLocation id =
                        ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (id != null) {
                    if (!contains(newHolding, id.toString())) {
                        newHolding.add(StringTag.valueOf(id.toString()));
                    }
                }
            }
        });

        data.put(HOLDING, newHolding);
    }

    /* =========================
       imitate +1
     ========================= */
    private static void addImitate(ServerPlayer player) {

        var instance = player.getAttribute(ModAttributes.IMITATE.get());
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() + 1);
        }
    }

    /* =========================
       ListTag contains
     ========================= */
    private static boolean contains(ListTag list, String value) {
        for (Tag tag : list) {
            if (tag.getAsString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
