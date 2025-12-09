package kaede.reabista.events;

import kaede.reabista.ReabistaConfig;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_1;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_2;
import kaede.reabista.weapons.item.theusfall.Theusfall_1;
import kaede.reabista.weapons.item.theusfall.Theusfall_2;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "reabista")
public class AttackEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        // 攻撃者がLivingEntity以外なら無視
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        // メインハンドの武器を取得
        ItemStack weapon = attacker.getMainHandItem();

        // Theusfallじゃなければ無視
        if (!(weapon.getItem() instanceof Theusfall_1)){
            if (!(weapon.getItem() instanceof Theusfall_2)) {
                if (!(weapon.getItem() instanceof Thaosvenom_1)){
                    if (!(weapon.getItem() instanceof Thaosvenom_2)) {
                        return;
                    }
                }
            }
        }

        CompoundTag tag = weapon.getOrCreateTag();
        int hits = tag.getInt("hit_count");

        // 元ダメージ（Attribute）
        double baseDamage = attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue();

        // 追加ダメージconfig：
        double multiplier = ReabistaConfig.COMMON.MULTIPLIER.get();

        // 追加ダメージ計算：
        //   extra = (baseDamage / 2) × MULTIPLIER × (hits /10)
        float extra = (float)((baseDamage / 2) * multiplier * ((double) hits / 10));

        // ダメージ上乗せ
        event.setAmount(extra);
    }
}
