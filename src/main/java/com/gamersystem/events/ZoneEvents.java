package com.gamersystem.events;

import com.gamersystem.GamerSystem;
import com.gamersystem.zone.ZoneCoreBlockEntity;
import com.gamersystem.zone.ZoneTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

/** Reglas de las zonas: nada de enemigos naturales dentro, y los enemigos "salvajes" no pueden danar a los jugadores. */
@EventBusSubscriber(modid = GamerSystem.MODID)
public class ZoneEvents {

    @SubscribeEvent
    public static void onSpawn(FinalizeSpawnEvent event) {
        if (!(event.getEntity() instanceof Enemy)) return;
        EntitySpawnReason reason = event.getSpawnType();
        if (reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION
                && reason != EntitySpawnReason.REINFORCEMENT && reason != EntitySpawnReason.PATROL) return;

        Vec3 pos = new Vec3(event.getX(), event.getY(), event.getZ());
        if (ZoneCoreBlockEntity.zoneAt(event.getLevel().getLevel(), pos) != null) {
            event.setSpawnCancelled(true);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof Enemy) || ZoneTags.isTrainingMob(attacker)) return;
        if (ZoneCoreBlockEntity.zoneAt(victim.level(), victim.position()) != null) {
            event.setCanceled(true);
        }
    }
}
