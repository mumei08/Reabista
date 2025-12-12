package kaede.reabista.registry;

import kaede.reabista.Reabista;
import kaede.reabista.item.*;
import kaede.reabista.item.Crystal.*;
import kaede.reabista.item.create.Monster;
import kaede.reabista.item.create.Redbull;
import kaede.reabista.item.create.Zone;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Reabista.MODID); // ← Registries.ITEM から変更

    public static final RegistryObject<Item> COMPRESSED_NETHERITE_9X =
            ITEMS.register("compressed_netherite_block",
                    () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> BEDROCK_NETHERSTAR =
            ITEMS.register("bedrock_netherstar",
                    () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> NANI_KORE =
            ITEMS.register("nanikore",
                    () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> WORLD_FRAGMENT =
            ITEMS.register("sekai",
                    () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> EDITOR_EYE =
            ITEMS.register("edit_eye", EditorEyeItem::new);

    public static final RegistryObject<Item> COPIER_EYE =
            ITEMS.register("copy_eye", CopierEyeItem::new);

    public static final RegistryObject<Item> YGGDRASIL_KEY =
            ITEMS.register("yggdrasilkey", YggdrasilKeyItem::new);

    public static final RegistryObject<Item> ABILITY_SET_ITEM =
            ITEMS.register("abilitysetitem", AbilitySetItem::new);

    public static final RegistryObject<Item> SORASUMMON =
            ITEMS.register("sorasummon", SorasummonItem::new);

    //以下Createアイテム

    public static final RegistryObject<Item> MONSTER =
            ITEMS.register("monster", Monster::new);
    public static final RegistryObject<Item> ZONE =
            ITEMS.register("zone", Zone::new);
    public static final RegistryObject<Item> REDBULL =
            ITEMS.register("redbull", Redbull::new);

    //以下結晶化アイテム

    public static final RegistryObject<Item> EDIT_CRYSTAL =
            ITEMS.register("edit_crystal", EditCrystal::new);
    public static final RegistryObject<Item> COPY_CRYSTAL =
            ITEMS.register("copy_crystal", CopyCrystal::new);
    public static final RegistryObject<Item> FLY_CRYSTAL =
            ITEMS.register("fly_crystal", FlyCrystal::new);
    public static final RegistryObject<Item> TELEPORT_CRYSTAL =
            ITEMS.register("teleport_crystal", TeleportCrystal::new);
    public static final RegistryObject<Item> GLUTTONY_CRYSTAL =
            ITEMS.register("gluttony_crystal", GluttonyCrystal::new);
    public static final RegistryObject<Item> GUARD_CRYSTAL =
            ITEMS.register("guard_crystal", GuardCrystal::new);
}