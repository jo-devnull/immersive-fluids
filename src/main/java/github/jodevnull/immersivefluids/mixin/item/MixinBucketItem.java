package github.jodevnull.immersivefluids.mixin.item;

import github.jodevnull.immersivefluids.core.NonCachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.world.item.BucketItem.class)
public abstract class MixinBucketItem
{
    @Redirect(
        method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean bucketPlace(Level instance, BlockPos pos, BlockState newState, int flags) {
        if (!instance.isClientSide)
            return newState.is(Blocks.WATER)
                ? NonCachedWater.addWater(8, pos, instance)
                : instance.setBlockAndUpdate(pos, newState);

        return true;
    }

    // FIXME
    // @WrapOperation(
    //     method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
    //     at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlockContainer;placeLiquid(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z"))
    // private boolean ifc$setProperties(LiquidBlockContainer instance, LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidstate, Operation<Boolean> original) {
    //     if (WaterUtils.hasEvaporation(state)) {
    //         final var newState = WaterUtils.setWaterLevel(WaterUtils.setEvaporation(state, MAX_EVAPORATION), 8);
    //         return original.call(instance, world, pos, newState, fluidstate);
    //     }
    //
    //     return original.call(instance, world, pos, state, fluidstate);
    // }
}
