package github.jodevnull.immersivefluids.mixin.fluid;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static github.jodevnull.immersivefluids.properties.WaterFluidProperties.NATURAL;

@Mixin(WaterFluid.Flowing.class)
public class FlowingWaterMixin
{
    @Inject(
        at = {@At("HEAD")},
        method = {"createFluidStateDefinition"}
    )
    protected void appendProperties(StateDefinition.Builder<Fluid, FluidState> builder, CallbackInfo ci) {
        builder.add(NATURAL);
    }
}