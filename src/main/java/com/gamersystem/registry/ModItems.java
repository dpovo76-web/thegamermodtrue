package com.gamersystem.registry;

import com.gamersystem.GamerSystem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GamerSystem.MODID);

    public static final DeferredItem<BlockItem> ZONE_CORE_ITEM = ITEMS.registerSimpleBlockItem("zone_core", ModBlocks.ZONE_CORE);
}
