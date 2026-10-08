package com.gamersystem.events;

import com.gamersystem.GamerService;
import com.gamersystem.GamerSystem;
import com.gamersystem.data.GamerData;
import com.gamersystem.zone.ZoneTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Funcion de cosas vanilla dentro del sistema (todo suma EXP o MP):
 *  - Matar cualquier mob  -> EXP segun su vida maxima
 *  - Picar / romper bloques -> EXP segun su dureza
 *  - Craftear, fundir, pescar -> EXP
 *  - Comer comida vanilla -> restaura MP (segun su nutricion)
 *  - Beber pociones vanilla -> restaura MP
 *  - Correr -> EXP ("ejercicio")
 */
@EventBusSubscriber(modid = GamerSystem.MODID)
public class GamerEvents {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        GamerData d = GamerService.get(p);
        if (!d.awakened()) {
            GamerService.set(p, d.awaken());
            p.sendSystemMessage(Component.literal("[Sistema] Has obtenido la habilidad: THE GAMER"));
            p.sendSystemMessage(Component.literal("[Sistema] Usa /gamer status para ver tu ventana de estado."));
        }
        GamerService.applyStats(p);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) GamerService.applyStats(p);
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer p)) return;
        int exp = Math.max(1, (int) (event.getEntity().getMaxHealth() / 2.0F));
        if (ZoneTags.isTrainingMob(event.getEntity())) exp = Math.max(1, exp / 2); // entrenar da menos que la mazmorra real
        GamerService.giveExp(p, exp);
    }

    @SubscribeEvent
    public static void onTrainingDrops(LivingDropsEvent event) {
        if (ZoneTags.isTrainingMob(event.getEntity())) event.getDrops().clear();
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer p)) return;
        float hardness = event.getState().getDestroySpeed(event.getLevel(), event.getPos());
        if (hardness > 0) GamerService.giveExp(p, 1 + (int) hardness);
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) GamerService.giveExp(p, 1);
    }

    @SubscribeEvent
    public static void onSmelt(PlayerEvent.ItemSmeltedEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) GamerService.giveExp(p, 1);
    }

    @SubscribeEvent
    public static void onFish(ItemFishedEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) GamerService.giveExp(p, 3);
    }

    @SubscribeEvent
    public static void onUseItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        ItemStack stack = event.getItem();
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            GamerService.restoreMp(p, food.nutrition() * 2);
            GamerService.giveExp(p, 1);
        } else if (stack.has(DataComponents.POTION_CONTENTS)) {
            GamerService.restoreMp(p, 25);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        if (p.tickCount % 40 == 0) {
            GamerData d = GamerService.get(p);
            if (d.awakened()) GamerService.restoreMp(p, 1 + d.wis() / 5);
        }
        if (p.isSprinting() && p.tickCount % 200 == 0) GamerService.giveExp(p, 1);
    }
}
