package github.jodevnull.immersivefluids.core.simulation;

import github.jodevnull.immersivefluids.core.CachedWater;
import github.jodevnull.immersivefluids.core.features.FlowFeature;
import github.jodevnull.immersivefluids.core.features.FlowFeatureInfinite;
import github.jodevnull.immersivefluids.core.features.PuddleFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FlowWater {
    public static int worldMinY = -64;
    public static BlockPos ce24;
    public static ServerLevel world;
    private FlowWater() {
    }

    public static void flowWater(LevelAccessor world, BlockPos fluidPos, FluidState state) {
        //Tick Counter
        if (fluidPos.getY() == worldMinY) {
            // TODO INSECURE
            CachedWater.setWaterLevel(0, fluidPos);
        } else {
            FlowWater.world = (ServerLevel) world;
            CachedWater.setup(FlowWater.world, fluidPos);
            int centerLevel = CachedWater.getWaterLevel(fluidPos);

            final var blockState = world.getBlockState(fluidPos);

            if ((blockState.getBlock() instanceof LiquidBlockContainer))
                return;

            if (CachedWater.isNatural(blockState))
                return;

            if ((CachedWater.getBlockState(fluidPos.below()).is(Blocks.LAVA)))
                world.setBlock(fluidPos.below(), Blocks.OBSIDIAN.defaultBlockState(), 11, 11);

            final BlockState belowState = CachedWater.getBlockState(fluidPos.below());

            if (belowState.canBeReplaced(Fluids.WATER) && isNotFull(CachedWater.getWaterLevel(fluidPos.below()))) {
                CachedWater.setWaterLevel(0, fluidPos);
                CachedWater.addWater(centerLevel, fluidPos.below());
            } else {
                equalizeWater(fluidPos, centerLevel, world);
            }
        }
    }

    public static void infiniteWaterFlow(LevelAccessor world, BlockPos fluidPos, FluidState state) {
        if (fluidPos.getY() == worldMinY) {
            // TODO INSECURE
            CachedWater.setWaterLevel(0, fluidPos);
        } else {
            FlowWater.world = (ServerLevel) world;
            CachedWater.setup(FlowWater.world, fluidPos);

            if ((CachedWater.getBlockState(fluidPos.below()).canBeReplaced(Fluids.WATER)) && isNotFull(CachedWater.getWaterLevel(fluidPos.below()))) {
                CachedWater.setWaterLevel(8, fluidPos.below());
                System.out.println("set below");
            } else {
                FlowFeatureInfinite.execute(fluidPos);
            }
        }
    }

    public static boolean isNotFull(int waterLevel) {
        return waterLevel < 8 && waterLevel >= 0;
    }

    public static void equalizeWater(BlockPos center, int level, LevelAccessor world) {
        int radius = 2;
        int diameter = (radius * 2) + 1;

        int[][] data = new int[diameter][diameter];
        int[][] newData;

        //int centerLevel = level + 10;

        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();

        int newX = 0;
        int newZ = 0;
        int minLevel = 99;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                newX = x + dx;
                newZ = z + dz;
                BlockPos internalPos = new BlockPos(newX, y, newZ);
                data[dx + radius][dz + radius] = CachedWater.getWaterLevel(internalPos);
            }
        }

        newData = FloodFill.flood(data, radius, radius);

        for (int i = 0; i < diameter - 1; i++) {
            for (int j = 0; j < diameter - 1; j++) {
                if (newData[i][j] >= 10) {
                    if (newData[i][j] < minLevel) {
                        minLevel = newData[i][j];
                    }
                }
            }
        }

        int range = level + 10 - minLevel;

        if (range == 1) {
            PuddleFeature.execute(center, level);
        } else if (range > 1) {
            FlowFeature.execute(center);
        }
    }
}
