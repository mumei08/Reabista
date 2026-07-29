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

    private final ItemStack Secondmodel;

    public RenderThaosvenom_2(JsonElement jsonElement) {
        super(jsonElement);
        this.Secondmodel = new ItemStack((ItemLike) ModItemWom.THAOSVENOM_HANDLE_2.get());
    }

    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        Armature var10 = entitypatch.getArmature();
        if (var10 instanceof ToolHolderArmature armature) {
            OpenMatrix4f modelMatrix = new OpenMatrix4f((OpenMatrix4f)this.mainhandCorrectionTransforms.get("Tool_R"));
            boolean isInMainhand = hand == InteractionHand.MAIN_HAND;
            Joint holdingHand = isInMainhand ? armature.rightToolJoint() : armature.leftToolJoint();
            OpenMatrix4f jointTransform = poses[holdingHand.getId()];
            modelMatrix.mulFront(jointTransform);
            poseStack.pushPose();
            MathUtils.mulStack(poseStack, modelMatrix);
            ItemDisplayContext transformType = isInMainhand ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, transformType, 255, OverlayTexture.NO_OVERLAY, poseStack, buffer, (Level)null, 0);
            poseStack.popPose();
            Joint holdingHand2 = null;
            Armature var17 = entitypatch.getArmature();
            if (var17 instanceof HumanoidArmature armature2) {
                holdingHand2 = isInMainhand ? armature2.handR : armature2.handL;
            }

            var17 = entitypatch.getArmature();
            if (var17 instanceof ArmedToolHolderArmature armature2) {
                holdingHand2 = isInMainhand ? armature2.rightHandJoint() : armature2.leftHandJoint();
            }

            if (holdingHand2 != null) {
                OpenMatrix4f jointTransform2 = poses[holdingHand2.getId()];
                modelMatrix.load((OpenMatrix4f)this.mainhandCorrectionTransforms.get("Tool_R"));
                modelMatrix.mulFront(jointTransform2);
                modelMatrix.translate(-0.001F, -0.02F, -0.08F);
                poseStack.pushPose();
                MathUtils.mulStack(poseStack, modelMatrix);
                Minecraft.getInstance().getItemRenderer().renderStatic(this.Secondmodel, transformType, 255, OverlayTexture.NO_OVERLAY, poseStack, buffer, (Level)null, 0);
                poseStack.popPose();
            }
        }

    }
}