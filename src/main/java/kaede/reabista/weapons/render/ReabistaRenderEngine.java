package kaede.reabista.weapons.render;

import kaede.reabista.weapons.item.ModItemWom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;

@Mod.EventBusSubscriber(
        modid = "reabista",
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ReabistaRenderEngine {

    @SubscribeEvent
    public static void registerRenderer(PatchedRenderersEvent.Add event) {
        // Thaosvenom のレンダラ登録
        event.addItemRenderer(ModItemWom.THAOSVENOM_1.get(), new RenderThaosvenom_1());
        event.addItemRenderer(ModItemWom.THAOSVENOM_2.get(), new RenderThaosvenom_2());
    }
}