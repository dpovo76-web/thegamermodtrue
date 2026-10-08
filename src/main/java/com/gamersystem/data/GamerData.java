package com.gamersystem.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Datos RPG del jugador (se guardan como "data attachment" y se copian al morir). */
public record GamerData(boolean awakened, int level, int exp, int statPoints,
                        int str, int vit, int dex, int intel, int wis, int luk, int mp) {

    public static final GamerData DEFAULT = new GamerData(false, 1, 0, 0, 5, 5, 5, 5, 5, 5, 50);

    public static final Codec<GamerData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("awakened", false).forGetter(GamerData::awakened),
            Codec.INT.optionalFieldOf("level", 1).forGetter(GamerData::level),
            Codec.INT.optionalFieldOf("exp", 0).forGetter(GamerData::exp),
            Codec.INT.optionalFieldOf("statPoints", 0).forGetter(GamerData::statPoints),
            Codec.INT.optionalFieldOf("str", 5).forGetter(GamerData::str),
            Codec.INT.optionalFieldOf("vit", 5).forGetter(GamerData::vit),
            Codec.INT.optionalFieldOf("dex", 5).forGetter(GamerData::dex),
            Codec.INT.optionalFieldOf("int", 5).forGetter(GamerData::intel),
            Codec.INT.optionalFieldOf("wis", 5).forGetter(GamerData::wis),
            Codec.INT.optionalFieldOf("luk", 5).forGetter(GamerData::luk),
            Codec.INT.optionalFieldOf("mp", 50).forGetter(GamerData::mp)
    ).apply(i, GamerData::new));

    public static final int POINTS_PER_LEVEL = 3;

    public int expToNext() {
        return 100 + (level - 1) * 50;
    }

    public int maxMp() {
        return 50 + intel * 4 + wis * 6;
    }

    public GamerData awaken() {
        return new GamerData(true, level, exp, statPoints, str, vit, dex, intel, wis, luk, maxMp());
    }

    public GamerData withMp(int newMp) {
        int clamped = Math.max(0, Math.min(maxMp(), newMp));
        return new GamerData(awakened, level, exp, statPoints, str, vit, dex, intel, wis, luk, clamped);
    }

    /** Suma EXP y sube de nivel las veces necesarias. */
    public GamerData gainExp(int amount) {
        int l = level, e = exp + Math.max(0, amount), points = statPoints;
        while (e >= 100 + (l - 1) * 50) {
            e -= 100 + (l - 1) * 50;
            l++;
            points += POINTS_PER_LEVEL;
        }
        return new GamerData(awakened, l, e, points, str, vit, dex, intel, wis, luk, mp);
    }

    /** Reparte puntos de stat. Devuelve el mismo objeto si el stat no existe o no hay puntos. */
    public GamerData addStat(String stat, int n) {
        if (n <= 0 || n > statPoints) return this;
        int p = statPoints - n;
        return switch (stat.toLowerCase()) {
            case "str" -> new GamerData(awakened, level, exp, p, str + n, vit, dex, intel, wis, luk, mp);
            case "vit" -> new GamerData(awakened, level, exp, p, str, vit + n, dex, intel, wis, luk, mp);
            case "dex" -> new GamerData(awakened, level, exp, p, str, vit, dex + n, intel, wis, luk, mp);
            case "int" -> new GamerData(awakened, level, exp, p, str, vit, dex, intel + n, wis, luk, mp);
            case "wis" -> new GamerData(awakened, level, exp, p, str, vit, dex, intel, wis + n, luk, mp);
            case "luk" -> new GamerData(awakened, level, exp, p, str, vit, dex, intel, wis, luk + n, mp);
            default -> this;
        };
    }
}
