package github.jodevnull.immersivefluids.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.fluid.FluidHelper;
import github.jodevnull.immersivefluids.WaterUtils;
import github.jodevnull.immersivefluids.core.NonCachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
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
        boolean returnValue;

        if (!instance.isClientSide) {
            if (newState.getBlock() == Blocks.WATER)
                returnValue = NonCachedWater.addWater(8, pos, instance);
            else
                returnValue = instance.setBlockAndUpdate(pos, newState);

            return returnValue;
        }

        return true;
    }

     @WrapOperation(
         method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlockContainer;placeLiquid(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z"))
     private boolean ifc$setProperties(LiquidBlockContainer instance, LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidstate, Operation<Boolean> original) {
        final boolean result =  original.call(instance, world, pos, state, fluidstate);

        if (!world.isClientSide())
            return result;

        if (result && FluidHelper.isWater(fluidstate.getType()))
             WaterUtils.setLifetime((Level) world, pos, WaterUtils.MAX_LIFETIME);

         return result;
     }
}
