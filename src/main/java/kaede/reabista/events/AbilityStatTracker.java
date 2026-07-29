package kaede.reabista.events;

import kaede.reabista.Reabista;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 「創造」(ID14)・「破壊」(ID15)の覚醒条件用トラッカー。
 * クラフト成功 or ブロック設置で craft属性 +1、ブロック破壊で break属性 +1 する。
 * 閾値到達後の実際の覚醒抽選は AbilityAwakeningHandler が行う。
 */
@Mod.EventBusSubscriber(modid = Reabista.MODID)
public class AbilityStatTracker {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        addOne(player, ModAttributes.CRAFT.get());
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        addOne(player, ModAttributes.CRAFT.get());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        addOne(player, ModAttributes.BREAK.get());
    }

    private static void addOne(ServerPlayer player, net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        instance.setBaseValue(instance.getBaseValue() + 1);
    }
}
