package kaede.reabista.registry;

import kaede.reabista.Reabista;
import kaede.reabista.entity.TokinosoraEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

@Mod.EventBusSubscriber(modid = Reabista.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reabista.MODID);

    public static final RegistryObject<EntityType<TokinosoraEntity>> TOKINOSORA = REGISTRY.register(
            "tokinosora",
            () -> EntityType.Builder.<TokinosoraEntity>of(TokinosoraEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f) // 幅、高さ
                    .clientTrackingRange(8)
                    .build("tokinosora")
    );

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}