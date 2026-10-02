package github.jodevnull.immersivefluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

public class WaterUtils
{
    public static final BooleanProperty ISFINITE = BooleanProperty.create("isfinite");
    public static final BooleanProperty ISNATURAL = BooleanProperty.create("isnatural");

    public static BlockState getWater() {
        return Fluids.WATER.getSource(false).createLegacyBlock().setValue(ISNATURAL, false);
    }

    public static BlockState getWater(int level) {
        return Fluids.WATER.getFlowing(level, false).createLegacyBlock().setValue(ISNATURAL, false);
    }

    public static boolean isNatural(BlockState state) {
        return state.hasProperty(ISNATURAL) && state.getValue(ISNATURAL);
    }

    public static boolean isNatural(LevelAccessor world, BlockPos pos) {
        // TODO: implement this
        return false;
    }
}
