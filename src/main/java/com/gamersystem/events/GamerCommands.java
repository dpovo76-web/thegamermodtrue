package com.gamersystem.events;

import com.gamersystem.GamerService;
import com.gamersystem.GamerSystem;
import com.gamersystem.data.GamerData;
import com.gamersystem.dungeon.DungeonFloors;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * /gamer status
 * /gamer add <str|vit|dex|int|wis|luk> <n>
 * /gamer floors
 * (La ventana de estado grafica con teclas llegara en la siguiente fase.)
 */
@EventBusSubscriber(modid = GamerSystem.MODID)
public class GamerCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gamer")
                .then(Commands.literal("status").executes(ctx -> status(ctx.getSource())))
                .then(Commands.literal("floors").executes(ctx -> floors(ctx.getSource())))
                .then(Commands.literal("add")
                        .then(Commands.argument("stat", StringArgumentType.word())
                                .suggests((c, b) -> {
                                    for (String s : List.of("str", "vit", "dex", "int", "wis", "luk")) b.suggest(s);
                                    return b.buildFuture();
                                })
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> add(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "stat"),
                                                IntegerArgumentType.getInteger(ctx, "amount")))))));
    }

    private static int status(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer p)) return 0;
        GamerData d = GamerService.get(p);
        send(src, "=== VENTANA DE ESTADO ===");
        send(src, "Nivel " + d.level() + "   EXP " + d.exp() + "/" + d.expToNext());
        send(src, "HP " + (int) p.getHealth() + "/" + (int) p.getMaxHealth() + "   MP " + d.mp() + "/" + d.maxMp());
        send(src, "STR " + d.str() + "  VIT " + d.vit() + "  DEX " + d.dex());
        send(src, "INT " + d.intel() + "  WIS " + d.wis() + "  LUK " + d.luk());
        send(src, "Puntos disponibles: " + d.statPoints());
        return 1;
    }

    private static int add(CommandSourceStack src, String stat, int amount) {
        if (!(src.getEntity() instanceof ServerPlayer p)) return 0;
        GamerData before = GamerService.get(p);
        GamerData after = before.addStat(stat, amount);
        if (after == before) {
            send(src, "[Sistema] No se pudo: stat invalido o puntos insuficientes (" + before.statPoints() + ").");
            return 0;
        }
        GamerService.set(p, after);
        GamerService.applyStats(p);
        send(src, "[Sistema] +" + amount + " " + stat.toUpperCase());
        return 1;
    }

    private static int floors(CommandSourceStack src) {
        DungeonFloors.all().forEach((floor, mobs) -> send(src, "Piso " + floor + ": " + String.join(", ", mobs)));
        return 1;
    }

    private static void send(CommandSourceStack src, String text) {
        src.sendSuccess(() -> Component.literal(text), false);
    }
}
