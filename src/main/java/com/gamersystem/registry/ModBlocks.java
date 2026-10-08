package com.gamersystem.registry;

import com.gamersystem.GamerSystem;
import com.gamersystem.zone.ZoneCoreBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GamerSystem.MODID);

    public static final DeferredBlock<Block> ZONE_CORE = BLOCKS.registerBlock(
            "zone_core", ZoneCoreBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F, 6.0F).lightLevel(state -> 10));
}
