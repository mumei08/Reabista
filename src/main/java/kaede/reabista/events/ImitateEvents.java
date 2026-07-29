package kaede.reabista.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class ImitateEvents {

    /* =========================
       アイテム取得
     ========================= */
    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var stack = event.getStack();
        if (stack.isEmpty()) return;

        ResourceLocation id =
                ForgeRegistries.ITEMS.getKey(stack.getItem());

        if (id != null) {
            ImitateHandler.onPickup(player, id);
        }
    }

    /* =========================
       毎Tick所持更新
     ========================= */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        ImitateHandler.updateHolding(player);
    }
}
