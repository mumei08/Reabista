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
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderThaosvenom_1 extends RenderItemBase {

    /** 表示専用：鞘 */
    private final ItemStack sheathStack;

    public RenderThaosvenom_1(JsonElement jsonElement) {
        super(jsonElement);
        this.sheathStack = new ItemStack((ItemLike) ModItemWom.THAOSVENOM_SHEATH_1.get());
    }

    @Override
    public void renderItemInHand(
            ItemStack weaponStack,
            LivingEntityPatch<?> entityPatch,
            InteractionHand hand,
            OpenMatrix4f[] poses,
            MultiBufferSource buffer,
            PoseStack poseStack,
            int packedLight,
            float partialTicks
    ) {

        /* ===== メインハンド：武器本体 ===== */

        OpenMatrix4f mainHandMatrix =
                this.getCorrectionMatrix(entityPatch, InteractionHand.MAIN_HAND, poses);

        poseStack.pushPose();
        MathUtils.mulStack(poseStack, mainHandMatrix);

        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(
                        weaponStack,
                        ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        buffer,
                        (Level) null,
                        0
                );

        poseStack.popPose();

        /* ===== オフハンド位置：鞘 ===== */

        OpenMatrix4f offHandMatrix =
                this.getCorrectionMatrix(entityPatch, InteractionHand.OFF_HAND, poses);

        poseStack.pushPose();
        MathUtils.mulStack(poseStack, offHandMatrix);

        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(
                        this.sheathStack,
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