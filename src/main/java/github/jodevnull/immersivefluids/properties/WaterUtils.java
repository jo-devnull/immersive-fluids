package github.jodevnull.immersivefluids.properties;

import github.jodevnull.immersivefluids.WaterPhysics;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import static github.jodevnull.immersivefluids.properties.WaterProperties.EVAPORATION;

public class WaterUtils
{
    public static FluidState getFlowingWater(int level) {
        assert level > 0 && level <= 8;
        return Fluids.WATER.getSource(false).setValue(FlowingFluid.LEVEL, level);
    }

    public static boolean hasEvaporation(BlockState state) {
        return state.hasProperty(EVAPORATION);
    }

    public static int getEvaporation(BlockState state) {
        return hasEvaporation(state) ? state.getValue(EVAPORATION) : -1;
    }

    public static boolean hasWaterLevel(BlockState state) {
        return state.hasProperty(WaterPhysics.WATER_LEVEL);
    }

    public static int getWaterLevel(BlockState state) {
        return hasWaterLevel(state) ? state.getValue(WaterPhysics.WATER_LEVEL) : -1;
    }

    public static boolean hasWater(BlockState state) {
        return hasWaterLevel(state) && getWaterLevel(state) > 0;
    }

    public static BlockState setEvaporation(BlockState state, int evaporation) {
        return hasEvaporation(state)
            ? state.setValue(EVAPORATION, evaporation)
            : state;
    }

    public static BlockState setWaterLevel(BlockState state, int level) {
        return hasWaterLevel(state)
            ? state.setValue(WaterPhysics.WATER_LEVEL, level)
            : state;
    }

    public static boolean isFull(BlockState state) {
        return getWaterLevel(state) == 8;
    }

    public static boolean isEmpty(BlockState state) {
        return getWaterLevel(state) == 0;
    }

    public static boolean canBeWaterlogged(BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED);
    }

    public static boolean isWaterlogged(BlockState state) {
        return canBeWaterlogged(state) && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    public static BlockState setWaterlogged(BlockState state, boolean waterlogged) {
        return canBeWaterlogged(state)
            ? state.setValue(BlockStateProperties.WATERLOGGED, waterlogged)
            : state;
    }
}
