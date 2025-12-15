package net.br1ghtsteel.thehunt;

import net.br1ghtsteel.thehunt.entity.ModEntities;
import net.br1ghtsteel.thehunt.entity.client.ModModelLayers;
import net.br1ghtsteel.thehunt.entity.client.torivor.TorivorEntityRenderer;
import net.br1ghtsteel.thehunt.entity.client.torivor.TorivorEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class TheHuntClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.TORIVOR_ENTITY, TorivorEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TORIVOR, TorivorEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TORIVOR_OUTER, TorivorEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TORIVOR_INNER_ARMOR, TorivorEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TORIVOR_OUTER_ARMOR, TorivorEntityModel::getTexturedModelData);
    }
}
