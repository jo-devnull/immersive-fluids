package github.jodevnull.immersivefluids.mixin.fluid;

import github.jodevnull.immersivefluids.features.NonCachedWater;
import github.jodevnull.immersivefluids.features.SpecialFlow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class MixinPistonBaseBlock {

    @Inject(at = @At("HEAD"), method = "moveBlocks", cancellable = true)
    private void moveBlocks(Level level, BlockPos pos, Direction facing, boolean extending, CallbackInfoReturnable<Boolean> cir) {
        if (!level.isClientSide) {
            if (SpecialFlow.checkIfCanPushWater((ServerLevel) level, pos, facing)) {
                BlockPos blockPos2 = pos.relative(facing);
                if (level.getBlockState(blockPos2).is(Blocks.WATER)) {
                    boolean returnValue = SpecialFlow.tryPushWater((ServerLevel) level, pos, facing);
                    if (returnValue) {
                        NonCachedWater.setWaterLevel(0, blockPos2, level);
                    }
                    //cir.setReturnValue(returnValue);
                }
            } else {
                cir.setReturnValue(false);
                cir.cancel();
            }

        }
    }
}