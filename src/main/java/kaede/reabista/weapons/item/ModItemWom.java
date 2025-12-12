package kaede.reabista.weapons.item;

import kaede.reabista.Reabista;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_1;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_2;
import kaede.reabista.weapons.item.theusfall.Theusfall_1;
import kaede.reabista.weapons.item.theusfall.Theusfall_2;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItemWom {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Reabista.MODID);

    public static final RegistryObject<Item> THEUSFALL_1 =
            ITEMS.register("theusfall_1", () -> new Theusfall_1((new Item.Properties()).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> THEUSFALL_2 =
            ITEMS.register("theusfall_2", () -> new Theusfall_2((new Item.Properties()).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> THAOSVENOM_1 =
            ITEMS.register("thaosvenom_1", () -> new Thaosvenom_1((new Item.Properties()).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> THAOSVENOM_SHEATH_1 =
            ITEMS.register("thaosvenom_sheath_1", () -> new Item((new Item.Properties()).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> THAOSVENOM_2 =
            ITEMS.register("thaosvenom_2", () -> new Thaosvenom_2((new Item.Properties()).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> THAOSVENOM_HANDLE_2 =
            ITEMS.register("thaosvenom_handle_2", () -> new Item((new Item.Properties()).rarity(Rarity.EPIC)));
}
