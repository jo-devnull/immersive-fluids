package github.jodevnull.immersivefluids.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.SimpleWaterloggedBlock;

@Mixin(SimpleWaterloggedBlock.class)
public interface WaterLoggableMixin
{
/*    @Inject(at = @At("HEAD"), method = "canPlaceLiquid", cancellable = true)
    default void canFill(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            cir.setReturnValue(state.getValue(WATER_LEVEL) < 8);
        }
    }

    @Inject(at = @At("HEAD"), method = "placeLiquid", cancellable = true)
    default void tryFill(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            cir.setReturnValue(false);
            if (state.getValue(WATER_LEVEL) < 8) {
                world.setBlock(pos, state.setValue(WATER_LEVEL, 8), 3);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "pickupBlock", cancellable = true)
    default void tryDrain(LevelAccessor world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            cir.setReturnValue(ItemStack.EMPTY);
            if (state.getValue(WATER_LEVEL) == 8) {
                world.setBlock(pos, state.setValue(WATER_LEVEL, 0), 3);
                cir.setReturnValue(new ItemStack(Items.WATER_BUCKET));
            }
        }
    }*/
}
