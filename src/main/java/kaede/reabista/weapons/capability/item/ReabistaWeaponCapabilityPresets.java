package kaede.reabista.weapons.capability.item;


import kaede.reabista.weapons.gameasset.ReabistaWeaponColliders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import reascer.wom.gameasset.animations.weapons.AnimsSatsujin;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.main.WeaponsOfMinecraft;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import yesman.epicfight.api.animation.AnimationManager;
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
                .styleProvider((entitypatch) ->
                        entitypatch instanceof PlayerPatch && ((PlayerPatch)entitypatch)
                                .getSkill(SkillSlots.WEAPON_INNATE)
                                .getRemainDuration() > 0 ? CapabilityItem.Styles.OCHS :
                                CapabilityItem.Styles.TWO_HAND)
                .collider(ReabistaWeaponColliders.THEUSFALL)
                .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
                .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
                .passiveSkill(WOMSkills.DEMON_MARK_PASSIVE)
                .canBePlacedOffhand(false)
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND,
                        new AnimationManager.AnimationAccessor[]{
                                AnimsAgony.AGONY_AUTO_1,
                                AnimsAgony.AGONY_AUTO_2,
                                AnimsAgony.AGONY_AUTO_3,
                                AnimsAgony.AGONY_AUTO_4,
                                AnimsAgony.AGONY_CLAWSTRIKE,
                                AnimsAgony.AGONY_RIPPING_FANGS})
                .newStyleCombo(CapabilityItem.Styles.OCHS,
                        new AnimationManager.AnimationAccessor[]{
                                WOMAnimations.ANTITHEUS_ASCENDED_AUTO_1,
                                WOMAnimations.ANTITHEUS_ASCENDED_AUTO_2,
                                WOMAnimations.ANTITHEUS_ASCENDED_AUTO_3,
                                WOMAnimations.ANTITHEUS_ASCENDED_BLINK,
                                WOMAnimations.ANTITHEUS_ASCENDED_DEATHFALL})
                .newStyleCombo(CapabilityItem.Styles.MOUNT,
                        new AnimationManager.AnimationAccessor[]{
                                Animations.SPEAR_MOUNT_ATTACK})
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.DEMONIC_ASCENSION)
                .innateSkill(CapabilityItem.Styles.OCHS, (itemstack) -> WOMSkills.DEMONIC_ASCENSION)
                .comboCancel((style) -> false)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, AnimsAgony.AGONY_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, AnimsAgony.AGONY_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, AnimsAgony.AGONY_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, AnimsAgony.AGONY_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, AnimsAgony.AGONY_GUARD)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.IDLE, WOMAnimations.ANTITHEUS_ASCENDED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.WALK, WOMAnimations.ANTITHEUS_ASCENDED_WALK)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.CHASE, WOMAnimations.ANTITHEUS_ASCENDED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.RUN, WOMAnimations.ANTITHEUS_ASCENDED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.SWIM, Animations.BIPED_SWIM)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.BLOCK, AnimsAgony.AGONY_GUARD);
        return builder;
    };
    public static final Function<Item, CapabilityItem.Builder> THEUSFALL_2 = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder()
                .category(CapabilityItem.WeaponCategories.SPEAR)
                .styleProvider((playerpatch) ->
                        CapabilityItem.Styles.TWO_HAND)
                .collider(ReabistaWeaponColliders.THEUSFALL)
                .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
                .canBePlacedOffhand(false)
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND,
                        new AnimationManager.AnimationAccessor[]{
                                WOMAnimations.ANTITHEUS_AUTO_1,
                                WOMAnimations.ANTITHEUS_AUTO_2,
                                WOMAnimations.ANTITHEUS_AUTO_3,
                                WOMAnimations.ANTITHEUS_AUTO_4,
                                WOMAnimations.ANTITHEUS_AGRESSION,
                                WOMAnimations.ANTITHEUS_GUILLOTINE})
                .newStyleCombo(CapabilityItem.Styles.MOUNT,
                        new AnimationManager.AnimationAccessor[]{
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
                        LivingMotions.BLOCK, AnimsAgony.AGONY_GUARD);
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
                .newStyleCombo(CapabilityItem.Styles.SHEATH,
                        new AnimationManager.AnimationAccessor[]{
                                AnimsSatsujin.SATSUJIN_SHEATHED_1,
                                AnimsSatsujin.SATSUJIN_SHEATHED_2,
                                AnimsSatsujin.SATSUJIN_SHEATHED_3,
                                AnimsSatsujin.SATSUJIN_SHEATHED_DASH,
                                AnimsSatsujin.SATSUJIN_TSUKUYOMI})
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND,
                        new AnimationManager.AnimationAccessor[]{
                                AnimsSatsujin.SATSUJIN_AUTO_1,
                                AnimsSatsujin.SATSUJIN_AUTO_2,
                                AnimsSatsujin.SATSUJIN_AUTO_3,
                                AnimsSatsujin.SATSUJIN_HARUSAKI,
                                AnimsSatsujin.SATSUJIN_TSUKUYOMI})
                .newStyleCombo(CapabilityItem.Styles.MOUNT, new AnimationManager.AnimationAccessor[]{
                        Animations.SWORD_MOUNT_ATTACK})
                .innateSkill(CapabilityItem.Styles.SHEATH, (itemstack) -> WOMSkills.SAKURA_STATE)
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.SAKURA_STATE)
                .comboCancel((style) -> false)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, AnimsSatsujin.SATSUJIN_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.KNEEL, AnimsSatsujin.SATSUJIN_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, AnimsSatsujin.SATSUJIN_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, AnimsSatsujin.SATSUJIN_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, AnimsSatsujin.SATSUJIN_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SNEAK, AnimsSatsujin.SATSUJIN_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, AnimsSatsujin.SATSUJIN_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.FLOAT, AnimsSatsujin.SATSUJIN_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.FALL, AnimsSatsujin.SATSUJIN_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.IDLE, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.KNEEL, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.WALK, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.CHASE, AnimsSatsujin.SATSUJIN_SHEATHED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.RUN, AnimsSatsujin.SATSUJIN_SHEATHED_RUN)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.SNEAK, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.SWIM, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.FLOAT, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.SHEATH,
                        LivingMotions.FALL, AnimsSatsujin.SATSUJIN_SHEATHED_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, AnimsSatsujin.SATSUJIN_GUARD);
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
                .newStyleCombo(CapabilityItem.Styles.TWO_HAND, new AnimationManager.AnimationAccessor[]{
                        AnimsMoonless.MOONLESS_AUTO_1,
                        AnimsMoonless.MOONLESS_AUTO_2,
                        AnimsMoonless.MOONLESS_AUTO_3,
                        AnimsMoonless.MOONLESS_REVERSED_BYPASS,
                        AnimsMoonless.MOONLESS_CRESCENT})
                .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> WOMSkills.AGONY_PLUNGE)
                .newStyleCombo(CapabilityItem.Styles.OCHS, new AnimationManager.AnimationAccessor[]{
                        AnimsMoonless.MOONLESS_AUTO_1_VERSO,
                        AnimsMoonless.MOONLESS_AUTO_2_VERSO,
                        AnimsMoonless.MOONLESS_AUTO_3_VERSO,
                        AnimsMoonless.MOONLESS_BYPASS,
                        AnimsMoonless.MOONLESS_FULLMOON})
                .innateSkill(CapabilityItem.Styles.OCHS, (itemstack) -> WOMSkills.AGONY_PLUNGE)
                .newStyleCombo(CapabilityItem.Styles.MOUNT, new AnimationManager.AnimationAccessor[]{
                        Animations.SWORD_MOUNT_ATTACK})
                .passiveSkill(WOMSkills.LUNAR_ECHO_PASSIVE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.IDLE, AnimsMoonless.MOONLESS_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.WALK, AnimsMoonless.MOONLESS_WALK)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.CHASE, AnimsMoonless.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.RUN, AnimsMoonless.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.TWO_HAND,
                        LivingMotions.BLOCK, AnimsMoonless.MOONLESS_GUARD)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.IDLE, AnimsMoonless.MOONLESS_IDLE)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.WALK, AnimsMoonless.MOONLESS_WALK)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.CHASE, AnimsMoonless.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.RUN, AnimsMoonless.MOONLESS_RUN)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
                .livingMotionModifier(CapabilityItem.Styles.OCHS,
                        LivingMotions.BLOCK, AnimsMoonless.MOONLESS_GUARD);
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
