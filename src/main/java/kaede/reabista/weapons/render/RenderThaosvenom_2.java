package kaede.reabista.weapons.render;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import kaede.reabista.weapons.item.ModItemWom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.armature.ArmedToolHolderArmature;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.model.armature.types.ToolHolderArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderThaosvenom_2 extends RenderItemBase {

    /** 第二モデル（柄・補助武装など） */
    private final ItemStack secondModel;

    public RenderThaosvenom_2(JsonElement jsonElement) {
        super(jsonElement);
        this.secondModel = new ItemStack((ItemLike) ModItemWom.THAOSVENOM_HANDLE_2.get());
    }

    @Override
    public void renderItemInHand(
            ItemStack mainStack,
            LivingEntityPatch<?> entityPatch,
            InteractionHand hand,
            OpenMatrix4f[] poses,
            MultiBufferSource buffer,
            PoseStack poseStack,
            int packedLight,
            float partialTicks
    ) {

        Armature armatureBase = entityPatch.getArmature();

        // ToolHolderArmature を持つエンティティのみ処理
        if (!(armatureBase instanceof ToolHolderArmature armature)) {
            return;
        }

        boolean isMainHand = hand == InteractionHand.MAIN_HAND;

        /* ===== メインモデル描画 ===== */

        Joint toolJoint =
                isMainHand ? armature.rightToolJoint() : armature.leftToolJoint();

        OpenMatrix4f modelMatrix =
                new OpenMatrix4f(this.mainhandCorrectionTransforms.get("Tool_R"));

        OpenMatrix4f toolJointPose = poses[toolJoint.getId()];
        modelMatrix.mulFront(toolJointPose);

        poseStack.pushPose();
        MathUtils.mulStack(poseStack, modelMatrix);

        ItemDisplayContext displayContext =
                isMainHand
                        ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                        : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(
                        mainStack,
                        displayContext,
                        255,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        buffer,
                        (Level) null,
                        0
                );

        poseStack.popPose();

        /* ===== 第二モデル用：手のJoint取得 ===== */

        Joint handJoint = null;

        if (armatureBase instanceof HumanoidArmature humanoid) {
            handJoint = isMainHand ? humanoid.handR : humanoid.handL;
        }

        if (armatureBase instanceof ArmedToolHolderArmature armed) {
            handJoint = isMainHand ? armed.rightHandJoint() : armed.leftHandJoint();
        }

        if (handJoint == null) {
            return;
        }

        /* ===== 第二モデル描画 ===== */

        OpenMatrix4f handJointPose = poses[handJoint.getId()];

        modelMatrix.load(this.mainhandCorrectionTransforms.get("Tool_R"));
        modelMatrix.mulFront(handJointPose);

        // 微調整オフセット
        modelMatrix.translate(-0.001F, -0.02F, -0.08F);

        poseStack.pushPose();
        MathUtils.mulStack(poseStack, modelMatrix);

        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(
                        this.secondModel,
                        displayContext,
                        255,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        buffer,
                        (Level) null,
                        0
                );

        poseStack.popPose();
    }
}