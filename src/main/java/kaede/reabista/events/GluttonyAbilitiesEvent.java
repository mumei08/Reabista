package kaede.reabista.events;

import kaede.reabista.Reabista;
import kaede.reabista.capabilities.AbilityDataAPI;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.LivingEntity;

@Mod.EventBusSubscriber(modid = Reabista.MODID)
public class GluttonyAbilitiesEvent {

    private static final ResourceLocation ADV_ID =
            new ResourceLocation("reabista:gluttony_advancement");
    private static final ResourceLocation ADV_ID2 =
            new ResourceLocation("reabista:gluttony_advancement_2");

    // ===========================
    //    Gluttony フラグ管理
    // ===========================
    public static boolean isGluttonyEnabled(LivingEntity living) {
        if (living instanceof Player player) {
            var data = AbilityDataAPI.get(player);
            return data != null && data.isGluttonyEnabled();
        }
        return false;
    }

    public static void clutchGluttony(LivingEntity living) {
        if (!(living instanceof Player player)) return;

        AbilityDataAPI.ifPresent(player, data -> {
            data.setGluttonyEnabled(!data.isGluttonyEnabled());
        });
    }

    // ===========================
    //     tick 常時処理
    // ===========================
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();

        if (!isGluttonyEnabled(living)) return;

        doGluttonyTick(living);
    }

    private static void doGluttonyTick(LivingEntity living) {

        if (living instanceof net.minecraft.world.entity.player.Player player) {
            var food = player.getFoodData();
            if (food.getFoodLevel() != 7) {
                food.setFoodLevel(7);
            }
        }
    }

    // ===========================
    //     食べ終わった時
    // ===========================
    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity living = event.getEntity();

        if (!isGluttonyEnabled(living)) return;

        onGluttonyEatFinish(living);
    }

    private static void onGluttonyEatFinish(LivingEntity living) {
        // 食事完了時の処理
        // --- GLUTTONY_FOOD +1 ---
        var attr = living.getAttribute(ModAttributes.GLUTTONY_FOOD.get());
        if (attr != null) {
            double current = attr.getBaseValue();
            attr.setBaseValue(current + 1);
        }

        // --- アドバンス付与 ---
        if (living instanceof ServerPlayer player) {
            var adv = player.server.getAdvancements().getAdvancement(ADV_ID);
            if (adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String c : progress.getRemainingCriteria())
                        player.getAdvancements().award(adv, c);
                }
            }
        }
    }

    // ===========================
//   右クリックで生物捕食
// ===========================
    @SubscribeEvent
    public static void onRightClickEntity(net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {

        LivingEntity living = event.getEntity();
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity victim)) return;

        if (!isGluttonyEnabled(living)) return;

        double ability = living.getAttribute(ModAttributes.ABILITY.get()).getBaseValue();
        double status  = living.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();

        // --- 実行条件 ---
        // ABILITY == 5 → STATUS_POINT >= 50 必須
        // ABILITY != 5 → STATUS_POINT 条件なし
        if (ability == 5) {
            if (status < 50) return;  // 条件満たさないので終了
        }
        // ABILITY != 5 の場合は何もせず通過

        // アドバンスメント1チェック
        if (living instanceof ServerPlayer sp) {
            var adv = sp.server.getAdvancements().getAdvancement(
                    new ResourceLocation("reabista:gluttony_advancement")
            );
            if (adv != null) {
                var progress = sp.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) return; // 未達成なら捕食不可
            }
        }

        // HP判定（最大値の1/3以下）
        if (victim.getHealth() > victim.getMaxHealth() / 3f) return;

        // 捕食モーション
        living.swing(event.getHand(), true);

        // エンティティ削除（サーバー）
        if (!victim.level().isClientSide()) victim.discard();

        // プレイヤー回復
        if (!living.level().isClientSide())
            living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HEAL, 1, 20
            ));

        // ---- GLUTTONY_ENTITY +1 ----
        var attr = living.getAttribute(ModAttributes.GLUTTONY_ENTITY.get());
        if (attr != null) {
            attr.setBaseValue(attr.getBaseValue() + 1);
        }
        // アドバンスメント2付与
        if (living instanceof ServerPlayer player) {
            var adv = player.server.getAdvancements().getAdvancement(ADV_ID2);
            if (adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String c : progress.getRemainingCriteria())
                        player.getAdvancements().award(adv, c);
                }
            }
        }
    }
}
