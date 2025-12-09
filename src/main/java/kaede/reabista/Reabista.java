package kaede.reabista;

import kaede.reabista.network.NetworkHandler;
import kaede.reabista.registry.*;
import kaede.reabista.weapons.item.ModItemWom;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Reabista.MODID)
public class Reabista {

    public static final String MODID = "reabista";

    public Reabista() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModAttributes.ATTRIBUTES.register(modBus);
        modBus.addListener(this::setup);
        ModItems.ITEMS.register(modBus);
        if (ModList.get().isLoaded("wom")) {
            ModItemWom.ITEMS.register(modBus);
        }
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        ModEntities.register(modBus);
        modBus.addListener(ModEventHandlers::onAttributeCreate);
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                ReabistaConfig.COMMON_SPEC,
                "Reabista.toml"
        );
    }

    // CommonSetup
    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.registerCommonPackets(); // サーバーでも安全なパケットだけ
    }

    // ClientSetup
    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        NetworkHandler.registerClientPackets(); // GUI系パケットだけ登録
    }
}