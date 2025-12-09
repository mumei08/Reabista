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
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;


@OnlyIn(Dist.CLIENT)
public class RenderThaosvenom_2 extends RenderItemBase {
    private final ItemStack sheathModel;

    public RenderThaosvenom_2() {
        this.sheathModel = new ItemStack(ModItemWom.THAOSVENOM_HANDLE_2.get());
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
            float partialTicks
    ) {
        boolean isMainhand = hand == InteractionHand.MAIN_HAND;

        //---------------------------------------------
        // ★ 1. 武器本体（右手）
        //---------------------------------------------
        OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);

        Joint handJoint = isMainhand ? armature.toolR : armature.toolL;
        OpenMatrix4f jointTransform = poses[handJoint.getId()];

        modelMatrix.mulFront(jointTransform);

        poseStack.pushPose();
        this.mulPoseStack(poseStack, modelMatrix);

        ItemDisplayContext transform = isMainhand ?
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND :
                ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                transform,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                (Level)null,
                0
        );
        poseStack.popPose();


        //---------------------------------------------
        // ★ 2. 鞘モデル（左手側）
        //---------------------------------------------
        modelMatrix = this.getCorrectionMatrix(sheathModel, entitypatch, hand);

        // Moonless と同じく「handR/handL」の Joint を使う
        Joint handJoint2 = isMainhand ? armature.handR : armature.handL;
        OpenMatrix4f jointTransform2 = poses[handJoint2.getId()];

        modelMatrix.mulFront(jointTransform2);

        // Moonless と同じ補正（必要なら後で調整）
        modelMatrix.translate(-0.001F, -0.02F, -0.08F);

        poseStack.pushPose();
        this.mulPoseStack(poseStack, modelMatrix);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                sheathModel,
                transform,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                (Level)null,
                0
        );
        poseStack.popPose();
    }
}