package kaede.reabista.events;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class geterAbility {
    public static void getterAbility(ServerLevel level, ServerPlayer player){
        double attribute = player.getAttributeValue(ModAttributes.ABILITY.get());
        if (attribute <= 0)return;
        final ResourceLocation ADV_ID =
                new ResourceLocation("reabista", "root2");
        final ResourceLocation ADV_ID2 =
                new ResourceLocation("reabista", "simple_add_ability");
        Advancement adv = level.getServer()
                .getAdvancements()
                .getAdvancement(ADV_ID);
        Advancement adv2 = level.getServer()
                .getAdvancements()
                .getAdvancement(ADV_ID2);

        if (adv == null) return;
        if (adv2 == null) return;
        AdvancementProgress progress =
                player.getAdvancements().getOrStartProgress(adv);
        AdvancementProgress progress2 =
                player.getAdvancements().getOrStartProgress(adv2);

        if (!progress.isDone()&&!progress2.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                player.getAdvancements().award(adv, criteria);
            }
            for (String criteria2 : progress2.getRemainingCriteria()) {
                player.getAdvancements().award(adv2, criteria2);
            }
        }
    }
}
