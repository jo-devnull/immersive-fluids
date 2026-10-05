package github.jodevnull.immersivefluids;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import github.jodevnull.immersivefluids.core.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class WaterUtils
{
    public static final int MAX_LIFETIME = 5;
    public static final BooleanProperty ISFINITE = BooleanProperty.create("isfinite");
    public static final IntegerProperty LIFETIME = IntegerProperty.create("lifetime", 0, MAX_LIFETIME);

    public static FluidState getWaterState(int level) {
        if (level == 8)
            return Fluids.WATER.getSource(false);

        else if (level > 0 && level < 8)
            return Fluids.WATER.getFlowing(level, false);

        else return Fluids.EMPTY.defaultFluidState();
    }

    public static FluidState getWaterState(int level, int lifetime) {
        return getWaterState(level).trySetValue(LIFETIME, lifetime);
    }

    public static BlockState getWater() {
        return getWaterState(8).createLegacyBlock().trySetValue(LIFETIME, MAX_LIFETIME);
    }

    public static BlockState getWater(int level) {
        return getWaterState(level).createLegacyBlock().trySetValue(LIFETIME, MAX_LIFETIME);
    }

    public static BlockState getWater(int level, int lifetime) {
        return getWaterState(level).createLegacyBlock().trySetValue(LIFETIME, lifetime);
    }

    public static FluidState getFluidState(Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);

        if (state.getBlock() instanceof LiquidBlock) {
            return state.getFluidState();
        }

        if (Fluidlogged.isFluidloggable(state)) {
            return level.getFluidState(pos);
        }

        return state.getFluidState();
    }

    public static boolean isNatural(BlockState state) {
        return state.hasProperty(LIFETIME) && state.getValue(LIFETIME) == 0;
    }

    public static boolean isNatural(Level level, BlockPos pos) {
        return getLifetime(level, pos) == 0;
    }

    public static boolean isWaterlogged(BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    public static int getLifetime(Level level, BlockPos pos) {
        final BlockState state = level.getBlockState(pos);

        if (state.hasProperty(LIFETIME)) {
            return state.getValue(LIFETIME);
        } else if (Fluidlogged.isFluidloggable(state)) {
            if (isWaterlogged(state)) {
                return MAX_LIFETIME;
            }

            return level.getFluidState(pos).getOptionalValue(LIFETIME).orElse(0);
        }

        return 0;
    }

    public static void setLifetime(Level level, BlockPos pos, int lifetime) {
        final var prev = level.getFluidState(pos);
        final var state = level.getBlockState(pos);
        final int flags = Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS;

        if (state.is(Blocks.WATER)) {
            level.setBlock(pos, getWater(state.getFluidState().getAmount(), lifetime), flags);
        }

        else if (prev.hasProperty(LIFETIME) && prev.getValue(LIFETIME) != lifetime) {
            if (Fluidlogged.isFluidloggable(state)) {
                setFluidState(level, pos, prev.setValue(LIFETIME, lifetime), flags);
            }
        }
    }

    public static void setFluidState(Level level, BlockPos pos, FluidState fluidState, int flags) {
        ((LevelExtension) level).setFluid(pos, fluidState, flags);
    }
}
