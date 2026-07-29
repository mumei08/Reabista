package kaede.reabista.util;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class AttributeTickHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) return; // サーバー側のみ
        if (!(entity instanceof Player)) return; // プレイヤー以外は無視

        // HP
        syncModifier(entity, Attributes.MAX_HEALTH,
                ModAttributes.HP_POINT.get(),
                UUID.fromString("2467de73-f790-4cc1-941d-5b65afbffa10"),
                "HP");

        // ATK
        syncModifier(entity, Attributes.ATTACK_DAMAGE,
                ModAttributes.ATK_POINT.get(),
                UUID.fromString("fc975381-d0ac-4684-a776-b53e80865d56"),
                "ATK");

        // DEF
        syncModifier(entity, Attributes.ARMOR,
                ModAttributes.DEF_POINT.get(),
                UUID.fromString("5823e5ab-9c8f-437a-8ec5-656336fb73a9"),
                "DEF");

        // Gluttony Food
        syncModifier(entity, Attributes.MAX_HEALTH,
                ModAttributes.GLUTTONY_FOOD.get(),
                UUID.fromString("9d7d004f-148c-4493-8f2f-53ee97febf66"),
                "FOODHP");

        syncModifier(entity, Attributes.ATTACK_DAMAGE,
                ModAttributes.GLUTTONY_FOOD.get(),
                UUID.fromString("9d4aa19b-88be-4216-b6bc-7e4276aa8c88"),
                "FOODATK");

        // Gluttony Entity
        syncModifier(entity, Attributes.MAX_HEALTH,
                ModAttributes.GLUTTONY_ENTITY.get(),
                UUID.fromString("5890e162-f92b-4d27-b99b-58312c3db471"),
                "ENTITYHP",
                4.0); // 倍率
        syncModifier(entity, Attributes.ATTACK_DAMAGE,
                ModAttributes.GLUTTONY_ENTITY.get(),
                UUID.fromString("cb2e0860-2476-465a-a773-2bddf3d1944e"),
                "ENTITYATK",
                4.0);
    }

    private static void syncModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute targetAttr,
                                     net.minecraft.world.entity.ai.attributes.Attribute sourceAttr,
                                     UUID uuid, String name) {
        syncModifier(entity, targetAttr, sourceAttr, uuid, name, 1.0);
    }

    private static void syncModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute targetAttr,
                                     net.minecraft.world.entity.ai.attributes.Attribute sourceAttr,
                                     UUID uuid, String name, double multiplier) {
        double baseValue = entity.getAttributeBaseValue(sourceAttr) * multiplier;
        var attr = entity.getAttribute(targetAttr);

        // Modifier が存在するか確認
        AttributeModifier modifier = attr.getModifier(uuid);
        if (modifier == null || modifier.getAmount() != baseValue) {
            // 古い Modifier を削除して新しい Modifier を追加
            if (modifier != null) attr.removeModifier(uuid);
            attr.addPermanentModifier(new AttributeModifier(uuid, name, baseValue, AttributeModifier.Operation.ADDITION));
        }
    }
}
