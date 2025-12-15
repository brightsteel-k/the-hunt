package net.br1ghtsteel.thehunt.entity.client.torivor;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.TorivorEntity;
import net.br1ghtsteel.thehunt.entity.client.HunterEntityModel;
import net.br1ghtsteel.thehunt.entity.client.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TorivorOverlayFeatureRenderer<T extends TorivorEntity> extends FeatureRenderer<T, HunterEntityModel<T>> {
    private static final Identifier SKIN = new Identifier(TheHunt.MOD_ID, "textures/entity/torivor_outer_layer.png");
    private final HunterEntityModel<T> model;

    public TorivorOverlayFeatureRenderer(FeatureRendererContext<T, HunterEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        this.model = new HunterEntityModel<>(loader.getModelPart(ModModelLayers.TORIVOR_OUTER));
    }

    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T torivorEntity, float f, float g, float h, float j, float k, float l
    ) {
        render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, torivorEntity, f, g, j, k, l, h, 1.0F, 1.0F, 1.0F);
    }
}