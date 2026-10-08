package com.gamersystem;

import com.gamersystem.data.GamerData;
import com.gamersystem.registry.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Logica compartida: dar EXP, restaurar MP y aplicar las stats a los atributos del jugador. */
public final class GamerService {
    private GamerService() {}

    public static GamerData get(ServerPlayer p) {
        return p.getData(ModAttachments.GAMER.get());
    }

    public static void set(ServerPlayer p, GamerData d) {
        p.setData(ModAttachments.GAMER.get(), d);
    }

    public static boolean isAwake(ServerPlayer p) {
        return get(p).awakened();
    }

    public static void giveExp(ServerPlayer p, int amount) {
        GamerData d = get(p);
        if (!d.awakened() || amount <= 0) return;
        int before = d.level();
        d = d.gainExp(amount);
        set(p, d);
        if (d.level() > before) {
            p.sendSystemMessage(Component.literal("[Sistema] Nivel " + d.level() + "! Puntos de stat disponibles: "
                    + d.statPoints() + "  (usa /gamer add <stat> <n>)"));
            applyStats(p);
        }
    }

    public static void restoreMp(ServerPlayer p, int amount) {
        GamerData d = get(p);
        if (!d.awakened() || amount <= 0) return;
        set(p, d.withMp(d.mp() + amount));
    }

    /** Traduce las stats a atributos reales del juego. */
    public static void applyStats(ServerPlayer p) {
        GamerData d = get(p);
        if (!d.awakened()) return;
        modify(p, Attributes.MAX_HEALTH, "vit_health", (d.vit() - 5) * 2.0);
        modify(p, Attributes.ATTACK_DAMAGE, "str_damage", (d.str() - 5) * 0.5);
        modify(p, Attributes.MOVEMENT_SPEED, "dex_speed", (d.dex() - 5) * 0.002);
        modify(p, Attributes.LUCK, "luk_luck", (d.luk() - 5) * 0.5);
    }

    private static void modify(ServerPlayer p, Holder<Attribute> attribute, String name, double amount) {
        AttributeInstance inst = p.getAttribute(attribute);
        if (inst == null) return;
        Identifier id = Identifier.fromNamespaceAndPath(GamerSystem.MODID, name);
        inst.removeModifier(id);
        if (amount != 0) {
            inst.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
