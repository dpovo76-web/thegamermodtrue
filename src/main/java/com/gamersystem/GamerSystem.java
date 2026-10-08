package com.gamersystem;

import com.gamersystem.registry.ModAttachments;
import com.gamersystem.registry.ModBlockEntities;
import com.gamersystem.registry.ModBlocks;
import com.gamersystem.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(GamerSystem.MODID)
public class GamerSystem {
    public static final String MODID = "gamersystem";

    public GamerSystem(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.TYPES.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);

        modBus.addListener(GamerSystem::addCreative);
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.ZONE_CORE_ITEM);
        }
    }
}
