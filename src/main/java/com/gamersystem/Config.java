package com.gamersystem;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuracion comun (config/gamersystem-common.toml).
 * Todo lo que usa mobs vanilla se define aqui con IDs, asi se puede cambiar sin tocar codigo
 * (y tambien aceptar mobs de otros mods: "modid:mob").
 */
public class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue ZONE_RADIUS = B
            .comment("Radio (en bloques) de las zonas protegidas / de entrenamiento.")
            .defineInRange("zoneRadius", 12, 2, 64);

    public static final ModConfigSpec.IntValue MAX_TRAINING_MOBS = B
            .comment("Maximo de enemigos de entrenamiento vivos a la vez por zona.")
            .defineInRange("maxTrainingMobs", 6, 1, 30);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> TRAINING_MOBS = B
            .comment("Tipos de enemigo que se pueden elegir en una zona de entrenamiento (Shift + clic derecho en el Nucleo).")
            .defineListAllowEmpty("trainingMobs",
                    List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:spider",
                            "minecraft:husk", "minecraft:stray"),
                    () -> "minecraft:zombie",
                    o -> o instanceof String s && s.contains(":"));

    public static final ModConfigSpec.ConfigValue<List<? extends String>> DUNGEON_FLOORS = B
            .comment("Pisos de la mazmorra. Formato: \"piso=mob1,mob2,...\". Admite mobs vanilla y de otros mods.")
            .defineListAllowEmpty("dungeonFloors",
                    List.of(
                            "1=minecraft:zombie,minecraft:skeleton",
                            "2=minecraft:spider,minecraft:cave_spider",
                            "3=minecraft:husk,minecraft:stray,minecraft:drowned",
                            "4=minecraft:pillager,minecraft:vindicator,minecraft:witch",
                            "5=minecraft:blaze,minecraft:wither_skeleton,minecraft:piglin_brute",
                            "6=minecraft:ravager,minecraft:evoker",
                            "7=minecraft:warden"),
                    () -> "1=minecraft:zombie",
                    o -> o instanceof String s && s.contains("="));

    public static final ModConfigSpec SPEC = B.build();
}
