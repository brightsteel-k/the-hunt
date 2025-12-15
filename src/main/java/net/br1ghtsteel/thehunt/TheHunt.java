package net.br1ghtsteel.thehunt;

import net.br1ghtsteel.thehunt.entity.ModEntities;
import net.br1ghtsteel.thehunt.entity.TorivorEntity;
import net.br1ghtsteel.thehunt.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheHunt implements ModInitializer {

    public static final String MOD_ID = "thehunt";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ModItems.registerModItems();

		FabricDefaultAttributeRegistry.register(ModEntities.TORIVOR_ENTITY, TorivorEntity.createTorivorAttributes());
	}

	public static void sendChatMessage(String message) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.inGameHud.getChatHud().addMessage(Text.literal(message));
	}
}