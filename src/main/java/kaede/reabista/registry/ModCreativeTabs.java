package kaede.reabista.registry;

import kaede.reabista.Reabista;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reabista.MODID);

    public static final RegistryObject<CreativeModeTab> Abista_TAB =
            CREATIVE_TABS.register("abista_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.literal("アビスタMOD"))
                            .icon(() -> new ItemStack(ModItems.ABILITY_SET_ITEM.get()))
                            .displayItems((params, output) -> {
                                output.accept(ModItems.COMPRESSED_NETHERITE_9X.get());
                                output.accept(ModItems.BEDROCK_NETHERSTAR.get());
                                output.accept(ModItems.NANI_KORE.get());
                                output.accept(ModItems.EDITOR_EYE.get());
                                output.accept(ModItems.COPIER_EYE.get());
                                output.accept(ModItems.WORLD_FRAGMENT.get());
                                output.accept(ModItems.YGGDRASIL_KEY.get());
                                output.accept(ModItems.ABILITY_SET_ITEM.get());
                                output.accept(ModItems.SORASUMMON.get());
                            })
                            .build()
            );
}

