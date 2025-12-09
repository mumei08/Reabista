package kaede.reabista.events;

import kaede.reabista.capabilities.AbilityProvider;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CloneEventHandler {

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        event.getOriginal().getCapability(AbilityProvider.ABILITY).ifPresent(oldStore -> {
            event.getEntity().getCapability(AbilityProvider.ABILITY).ifPresent(newStore -> {
                newStore.setGluttonyEnabled(oldStore.isGluttonyEnabled());
            });
        });
    }
}