package github.jodevnull.immersivefluids.mixin;

import github.jodevnull.immersivefluids.features.NonCachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.BucketItem.class)
public abstract class BucketMixin{

    @Redirect(
        method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean bucketPlace(Level instance, BlockPos pos, BlockState newState, int flags) {
        boolean returnValue = true;
        if (!instance.isClientSide) {
            if(newState.getBlock() == Blocks.WATER) {
                returnValue = NonCachedWater.addWater(8, pos, instance);
            }
            else {
                returnValue = instance.setBlockAndUpdate(pos, newState);
            }
            return returnValue;
        }
        else {
            return true;
        }
    }

    @Inject(at = @At("HEAD"), method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z", cancellable = true)
    private void checkIfCanPlace(Player player, Level level, BlockPos pos, BlockHitResult result, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof LiquidBlockContainer) {
            if (blockState.getValue(BlockStateProperties.WATERLOGGED)) {
                cir.setReturnValue(false);
                return;
            }
            level.setBlock(pos, blockState.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            cir.setReturnValue(true);
        }
    }
}
