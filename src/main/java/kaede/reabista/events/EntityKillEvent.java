package kaede.reabista.events;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityKillEvent {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return; // サーバー側のみ

        if (event.getSource().getEntity() instanceof Player player) {
            // Status_point 属性を取得して +1
            Attribute attr = ModAttributes.STATUS_POINT.get();
            player.getAttribute(attr).setBaseValue(player.getAttributeBaseValue(attr) + 1);

        }
    }
}
