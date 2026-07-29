package kaede.reabista.registry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import kaede.reabista.client.renderer.TokinosoraRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TOKINOSORA.get(), TokinosoraRenderer::new);
        // 分身(CloneEntity)はZombie継承なのでバニラのZombieRendererをそのまま流用
        event.registerEntityRenderer(ModEntities.CLONE.get(), ZombieRenderer::new);
    }
}
