package github.jodevnull.immersivefluids.mixin.fluid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import github.jodevnull.immersivefluids.FlowWater;
import github.jodevnull.immersivefluids.features.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.FlowingFluid.class)
public class FlowingMixin {
    @Inject(
        at = {@At("HEAD")},
        method = {"spread"},
        cancellable = true
    )
    private void tryFlow(Level level, BlockPos pos, FluidState state, CallbackInfo bruh) {
        if (ifc$isWater(state.getType()) && !CachedWater.isNatural(level.getBlockState(pos))) {
            FlowWater.flowWater(level, pos, state);
            bruh.cancel();
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"getNewLiquid"},
        cancellable = true
    )
    private void getUpdatedState(Level level, BlockPos pos, BlockState blockState, CallbackInfoReturnable<FluidState> bruh) {
        FluidState fluidstate = blockState.getFluidState();
        if (ifc$isWater(fluidstate.getType()) && !CachedWater.isNatural(level.getBlockState(pos))) {
            bruh.setReturnValue(Fluids.FLOWING_WATER.getFlowing(blockState.getFluidState().getAmount(), false));
        }
    }

    @WrapMethod(method = "getFlow")
    public Vec3 ifc$getFlow(BlockGetter blockReader, BlockPos pos, FluidState fluidState, Operation<Vec3> original) {
        if (!CachedWater.isNatural(blockReader.getBlockState(pos)))
            return Vec3.ZERO;
        else
            return original.call(blockReader, pos, fluidState);
    }

    @Inject(method = "canConvertToSource(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", at=@At("HEAD"), cancellable = true, remap = false)
    private void ifc$canConvertToSource(FluidState state, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(CachedWater.isNatural(level.getBlockState(pos)));
    }

    @Unique
    public boolean ifc$isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }
}