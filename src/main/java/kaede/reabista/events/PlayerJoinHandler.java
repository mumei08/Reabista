package kaede.reabista.events;

import kaede.reabista.gamerules.AbilityDistributor;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.SyncStoryModePacket;
import kaede.reabista.registry.ModGamerules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerJoinHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        boolean story = player.serverLevel()
                .getGameRules()
                .getBoolean(ModGamerules.STORY_MODE);

        NetworkHandler.sendToClient(
                new SyncStoryModePacket(story),
                player
        );

        AbilityDistributor.distributeOnJoin(player);
    }
}
