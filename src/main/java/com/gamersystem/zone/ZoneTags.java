package com.gamersystem.zone;

import net.minecraft.world.entity.Entity;

/**
 * Unico sitio donde se leen las etiquetas de entidad.
 * Si tu version de Minecraft renombra Entity#getTags (p. ej. a entityTags()), cambialo solo aqui.
 */
public final class ZoneTags {
    private ZoneTags() {}

    public static boolean hasTag(Entity entity, String tag) {
        return entity.getTags().contains(tag);
    }

    /** True si la entidad es un enemigo de entrenamiento creado por alguna zona. */
    public static boolean isTrainingMob(Entity entity) {
        for (String t : entity.getTags()) {
            if (t.startsWith(ZoneCoreBlockEntity.TAG_PREFIX)) return true;
        }
        return false;
    }
}
