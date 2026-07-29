package kaede.reabista.events;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AttributePersistenceHandler {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {

        // 死亡時のみコピー
        if (!event.isWasDeath()) return;

        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        copyAttribute(oldPlayer, newPlayer, ModAttributes.ABILITY.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.STATUS_POINT.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.HP_POINT.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.ATK_POINT.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.DEF_POINT.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.ABILITY_POINT.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.GLUTTONY_FOOD.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.GLUTTONY_ENTITY.get());
        copyAttribute(oldPlayer, newPlayer, ModAttributes.IMITATE.get());
    }

    private static void copyAttribute(ServerPlayer oldPlayer, ServerPlayer newPlayer, Attribute attribute) {

        AttributeInstance oldInstance = oldPlayer.getAttribute(attribute);
        AttributeInstance newInstance = newPlayer.getAttribute(attribute);

        if (oldInstance != null && newInstance != null) {
            newInstance.setBaseValue(oldInstance.getBaseValue());
        }
    }
}
