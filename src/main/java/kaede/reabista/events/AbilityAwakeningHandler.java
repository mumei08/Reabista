package kaede.reabista.events;

import kaede.reabista.Reabista;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reabista.MODID)
public class AbilityAwakeningHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        final ResourceLocation ADV_ID0 =
                new ResourceLocation("reabista", "empty_ability_player");
        final ResourceLocation ADV_ID1 =
                new ResourceLocation("reabista", "root2");
        final ResourceLocation ADV_ID2 =
                new ResourceLocation("reabista", "ability_add_advancement");

        // ENDフェーズ & サーバー側のみ
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        if (!(event.player instanceof ServerPlayer p)) return;

        Player player = event.player;

        AttributeInstance ability = player.getAttribute(ModAttributes.ABILITY.get());
        AttributeInstance ap = player.getAttribute(ModAttributes.ABILITY_POINT.get());
        AttributeInstance imitate = player.getAttribute(ModAttributes.IMITATE.get());
        AttributeInstance statusPoint = player.getAttribute(ModAttributes.STATUS_POINT.get());
        AttributeInstance craft = player.getAttribute(ModAttributes.CRAFT.get());
        AttributeInstance breakAttr = player.getAttribute(ModAttributes.BREAK.get());

        if (ability == null || ap == null || imitate == null) return;

        // 無能力でなければ処理しない
        if ((int) ability.getValue() != 0) return;

        Advancement adv0 = event.player.level().getServer()
                .getAdvancements()
                .getAdvancement(ADV_ID0);

        if (adv0 == null) return;
        AdvancementProgress progress0 =
                p.getAdvancements().getOrStartProgress(adv0);

        if (!progress0.isDone()) {
            for (String criteria0 : progress0.getRemainingCriteria()) {
                p.getAdvancements().award(adv0, criteria0);
            }
        }

        RandomSource random = player.getRandom();

        // ===== 編集能力判定 =====
        if (ap.getValue() >= 1000) {
            if (random.nextInt(200) == 0) {
                ability.setBaseValue(1);
                player.sendSystemMessage(
                        Component.literal("能力『編集』が覚醒した"));
                player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                Advancement adv1 = event.player.level().getServer()
                        .getAdvancements()
                        .getAdvancement(ADV_ID1);
                Advancement adv2 = event.player.level().getServer()
                        .getAdvancements()
                        .getAdvancement(ADV_ID2);

                if (adv1 == null) return;
                if (adv2 == null) return;
                AdvancementProgress progress =
                        p.getAdvancements().getOrStartProgress(adv1);
                AdvancementProgress progress2 =
                        p.getAdvancements().getOrStartProgress(adv2);

                if (!progress.isDone()&&!progress2.isDone()) {
                    for (String criteria : progress.getRemainingCriteria()) {
                        p.getAdvancements().award(adv1, criteria);
                    }
                    for (String criteria2 : progress2.getRemainingCriteria()) {
                        p.getAdvancements().award(adv2, criteria2);
                    }
                }
                return;
            }
        }

        // ===== 複製能力判定 =====
        if (imitate.getValue() >= 1000) {
            if (random.nextInt(200) == 0) {
                ability.setBaseValue(2);
                player.sendSystemMessage(
                        Component.literal("能力『複製』が覚醒した"));
                player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                Advancement adv1 = event.player.level().getServer()
                        .getAdvancements()
                        .getAdvancement(ADV_ID1);
                Advancement adv2 = event.player.level().getServer()
                        .getAdvancements()
                        .getAdvancement(ADV_ID2);

                if (adv1 == null) return;
                if (adv2 == null) return;
                AdvancementProgress progress =
                        p.getAdvancements().getOrStartProgress(adv1);
                AdvancementProgress progress2 =
                        p.getAdvancements().getOrStartProgress(adv2);

                if (!progress.isDone()&&!progress2.isDone()) {
                    for (String criteria : progress.getRemainingCriteria()) {
                        p.getAdvancements().award(adv1, criteria);
                    }
                    for (String criteria2 : progress2.getRemainingCriteria()) {
                        p.getAdvancements().award(adv2, criteria2);
                    }
                }
            }
        }

        // ===== 0と1(ID12)判定: Craft&Break&StatusPointが全て100以上、1/100 =====
        if (statusPoint != null && craft != null && breakAttr != null
                && statusPoint.getValue() >= 100 && craft.getValue() >= 100 && breakAttr.getValue() >= 100) {
            if (random.nextInt(100) == 0) {
                ability.setBaseValue(12);
                player.sendSystemMessage(Component.literal("能力『0と1』が覚醒した"));
                player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                awardAbilityAdvancement(p, event, "ability_binary_advancement");
                return;
            }
        }

        // ===== 創造(ID14)判定: craft属性2000以上、1/100 =====
        if (craft != null && craft.getValue() >= 2000) {
            if (random.nextInt(100) == 0) {
                ability.setBaseValue(14);
                player.sendSystemMessage(Component.literal("能力『創造』が覚醒した"));
                player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                awardAbilityAdvancement(p, event, "ability_creation_advancement");
                return;
            }
        }

        // ===== 破壊(ID15)判定: break属性2000以上、1/100 =====
        if (breakAttr != null && breakAttr.getValue() >= 2000) {
            if (random.nextInt(100) == 0) {
                ability.setBaseValue(15);
                player.sendSystemMessage(Component.literal("能力『破壊』が覚醒した"));
                player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                awardAbilityAdvancement(p, event, "ability_destruction_advancement");
                return;
            }
        }
    }

    /**
     * root2(親) + 指定した個別実績の両方を付与する共通ヘルパー。
     * 編集/複製と同じ二段構成(root2 → 個別実績)に揃えてある。
     */
    private static void awardAbilityAdvancement(ServerPlayer p, TickEvent.PlayerTickEvent event, String advancementPath) {
        Advancement root2 = event.player.level().getServer()
                .getAdvancements()
                .getAdvancement(new ResourceLocation("reabista", "root2"));
        Advancement specific = event.player.level().getServer()
                .getAdvancements()
                .getAdvancement(new ResourceLocation("reabista", advancementPath));

        if (root2 != null) {
            AdvancementProgress rootProgress = p.getAdvancements().getOrStartProgress(root2);
            if (!rootProgress.isDone()) {
                for (String criteria : rootProgress.getRemainingCriteria()) {
                    p.getAdvancements().award(root2, criteria);
                }
            }
        }
        if (specific != null) {
            AdvancementProgress specificProgress = p.getAdvancements().getOrStartProgress(specific);
            if (!specificProgress.isDone()) {
                for (String criteria : specificProgress.getRemainingCriteria()) {
                    p.getAdvancements().award(specific, criteria);
                }
            }
        }
    }
}
