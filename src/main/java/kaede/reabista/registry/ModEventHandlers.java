package kaede.reabista.registry;

import kaede.reabista.entity.TokinosoraEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEventHandlers {
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntities.TOKINOSORA.get(), TokinosoraEntity.createAttributes().build());
    }
}