package github.jodevnull.immersivefluids.mixin.fluid;

import github.jodevnull.immersivefluids.FlowLava;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.FlowingFluid.class)
public class FlowingLavaMixin {
    @Inject(at = @At("HEAD"), method = "canPassThrough", cancellable = true)
    private void canFlowThrough(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, Direction direction, BlockPos spreadPos, BlockState spreadState, FluidState fluidState, CallbackInfoReturnable<Boolean> lbruh) {
        if (fluid instanceof LavaFluid) {
            lbruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canSpreadTo", cancellable = true)
    private void canFlow(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid, CallbackInfoReturnable<Boolean> lbruh) {
        if (fluid instanceof LavaFluid) {
            lbruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "spread", cancellable = true)
    private void tryFlow(Level level, BlockPos pos, FluidState state, CallbackInfo lbruh) {
        if ((state.getType() instanceof LavaFluid.Flowing) || (state.getType() instanceof LavaFluid.Source)) {
            FlowLava.flowlava(level, pos, state);
            lbruh.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getNewLiquid", cancellable = true)
    private void getUpdatedState(Level level, BlockPos pos, BlockState blockState, CallbackInfoReturnable<FluidState> lbruh) {
        FluidState fluidstate = blockState.getFluidState();
        if (fluidstate.getType() instanceof LavaFluid.Flowing) {
            lbruh.setReturnValue(Fluids.FLOWING_LAVA.getFlowing(blockState.getFluidState().getAmount(), false));
        }
    }
}