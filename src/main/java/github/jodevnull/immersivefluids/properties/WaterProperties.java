package github.jodevnull.immersivefluids.properties;

import github.jodevnull.immersivefluids.WaterPhysics;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class WaterProperties
{
    public static final int MAX_EVAPORATION = 5;

    public static final BooleanProperty ISFINITE = BooleanProperty.create("isfinite");
    public static final IntegerProperty EVAPORATION = IntegerProperty.create("evaporation", 0, MAX_EVAPORATION);
}
