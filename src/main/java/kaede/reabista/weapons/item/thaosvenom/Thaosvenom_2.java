package kaede.reabista.weapons.item.thaosvenom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.item.EpicFightItemTier;
import yesman.epicfight.world.item.WeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Thaosvenom_2 extends WeaponItem {

    private final float attackDamage = 10.0F;
    private final double attackSpeed = -1.5F;

    public Thaosvenom_2(Item.Properties build) {
        super(EpicFightItemTier.UCHIGATANA, 0, -1.5F, build);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Items.NETHERITE_INGOT;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            builder.put(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION)
            );
            builder.put(
                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION)
            );

            return builder.build();
        }

        return super.getAttributeModifiers(slot, stack);
    }

    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> components, @NotNull TooltipFlag flagIn) {
        components.add(Component.literal(""));
        components.add(Component.translatable("item.reabista.thaosvenom.tooltip"));
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
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}