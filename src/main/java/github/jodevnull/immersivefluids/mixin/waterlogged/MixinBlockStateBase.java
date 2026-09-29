package github.jodevnull.immersivefluids.mixin.waterlogged;

import github.jodevnull.immersivefluids.properties.WaterUtils;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static github.jodevnull.immersivefluids.WaterPhysics.WATER_LEVEL;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockStateBase
{
    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void configurablewaterlogging$getFluidState(CallbackInfoReturnable<FluidState> cir) {
        BlockState self = (BlockState) (Object) this;

        if (self.hasProperty(WATER_LEVEL) && self.getValue(WATER_LEVEL) > 0) {
            cir.setReturnValue(WaterUtils.getFlowingWater(self.getValue(WATER_LEVEL)));
        }
    }
}
