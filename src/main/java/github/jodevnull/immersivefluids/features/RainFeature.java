package github.jodevnull.immersivefluids.features;

import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import static github.jodevnull.immersivefluids.properties.WaterProperties.EVAPORATION;

public class RainFeature
{
    public static boolean canEvaporate(ServerLevel world, BlockPos pos, BlockState state) {
        if (CachedWater.world == null)
            return false;

        return state.hasProperty(EVAPORATION) && state.getValue(EVAPORATION) > 0
            && FluidHelper.isWater(state.getFluidState().getType()) // is water
            && CachedWater.getWaterLevel(pos) == 1  // min level
            && !world.isRainingAt(pos);             // not raining
    }
}
