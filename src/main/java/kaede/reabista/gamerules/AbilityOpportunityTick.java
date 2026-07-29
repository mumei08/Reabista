package kaede.reabista.gamerules;

import kaede.reabista.registry.ModGamerules;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AbilityOpportunityTick {

    private static final ResourceLocation ADV_ID =
            new ResourceLocation("reabista", "ability_set_advancement");

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {

        if (!(event.level instanceof ServerLevel level)) return;
        if (event.phase != TickEvent.Phase.END) return;

        if (level.getGameRules().getBoolean(ModGamerules.STORY_MODE)&&level.getGameRules().getBoolean(ModGamerules.MULTI_MODE)){
            level.getGameRules().getRule(ModGamerules.MULTI_MODE).set(false, level.getServer());
        }

        // モードが有効なら無効化
        if (ModeHandler.isAnyModeEnabled(level)) return;

        Advancement adv = level.getServer()
                .getAdvancements()
                .getAdvancement(ADV_ID);

        if (adv == null) return;

        // 🔥 プレイヤーをループ
        for (ServerPlayer player : level.players()) {

            AdvancementProgress progress =
                    player.getAdvancements().getOrStartProgress(adv);

            if (!progress.isDone()) {

                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(adv, criteria);
                }
            }
        }
    }
}