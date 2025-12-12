package kaede.reabista.util;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class AttributeUtils {

    public static void applyStatusPoint(LivingEntity entity, String type, int value) {
        switch (type) {
            case "HP" -> {
                appleAttribute(entity,
                    ModAttributes.HP_POINT.get(),
                    Attributes.MAX_HEALTH,
                    value,
                    UUID.fromString("2467de73-f790-4cc1-941d-5b65afbffa10"),
                    "HP"
            );
            }
            case "ATK" -> {
                appleAttribute(entity,
                    ModAttributes.ATK_POINT.get(),
                    Attributes.ATTACK_DAMAGE,
                    value,
                    UUID.fromString("fc975381-d0ac-4684-a776-b53e80865d56"),
                    "ATK"
            );
            }
            case "DEF" -> {
                appleAttribute(entity,
                    ModAttributes.DEF_POINT.get(),
                    Attributes.ARMOR,
                    value,
                    UUID.fromString("5823e5ab-9c8f-437a-8ec5-656336fb73a9"),
                    "DEF"
            );
            }
            case "AP" -> {
                double now = entity.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();
                double next = now + value;
                entity.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(next);
            }
            case "GluttonyFood" -> {
                double now = entity.getAttribute(ModAttributes.GLUTTONY_FOOD.get()).getBaseValue();
                double next = now + value;

                AttributeModifier modifier = new AttributeModifier(UUID.fromString("9d4aa19b-88be-4216-b6bc-7e4276aa8c88"), "FOODATK", next, AttributeModifier.Operation.ADDITION);
                var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                attr.removeModifier(modifier.getId());
                attr.addPermanentModifier(modifier);

                AttributeModifier modifier2 = new AttributeModifier(UUID.fromString("9d7d004f-148c-4493-8f2f-53ee97febf66"), "FOODHP", next, AttributeModifier.Operation.ADDITION);
                var attr2 = entity.getAttribute(Attributes.MAX_HEALTH);
                attr2.removeModifier(modifier2.getId());
                attr2.addPermanentModifier(modifier2);
            }
            case "GluttonyEntity" -> {
                double now = entity.getAttribute(ModAttributes.GLUTTONY_ENTITY.get()).getBaseValue();
                double next = now + value;

                AttributeModifier modifier = new AttributeModifier(UUID.fromString("cb2e0860-2476-465a-a773-2bddf3d1944e"), "ENTITYATK", next, AttributeModifier.Operation.ADDITION);
                var attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                attr.removeModifier(modifier.getId());
                attr.addPermanentModifier(modifier);

                AttributeModifier modifier2 = new AttributeModifier(UUID.fromString("5890e162-f92b-4d27-b99b-58312c3db471"), "ENTITYHP", next, AttributeModifier.Operation.ADDITION);
                var attr2 = entity.getAttribute(Attributes.MAX_HEALTH);
                attr2.removeModifier(modifier2.getId());
                attr2.addPermanentModifier(modifier2);
            }
        }
    }

    private static void updateAttribute(LivingEntity player, Attribute targetAttr, double value, UUID uuid, String name) {
        AttributeModifier modifier = new AttributeModifier(uuid, name, value, AttributeModifier.Operation.ADDITION);
        var attr = player.getAttribute(targetAttr);
        attr.removeModifier(modifier.getId());
        attr.addPermanentModifier(modifier);
    }
    public static void setAttribute(LivingEntity player, String type, int value) {
        switch (type) {
            case "HP" -> {
                player.getAttribute(ModAttributes.HP_POINT.get()).setBaseValue(value);
                updateAttribute(player, Attributes.MAX_HEALTH, value, UUID.fromString("2467de73-f790-4cc1-941d-5b65afbffa10"), type);
            }
            case "ATK" -> {
                player.getAttribute(ModAttributes.ATK_POINT.get()).setBaseValue(value);
                updateAttribute(player, Attributes.ATTACK_DAMAGE, value, UUID.fromString("fc975381-d0ac-4684-a776-b53e80865d56"), type);
            }
            case "DEF" -> {
                player.getAttribute(ModAttributes.DEF_POINT.get()).setBaseValue(value);
                updateAttribute(player, Attributes.ARMOR, value, UUID.fromString("5823e5ab-9c8f-437a-8ec5-656336fb73a9"), type);
            }
            case "AP" -> {
                player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(value);
            }
        }
    }
    public static void appleAttribute(LivingEntity player, Attribute attrPoint, Attribute targetAttr, int value, UUID uuid, String name) {
        double now = player.getAttribute(attrPoint).getBaseValue();
        double next = now + value;
        player.getAttribute(attrPoint).setBaseValue(next);
        double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
        player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - value);

        updateAttribute(player, targetAttr, value, uuid, name);
    }
    public static void updateAttributeModifier(LivingEntity entity, String type) {
        switch (type) {
            case "HP" -> updateAttribute(entity,
                    Attributes.MAX_HEALTH,
                    entity.getAttributeBaseValue(ModAttributes.HP_POINT.get()),
                    UUID.fromString("2467de73-f790-4cc1-941d-5b65afbffa10"),
                    "HP"
            );
            case "ATK" -> updateAttribute(entity,
                    Attributes.ATTACK_DAMAGE,
                    entity.getAttributeBaseValue(ModAttributes.ATK_POINT.get()),
                    UUID.fromString("fc975381-d0ac-4684-a776-b53e80865d56"),
                    "ATK"
            );
            case "DEF" -> updateAttribute(entity,
                    Attributes.ARMOR,
                    entity.getAttributeBaseValue(ModAttributes.DEF_POINT.get()),
                    UUID.fromString("5823e5ab-9c8f-437a-8ec5-656336fb73a9"),
                    "DEF"
            );
            case "GFOOD" -> {
                updateAttribute(entity,
                    Attributes.MAX_HEALTH,
                    entity.getAttributeBaseValue(ModAttributes.GLUTTONY_FOOD.get()),
                    UUID.fromString("9d7d004f-148c-4493-8f2f-53ee97febf66"),
                    "FOODHP"
                );
                updateAttribute(entity,
                        Attributes.ATTACK_DAMAGE,
                        entity.getAttributeBaseValue(ModAttributes.GLUTTONY_FOOD.get()),
                        UUID.fromString("9d4aa19b-88be-4216-b6bc-7e4276aa8c88"),
                        "FOODATK"
                );
            }
            case "GENTITY" -> {
                updateAttribute(entity,
                        Attributes.MAX_HEALTH,
                        entity.getAttributeBaseValue(ModAttributes.GLUTTONY_ENTITY.get()) * 4,
                        UUID.fromString("5890e162-f92b-4d27-b99b-58312c3db4719"),
                        "ENTITYHP"
                );
                updateAttribute(entity,
                        Attributes.ATTACK_DAMAGE,
                        entity.getAttributeBaseValue(ModAttributes.GLUTTONY_ENTITY.get()) * 4,
                        UUID.fromString("cb2e0860-2476-465a-a773-2bddf3d1944e"),
                        "ENTITYATK"
                );
            }
        }
    }
}