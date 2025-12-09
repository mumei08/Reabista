package kaede.reabista.weapons.capability.item;


import kaede.reabista.weapons.gameasset.ReabistaWeaponColliders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WOMSkillDataKeys;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;

@Mod.EventBusSubscriber(
        modid = "reabista",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ReabistaWeaponCapabilityPresets {
    public static final Function<Item, CapabilityItem.Builder> THEUSFALL_1 = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder()
                .category(CapabilityItem.WeaponCategories.SPEAR)
                .styleProvider((entitypatch) -> entitypatch instanceof PlayerPatch && ((PlayerPatch)entitypatch)
                        .getSkill(SkillSlots.WEAPON_INNATE)
                        .getRemainDuration() > 0 ? CapabilityItem.Styles.OCHS : CapabilityItem.Styles.TWO_HAND)
                .collider(ReabistaWeaponColliders.THEUSFALL)
                .hitSound((SoundEvent) EpicFightSounds.BLADE_HIT.get())
                .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
                .passiveSkill(WOMSkills.DEMON_MARK_PASSIVE)
                .canBePlacedOffhand(false)
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND, new StaticAnimation[]{
                        WOMAnimations.AGONY_AUTO_1,
                        WOMAnimations.AGONY_AUTO_2,
                        WOMAnimations.AGONY_AUTO_3,
                        WOMAnimations.AGONY_AUTO_4,
                        WOMAnimations.AGONY_CLAWSTRIKE,
                        WOMAnimations.AGONY_RIPPING_FANGS})
                .newStyleCombo(CapabilityItem.Styles.MOUNT, new StaticAnimation[]{
                        Animations.SPEAR_MOUNT_ATTACK})
                .newStyleCombo(CapabilityItem.Styles.OCHS, new StaticAnimation[]{
                        WOMAnimations.ANTITHEUS_ASCENDED_AUTO_1,
                        WOMAnimations.ANTITHEUS_ASCENDED_AUTO_2,
                        WOMAnimations.ANTITHEUS_ASCENDED_AUTO_3,
                        WOMAnimations.ANTITHEUS_ASCENDED_BLINK,
                        WOMAnimations.ANTITHEUS_ASCENDED_DEATHFALL})
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.DEMONIC_ASCENSION)
                .innateSkill(CapabilityItem.Styles.OCHS, (itemstack) -> WOMSkills.DEMONIC_ASCENSION)
                .comboCancel((style) -> false)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, WOMAnimations.AGONY_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, WOMAnimations.AGONY_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, WOMAnimations.AGONY_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, WOMAnimations.AGONY_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, WOMAnimations.AGONY_GUARD)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.IDLE, WOMAnimations.ANTITHEUS_ASCENDED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.WALK, WOMAnimations.ANTITHEUS_ASCENDED_WALK)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.CHASE, WOMAnimations.ANTITHEUS_ASCENDED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.RUN, WOMAnimations.ANTITHEUS_ASCENDED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.SWIM, Animations.BIPED_SWIM);
        return builder;
    };
    public static final Function<Item, CapabilityItem.Builder> THEUSFALL_2 = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder()
                .category(CapabilityItem.WeaponCategories.SPEAR)
                .styleProvider((playerpatch) -> CapabilityItem.Styles.TWO_HAND)
                .collider(ReabistaWeaponColliders.THEUSFALL)
                .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
                .canBePlacedOffhand(false)
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND, new StaticAnimation[]{
                        WOMAnimations.ANTITHEUS_AUTO_1,
                        WOMAnimations.ANTITHEUS_AUTO_2,
                        WOMAnimations.ANTITHEUS_AUTO_3,
                        WOMAnimations.ANTITHEUS_AUTO_4,
                        WOMAnimations.ANTITHEUS_AGRESSION,
                        WOMAnimations.ANTITHEUS_GUILLOTINE})
                .newStyleCombo(CapabilityItem.Styles.MOUNT, new StaticAnimation[]{
                        Animations.SPEAR_MOUNT_ATTACK})
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.AGONY_PLUNGE)
                .comboCancel((style) -> false)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, WOMAnimations.ANTITHEUS_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, WOMAnimations.ANTITHEUS_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, WOMAnimations.ANTITHEUS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, WOMAnimations.ANTITHEUS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, WOMAnimations.AGONY_GUARD);
        return builder;
    };
    public static final Function<Item, CapabilityItem.Builder> THAOSVENOM_1 = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder()
                .category(CapabilityItem.WeaponCategories.UCHIGATANA)
                .styleProvider((entitypatch) -> {
            if (entitypatch instanceof PlayerPatch<?> playerpatch) {
                if (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE)
                        .getDataManager().hasData((SkillDataKey) WOMSkillDataKeys.SHEATH.get()) && (Boolean)playerpatch
                        .getSkill(SkillSlots.WEAPON_PASSIVE)
                        .getDataManager()
                        .getDataValue((SkillDataKey)WOMSkillDataKeys.SHEATH.get())) {
                    return CapabilityItem.Styles.SHEATH;
                }
            }
            return CapabilityItem.Styles.TWO_HAND;
        })
                .passiveSkill(WOMSkills.SATSUJIN_PASSIVE)
                .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
                .collider(ReabistaWeaponColliders.THAOSVENOM)
                .canBePlacedOffhand(false)
                .newStyleCombo(CapabilityItem.Styles.SHEATH, new StaticAnimation[]{
                        WOMAnimations.KATANA_SHEATHED_AUTO_1,
                        WOMAnimations.KATANA_SHEATHED_AUTO_2,
                        WOMAnimations.KATANA_SHEATHED_AUTO_3,
                        WOMAnimations.KATANA_SHEATHED_DASH,
                        WOMAnimations.KATANA_TSUKUYOMI})
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND, new StaticAnimation[]{
                        WOMAnimations.KATANA_AUTO_1,
                        WOMAnimations.KATANA_AUTO_2,
                        WOMAnimations.KATANA_AUTO_3,
                        WOMAnimations.KATANA_DASH,
                        WOMAnimations.KATANA_TSUKUYOMI})
                .newStyleCombo(CapabilityItem.Styles.MOUNT, new StaticAnimation[]{
                        Animations.SWORD_MOUNT_ATTACK})
                .innateSkill(CapabilityItem.Styles.SHEATH, (itemstack) -> WOMSkills.SAKURA_STATE)
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.SAKURA_STATE)
                .comboCancel((style) -> false)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, WOMAnimations.KATANA_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.KNEEL, WOMAnimations.KATANA_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, WOMAnimations.KATANA_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, WOMAnimations.KATANA_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, WOMAnimations.KATANA_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SNEAK, WOMAnimations.KATANA_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, WOMAnimations.KATANA_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.FLOAT, WOMAnimations.KATANA_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.FALL, WOMAnimations.KATANA_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.IDLE, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.KNEEL, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.WALK, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.CHASE, WOMAnimations.KATANA_SHEATHED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.RUN, WOMAnimations.KATANA_SHEATHED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.SNEAK, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.SWIM, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.FLOAT, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.FALL, WOMAnimations.KATANA_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, WOMAnimations.KATANA_GUARD);
        return builder;
    };
    public static final Function<Item, CapabilityItem.Builder> THAOSVENOM_2 = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder().category(CapabilityItem.WeaponCategories.TACHI).styleProvider((entitypatch) -> {
            if (entitypatch instanceof PlayerPatch<?> playerpatch) {
                if (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE)
                        .getDataManager()
                        .hasData((SkillDataKey)WOMSkillDataKeys.VERSO.get()) && (Boolean)playerpatch
                        .getSkill(SkillSlots.WEAPON_PASSIVE)
                        .getDataManager()
                        .getDataValue((SkillDataKey)WOMSkillDataKeys.VERSO.get())) {
                    return CapabilityItem.Styles.OCHS;
                }
            }

            return CapabilityItem.Styles.TWO_HAND;
        })
                .collider(ReabistaWeaponColliders.THAOSVENOM)
                .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
                .comboCancel((style) -> false)
                .canBePlacedOffhand(false)
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND, new StaticAnimation[]{
                        WOMAnimations.MOONLESS_AUTO_1,
                        WOMAnimations.MOONLESS_AUTO_2,
                        WOMAnimations.MOONLESS_AUTO_3,
                        WOMAnimations.MOONLESS_REVERSED_BYPASS,
                        WOMAnimations.MOONLESS_CRESCENT})
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.AGONY_PLUNGE)
                .newStyleCombo(CapabilityItem.Styles.OCHS, new StaticAnimation[]{
                        WOMAnimations.MOONLESS_AUTO_1_VERSO,
                        WOMAnimations.MOONLESS_AUTO_2_VERSO,
                        WOMAnimations.MOONLESS_AUTO_3_VERSO,
                        WOMAnimations.MOONLESS_BYPASS,
                        WOMAnimations.MOONLESS_FULLMOON})
                .innateSkill(CapabilityItem.Styles.OCHS, (itemstack) -> WOMSkills.AGONY_PLUNGE)
                .newStyleCombo(CapabilityItem.Styles.MOUNT, new StaticAnimation[]{
                        Animations.SWORD_MOUNT_ATTACK})
                .passiveSkill(WOMSkills.LUNAR_ECHO_PASSIVE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, WOMAnimations.MOONLESS_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, WOMAnimations.MOONLESS_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, WOMAnimations.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, WOMAnimations.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, WOMAnimations.MOONLESS_GUARD)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.IDLE, WOMAnimations.MOONLESS_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.WALK, WOMAnimations.MOONLESS_WALK)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.CHASE, WOMAnimations.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.RUN, WOMAnimations.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.BLOCK, WOMAnimations.MOONLESS_GUARD);
        return builder;
    };

    @SubscribeEvent
    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        event.getTypeEntry().put(new ResourceLocation("reabista", "theusfall_1"), THEUSFALL_1);
        event.getTypeEntry().put(new ResourceLocation("reabista", "theusfall_2"), THEUSFALL_2);
        event.getTypeEntry().put(new ResourceLocation("reabista", "thaosvenom_1"), THAOSVENOM_1);
        event.getTypeEntry().put(new ResourceLocation("reabista", "thaosvenom_2"), THAOSVENOM_2);
    }
}
