package kaede.reabista.registry;

import kaede.reabista.Reabista;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reabista.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Reabista.MODID);

    public static final RegistryObject<Attribute> ABILITY =
            ATTRIBUTES.register("ability", () -> new RangedAttribute("attribute.reabista.ability", -1, -1, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> STATUS_POINT =
            ATTRIBUTES.register("status_point", () -> new RangedAttribute("attribute.reabista.status_point", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> HP_POINT =
            ATTRIBUTES.register("hp_point", () -> new RangedAttribute("attribute.reabista.hp_point", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> ATK_POINT =
            ATTRIBUTES.register("atk_point", () -> new RangedAttribute("attribute.reabista.atk_point", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> DEF_POINT =
            ATTRIBUTES.register("def_point", () -> new RangedAttribute("attribute.reabista.def_point", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> ABILITY_POINT =
            ATTRIBUTES.register("ability_point", () -> new RangedAttribute("attribute.reabista.ability_point", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> GLUTTONY_FOOD =
            ATTRIBUTES.register("gluttony_food", () -> new RangedAttribute("attribute.reabista.gluttony_food", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> GLUTTONY_ENTITY =
            ATTRIBUTES.register("gluttony_entity", () -> new RangedAttribute("attribute.reabista.gluttony_entity", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> IMITATE =
            ATTRIBUTES.register("imitate", () -> new RangedAttribute("attribute.reabista.imitate", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> CRAFT =
            ATTRIBUTES.register("craft", () -> new RangedAttribute("attribute.reabista.craft", 0, 0, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> BREAK =
            ATTRIBUTES.register("break", () -> new RangedAttribute("attribute.reabista.break", 0, 0, Integer.MAX_VALUE).setSyncable(true));

    // ---------------------------
    // Player に属性を登録する
    // ---------------------------
    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        // プレイヤー属性追加（重複チェック）
        if (!event.has(EntityType.PLAYER, ABILITY.get())) event.add(EntityType.PLAYER, ABILITY.get());
        if (!event.has(EntityType.PLAYER, HP_POINT.get())) event.add(EntityType.PLAYER, HP_POINT.get());
        if (!event.has(EntityType.PLAYER, ATK_POINT.get())) event.add(EntityType.PLAYER, ATK_POINT.get());
        if (!event.has(EntityType.PLAYER, DEF_POINT.get())) event.add(EntityType.PLAYER, DEF_POINT.get());
        if (!event.has(EntityType.PLAYER, ABILITY_POINT.get())) event.add(EntityType.PLAYER, ABILITY_POINT.get());
        if (!event.has(EntityType.PLAYER, STATUS_POINT.get())) event.add(EntityType.PLAYER, STATUS_POINT.get());
        if (!event.has(EntityType.PLAYER, GLUTTONY_FOOD.get())) event.add(EntityType.PLAYER, GLUTTONY_FOOD.get());
        if (!event.has(EntityType.PLAYER, GLUTTONY_ENTITY.get())) event.add(EntityType.PLAYER, GLUTTONY_ENTITY.get());
        if (!event.has(EntityType.PLAYER, IMITATE.get())) event.add(EntityType.PLAYER, IMITATE.get());
        if (!event.has(EntityType.PLAYER, CRAFT.get())) event.add(EntityType.PLAYER, CRAFT.get());
        if (!event.has(EntityType.PLAYER, BREAK.get())) event.add(EntityType.PLAYER, BREAK.get());
    }
}