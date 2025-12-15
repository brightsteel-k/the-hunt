package net.br1ghtsteel.thehunt.item;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item TORIVOR_SPAWN_EGG = registerItem("torivor_spawn_egg",
            new SpawnEggItem(ModEntities.TORIVOR_ENTITY, 0x242424, 0xab31ab, new FabricItemSettings()));

    private static void addItemsToSpawnEggItemGroup(FabricItemGroupEntries entries) {
        entries.add(TORIVOR_SPAWN_EGG);
    }

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(TheHunt.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TheHunt.LOGGER.info("Registering Mod Items for: " + TheHunt.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addItemsToSpawnEggItemGroup);
    }
}
