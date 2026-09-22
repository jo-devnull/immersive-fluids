package github.jodevnull.immersivefluids.mixin;

import github.jodevnull.immersivefluids.FlowWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.FlowingFluid.class)
public class FlowingMixin {
    @Inject(at = @At("HEAD"), method = "canPassThrough", cancellable = true)
    private void canFlowThrough(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, Direction direction, BlockPos spreadPos, BlockState spreadState, FluidState fluidState, CallbackInfoReturnable<Boolean> bruh) {
        if (isWater(fluid)) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canSpreadTo", cancellable = true)
    private void canFlow(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid, CallbackInfoReturnable<Boolean> bruh) {
        if (isWater(fluid)) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "spread", cancellable = true)
    private void tryFlow(Level level, BlockPos pos, FluidState state, CallbackInfo bruh) {
        if (isWater(state.getType())) {
            FlowWater.flowWater(level, pos, state);
            bruh.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getNewLiquid", cancellable = true)
    private void getUpdatedState(Level level, BlockPos pos, BlockState blockState, CallbackInfoReturnable<FluidState> bruh) {
        FluidState fluidstate = blockState.getFluidState();
        if (isWater(fluidstate.getType())) {
            bruh.setReturnValue(Fluids.FLOWING_WATER.getFlowing(blockState.getFluidState().getAmount(), false));
        }
    }

    /**
     * @author ewoudje
     * @reason fck flowing animation
     */
    @Overwrite
    public Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }

    @Unique
    public boolean isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }
}