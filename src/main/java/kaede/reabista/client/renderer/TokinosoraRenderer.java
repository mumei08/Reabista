package kaede.reabista.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import kaede.reabista.entity.TokinosoraEntity;

public class TokinosoraRenderer extends HumanoidMobRenderer<TokinosoraEntity, HumanoidModel<TokinosoraEntity>> {
    public TokinosoraRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<TokinosoraEntity>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(TokinosoraEntity entity) {
        return new ResourceLocation("reabista:textures/entities/tokinosorach.png");
    }
}
