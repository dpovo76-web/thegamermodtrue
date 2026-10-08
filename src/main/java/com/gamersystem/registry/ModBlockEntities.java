package com.gamersystem.registry;

import com.gamersystem.GamerSystem;
import com.gamersystem.zone.ZoneCoreBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GamerSystem.MODID);

    // Si tu version de NeoForge/Minecraft cambia el constructor de BlockEntityType, este es el unico sitio a ajustar.
    public static final Supplier<BlockEntityType<ZoneCoreBlockEntity>> ZONE_CORE = TYPES.register("zone_core",
            () -> new BlockEntityType<>(ZoneCoreBlockEntity::new, ModBlocks.ZONE_CORE.get()));
}
