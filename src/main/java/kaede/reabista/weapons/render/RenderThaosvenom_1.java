package kaede.reabista.weapons.render;

import com.mojang.blaze3d.vertex.PoseStack;
import kaede.reabista.weapons.item.ModItemWom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderThaosvenom_1 extends RenderItemBase {

    private final ItemStack sheathStack;

    public RenderThaosvenom_1() {
        this.sheathStack = new ItemStack(ModItemWom.THAOSVENOM_SHEATH_1.get());
    }

    @Override
    public void renderItemInHand(
            ItemStack stack,
            LivingEntityPatch<?> entitypatch,
            InteractionHand hand,
            HumanoidArmature armature,
            OpenMatrix4f[] poses,
            MultiBufferSource buffer,
            PoseStack poseStack,
            int packedLight,
            float partialTicks) {

        // 右手（武器）
        OpenMatrix4f mat = new OpenMatrix4f(this.mainhandcorrectionMatrix);
        mat.mulFront(poses[armature.toolR.getId()]);
        poseStack.pushPose();
        this.mulPoseStack(poseStack, mat);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                (Level) null,
                0
        );
        poseStack.popPose();

        // 左手（鞘）
        mat = new OpenMatrix4f(this.mainhandcorrectionMatrix);
        mat.mulFront(poses[armature.toolL.getId()]);
        poseStack.pushPose();
        this.mulPoseStack(poseStack, mat);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                sheathStack,
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                (Level) null,
                0
        );

        poseStack.popPose();
    }
}