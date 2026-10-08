package com.gamersystem.zone;

import com.gamersystem.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Nucleo de Zona.
 *  - Clic derecho: alterna entre ZONA PROTEGIDA y ZONA DE ENTRENAMIENTO.
 *  - Shift + clic derecho: cambia el tipo de enemigo (solo en entrenamiento).
 */
public class ZoneCoreBlock extends Block implements EntityBlock {

    public ZoneCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ZoneCoreBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || type != ModBlockEntities.ZONE_CORE.get()) return null;
        return (l, p, s, be) -> ((ZoneCoreBlockEntity) be).serverTick();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof ZoneCoreBlockEntity zone) {
            if (player.isShiftKeyDown()) {
                zone.cycleMob(player);
            } else {
                zone.toggleMode(player);
            }
        }
        return InteractionResult.CONSUME;
    }
}
