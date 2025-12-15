package net.br1ghtsteel.thehunt.entity.client;

import net.br1ghtsteel.thehunt.TheHunt;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {

    public static final EntityModelLayer TORIVOR = register("torivor");
    public static final EntityModelLayer TORIVOR_OUTER = register("torivor", "outer");
    public static final EntityModelLayer TORIVOR_INNER_ARMOR = register("torivor", "inner_armor");
    public static final EntityModelLayer TORIVOR_OUTER_ARMOR = register("torivor", "outer_armor");

    private static EntityModelLayer register(String id) {
        return register(id, "main");
    }

    private static EntityModelLayer register(String id, String type) {
        return new EntityModelLayer(new Identifier(TheHunt.MOD_ID, id), type);
    }
}
