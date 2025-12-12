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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.item.EpicFightItemTier;
import yesman.epicfight.world.item.WeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Theusfall_2 extends WeaponItem {
    @OnlyIn(Dist.CLIENT)
    private List<Component> tooltipExpand;
    private float attackDamage;
    private double attackSpeed;

    public Theusfall_2(Properties build) {
        super(EpicFightItemTier.UCHIGATANA, 0, -1.5F, build);
        if (EpicFightMod.isPhysicalClient()) {
            this.tooltipExpand = new ArrayList();
            this.tooltipExpand.add(Component.literal(""));
            this.tooltipExpand.add(Component.translatable("item.reabista.Theusfall.tooltip"));
        }

        this.attackDamage = 10.0F;
        this.attackSpeed = (double)-1.5F;
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Items.NETHERITE_INGOT;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                            "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));

            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                            "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

            return builder.build();
        }

        return super.getAttributeModifiers(slot, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        if (tooltipExpand != null) {
            tooltip.addAll(tooltipExpand);
        }
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
