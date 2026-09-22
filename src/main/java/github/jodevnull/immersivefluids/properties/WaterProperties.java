package github.jodevnull.immersivefluids.properties;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WaterProperties
{
    public static final int MAX_EVAPORATION = 5;

    public static final BooleanProperty ISFINITE = BooleanProperty.create("isfinite");
    public static final IntegerProperty EVAPORATION = IntegerProperty.create("evaporation", 0, MAX_EVAPORATION);

    public static BlockState naturally(BlockState state) {
        return state.setValue(EVAPORATION, 0);
    }

    public static BlockState interacted(BlockState state) {
        return state.setValue(EVAPORATION, MAX_EVAPORATION);
    }
}
