package kaede.reabista.existence;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

/**
 * アイテムの存在ワールドを管理するデータクラス
 * 各アイテムがどのワールドに存在できるかを追跡
 */
public class ItemExistenceData {
    private static final String NBT_KEY = "ReabistaExistence";
    private static final String WORLDS_KEY = "worlds";

    private final Set<String> existenceWorlds = new HashSet<>();

    public ItemExistenceData() {
    }

    public ItemExistenceData(Set<String> worlds) {
        this.existenceWorlds.addAll(worlds);
    }

    /**
     * 指定ワールドに存在するか確認
     */
    public boolean existsIn(String worldId) {
        return existenceWorlds.contains(worldId);
    }

    /**
     * ワールドを追加
     */
    public void addWorld(String worldId) {
        existenceWorlds.add(worldId);
    }

    /**
     * ワールドを削除
     */
    public void removeWorld(String worldId) {
        existenceWorlds.remove(worldId);
    }

    /**
     * 存在ワールドを取得
     */
    public Set<String> getWorlds() {
        return new HashSet<>(existenceWorlds);
    }

    /**
     * 存在ワールドが空かどうか
     */
    public boolean isEmpty() {
        return existenceWorlds.isEmpty();
    }

    /**
     * NBTからデータを読み込み
     */
    public static ItemExistenceData fromNBT(CompoundTag tag) {
        ItemExistenceData data = new ItemExistenceData();
        if (tag.contains(NBT_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag existenceTag = tag.getCompound(NBT_KEY);
            if (existenceTag.contains(WORLDS_KEY, Tag.TAG_LIST)) {
                ListTag worldsList = existenceTag.getList(WORLDS_KEY, Tag.TAG_STRING);
                for (int i = 0; i < worldsList.size(); i++) {
                    data.addWorld(worldsList.getString(i));
                }
            }
        }
        return data;
    }

    /**
     * NBTへデータを保存
     */
    public void toNBT(CompoundTag tag) {
        CompoundTag existenceTag = new CompoundTag();
        ListTag worldsList = new ListTag();
        for (String world : existenceWorlds) {
            worldsList.add(StringTag.valueOf(world));
        }
        existenceTag.put(WORLDS_KEY, worldsList);
        tag.put(NBT_KEY, existenceTag);
    }

    /**
     * NBTキーを取得
     */
    public static String getNbtKey() {
        return NBT_KEY;
    }
}
