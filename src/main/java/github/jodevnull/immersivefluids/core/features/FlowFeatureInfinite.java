package github.jodevnull.immersivefluids.core.features;

import github.jodevnull.immersivefluids.core.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class FlowFeatureInfinite
{
    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if(CachedWater.isNotFull(center.relative(dir))) {
                CachedWater.setWaterLevel(8, center.relative(dir));
                //System.out.println("set flowfeature");
            };
        }
    }
}
