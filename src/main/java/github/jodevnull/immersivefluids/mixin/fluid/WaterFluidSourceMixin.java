package github.jodevnull.immersivefluids.mixin.fluid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterFluid.Source.class)
public abstract class WaterFluidSourceMixin extends WaterFluid
{
    @ModifyReturnValue(method = "getAmount", at = @At("RETURN"))
    private int ifc$getAmount(int original, @Local(argsOnly = true) FluidState state) {
        return state.getValue(LEVEL);
    }
}
