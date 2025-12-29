package kaede.reabista.weapons.item.theusfall;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.world.item.EpicFightItemTier;
import yesman.epicfight.world.item.WeaponItem;

import javax.annotation.Nullable;
import java.util.List;

public class Theusfall_2 extends WeaponItem {
    private float attackDamage;
    private double attackSpeed;

    public Theusfall_2(Item.Properties build) {
        super(EpicFightItemTier.UCHIGATANA, 0, -1.5F, build.defaultDurability(6666));
    }

    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == WOMItems.DEMON_SEAL.get();
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, List<Component> components, @NotNull TooltipFlag flagIn) {
        components.add(Component.literal(""));
        components.add(Component.translatable("item.reabista.Theusfall.tooltip"));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            long now = attacker.level().getGameTime();
            CompoundTag tag = stack.getOrCreateTag();

            long lastHit = tag.getLong("last_hit_time");
            if (now - lastHit > 3000) { // 10分
                tag.putInt("hit_count", 0);
            }

            int hits = tag.getInt("hit_count") + 1;
            tag.putInt("hit_count", hits);
            tag.putLong("last_hit_time", now);

            // デバフ
            target.addEffect(new MobEffectInstance(
                    MobEffects.WITHER, 100, 5, false, false
            ));
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
