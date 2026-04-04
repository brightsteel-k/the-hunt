package net.br1ghtsteel.thehunt.item;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item TORIVOR_SPAWN_EGG = registerItem("torivor_spawn_egg",
            new SpawnEggItem(ModEntities.TORIVOR_ENTITY, 0x242424, 0xab31ab, new FabricItemSettings()));
    public static final Item TORIVORIAN_SWORD = registerItem("torivorian_sword",
            new SwordItem(ModToolMaterials.TORIVORIAN, 3, -2.4F, new FabricItemSettings().rarity(Rarity.EPIC)));

    private static void addItemsToSpawnEggItemGroup(FabricItemGroupEntries entries) {
        entries.add(TORIVOR_SPAWN_EGG);
    }
    private static void addItemsToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.add(TORIVORIAN_SWORD);
    }

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(TheHunt.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TheHunt.LOGGER.info("Registering Mod Items for: " + TheHunt.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addItemsToSpawnEggItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatItemGroup);
    }
}
