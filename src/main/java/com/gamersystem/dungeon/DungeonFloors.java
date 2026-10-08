package com.gamersystem.dungeon;

import com.gamersystem.Config;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/** Lee los pisos de la mazmorra desde la config. Los mobs son IDs de cualquier mob (vanilla o de otros mods). */
public final class DungeonFloors {
    private DungeonFloors() {}

    public static TreeMap<Integer, List<String>> all() {
        TreeMap<Integer, List<String>> floors = new TreeMap<>();
        for (String line : Config.DUNGEON_FLOORS.get()) {
            String[] parts = line.split("=", 2);
            if (parts.length != 2) continue;
            try {
                int floor = Integer.parseInt(parts[0].trim());
                List<String> mobs = new ArrayList<>();
                for (String id : parts[1].split(",")) {
                    String t = id.trim();
                    if (!t.isEmpty()) mobs.add(t);
                }
                floors.put(floor, mobs);
            } catch (NumberFormatException ignored) {
                // linea mal formada: se ignora
            }
        }
        return floors;
    }

    public static List<String> mobsFor(int floor) {
        TreeMap<Integer, List<String>> floors = all();
        if (floors.isEmpty()) return List.of();
        Integer key = floors.floorKey(floor);
        return floors.get(key != null ? key : floors.firstKey());
    }
}
