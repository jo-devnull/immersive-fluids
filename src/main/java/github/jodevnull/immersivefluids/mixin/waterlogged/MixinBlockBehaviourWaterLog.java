package github.jodevnull.immersivefluids.mixin.waterlogged;

import github.jodevnull.immersivefluids.properties.WaterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static github.jodevnull.immersivefluids.WaterPhysics.WATER_LEVEL;

@Mixin(BlockBehaviour.class)
public class MixinBlockBehaviourWaterLog
{
    @Inject(method = "getFluidState", at=@At("HEAD"), cancellable = true)
    private void ifc$getFluidState(BlockState state, CallbackInfoReturnable<FluidState> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            final int waterLevel = state.getValue(WATER_LEVEL);

            if (waterLevel > 0) {
                cir.setReturnValue(WaterUtils.getFlowingWater(waterLevel));
            }
        }
    }

    @Inject(method = "updateShape", at=@At("HEAD"))
    private void ifc$scheduleFluidTick(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (state.hasProperty(WATER_LEVEL) && state.getValue(WATER_LEVEL) > 0) {
            level.scheduleTick(pos, state.getFluidState().getType(), state.getFluidState().getType().getTickDelay(level));
        }
    }
}
