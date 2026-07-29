package kaede.reabista.client.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 「創造」能力用: アイテム/ブロックの表示名(現在の言語 or 英語)からResourceLocationを逆引きする。
 * クライアント専用(言語リソースはクライアントにしか無いため)。
 * 一度構築したら以降はキャッシュを使い回す。
 */
public final class NameResolver {
    private static Map<String, ResourceLocation> itemNameIndex;
    private static Map<String, ResourceLocation> blockNameIndex;
    private static Map<String, String> englishLangCache;

    private NameResolver() {}

    public static Optional<ResourceLocation> resolveItem(String query) {
        ensureItemIndex();
        return Optional.ofNullable(itemNameIndex.get(normalize(query)));
    }

    public static Optional<ResourceLocation> resolveBlock(String query) {
        ensureBlockIndex();
        return Optional.ofNullable(blockNameIndex.get(normalize(query)));
    }

    private static String normalize(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private static void ensureItemIndex() {
        if (itemNameIndex != null) return;
        itemNameIndex = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == net.minecraft.world.item.Items.AIR) continue;
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            String key = item.getDescriptionId();
            registerName(itemNameIndex, currentLocaleName(key), id);
            registerName(itemNameIndex, englishName(key), id);
            registerName(itemNameIndex, id.toString(), id); // "minecraft:diamond" 等の直接指定も許可
            registerName(itemNameIndex, id.getPath(), id);  // "diamond" だけでも許可
        }
    }

    private static void ensureBlockIndex() {
        if (blockNameIndex != null) return;
        blockNameIndex = new HashMap<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == net.minecraft.world.level.block.Blocks.AIR) continue;
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            String key = block.getDescriptionId();
            registerName(blockNameIndex, currentLocaleName(key), id);
            registerName(blockNameIndex, englishName(key), id);
            registerName(blockNameIndex, id.toString(), id);
            registerName(blockNameIndex, id.getPath(), id);
        }
    }

    private static void registerName(Map<String, ResourceLocation> map, String name, ResourceLocation id) {
        if (name == null || name.isBlank()) return;
        map.putIfAbsent(normalize(name), id);
    }

    private static String currentLocaleName(String descriptionKey) {
        return I18n.exists(descriptionKey) ? I18n.get(descriptionKey) : null;
    }

    /**
     * 現在の言語設定に関わらず、常に英語(en_us)の表示名を取得する。
     * バニラ+ReAbista本体のen_us.jsonをリソースマネージャから直接読み込んでキャッシュする。
     */
    private static String englishName(String descriptionKey) {
        ensureEnglishLangCache();
        return englishLangCache.get(descriptionKey);
    }

    private static void ensureEnglishLangCache() {
        if (englishLangCache != null) return;
        englishLangCache = new HashMap<>();
        loadLangFile(new ResourceLocation("minecraft", "lang/en_us.json"));
        loadLangFile(new ResourceLocation("reabista", "lang/en_us.json"));
    }

    private static void loadLangFile(ResourceLocation path) {
        try {
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(path);
            if (resource.isEmpty()) return;
            try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : json.keySet()) {
                    englishLangCache.putIfAbsent(key, json.get(key).getAsString());
                }
            }
        } catch (Exception ignored) {
            // 読めなければ英語名は諦めて現在言語のみで運用する
        }
    }
}
