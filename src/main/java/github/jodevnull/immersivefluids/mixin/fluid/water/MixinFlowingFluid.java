package github.jodevnull.immersivefluids.mixin.fluid.water;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import github.jodevnull.immersivefluids.core.simulation.FlowWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FlowingFluid.class, priority = 9999)
public class MixinFlowingFluid
{
    @Inject(method = "spread", at = @At("HEAD"), cancellable = true)
    private void tryFlow(Level level, BlockPos pos, FluidState state, CallbackInfo bruh) {
        if (ifc$isWater(state.getType())) {
            FlowWater.flowWater(level, pos, state);
            bruh.cancel();
        }
    }

    @Inject(method = "getNewLiquid", at = @At("HEAD"), cancellable = true)
    private void getUpdatedState(Level level, BlockPos pos, BlockState blockState, CallbackInfoReturnable<FluidState> bruh) {
        final FluidState fluidState = level.getFluidState(pos);

        if (ifc$isWater(fluidState.getType())) {
            bruh.setReturnValue(Fluids.FLOWING_WATER.getFlowing(fluidState.getAmount(), false));
        }
    }

    @WrapMethod(method = "getFlow")
    public Vec3 ifc$getFlow(BlockGetter blockReader, BlockPos pos, FluidState fluidState, Operation<Vec3> original) {
        return Vec3.ZERO;
    }

    @Inject(method = "canConvertToSource(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", at=@At("HEAD"), cancellable = true, remap = false)
    private void ifc$canConvertToSource(FluidState state, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Unique
    public boolean ifc$isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }
}