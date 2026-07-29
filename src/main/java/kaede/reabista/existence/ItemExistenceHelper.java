package kaede.reabista.existence;

import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * ItemStackからexistenceデータを読み書きするユーティリティ
 */
public class ItemExistenceHelper {

    /**
     * ItemStackにexistenceデータが存在するか確認
     */
    public static boolean hasExistenceData(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(ItemExistenceData.getNbtKey());
    }

    /**
     * ItemStackからexistenceデータを取得
     * 存在しない場合は空のデータを返す
     */
    public static ItemExistenceData getExistenceData(ItemStack stack) {
        if (stack.isEmpty()) return new ItemExistenceData();
        CompoundTag tag = stack.getTag();
        if (tag == null) return new ItemExistenceData();
        return ItemExistenceData.fromNBT(tag);
    }

    /**
     * ItemStackにexistenceデータを設定
     */
    public static void setExistenceData(ItemStack stack, ItemExistenceData data) {
        if (stack.isEmpty()) return;
        CompoundTag tag = stack.getOrCreateTag();
        data.toNBT(tag);
    }

    /**
     * ItemStackが指定ワールドに存在するか確認
     */
    public static boolean existsIn(ItemStack stack, String worldId) {
        return getExistenceData(stack).existsIn(worldId);
    }

    /**
     * ItemStackにワールドを追加
     */
    public static void addWorld(ItemStack stack, String worldId) {
        ItemExistenceData data = getExistenceData(stack);
        data.addWorld(worldId);
        setExistenceData(stack, data);
    }

    /**
     * ItemStackにワールドを削除
     */
    public static void removeWorld(ItemStack stack, String worldId) {
        ItemExistenceData data = getExistenceData(stack);
        data.removeWorld(worldId);
        setExistenceData(stack, data);
    }

    /**
     * ItemStackの存在ワールドを取得
     */
    public static Set<String> getWorlds(ItemStack stack) {
        return getExistenceData(stack).getWorlds();
    }

    /**
     * 初期existenceを設定（アイテム作成時）
     * 現在のワールドにのみ存在するように設定
     */
    public static void initializeExistence(ItemStack stack, String currentWorldId) {
        if (stack.isEmpty()) return;
        if (hasExistenceData(stack)) return; // 既に設定済みなら何もしない

        ItemExistenceData data = new ItemExistenceData();
        data.addWorld(currentWorldId);
        setExistenceData(stack, data);
    }

    /**
     * 全ワールドに存在するように設定（特殊アイテム用）
     */
    public static void setExistsEverywhere(ItemStack stack, Set<String> allWorldIds) {
        if (stack.isEmpty()) return;
        ItemExistenceData data = new ItemExistenceData(allWorldIds);
        setExistenceData(stack, data);
    }
}
