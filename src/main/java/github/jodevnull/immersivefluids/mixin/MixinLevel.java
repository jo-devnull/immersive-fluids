package github.jodevnull.immersivefluids.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.immersivefluids.WaterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public abstract class MixinLevel
{
    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @ModifyVariable(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At("HEAD"),
        index = 2,
        argsOnly = true
    )
    private BlockState ifc$placeBlock(BlockState newState, @Local(argsOnly = true) BlockPos pos) {
        // TODO: implement breaking of waterlogged blocks
        final Level self = (Level) (Object) this;
        final BlockState oldState = getBlockState(pos);

        if (!oldState.isAir() && newState.is(Blocks.WATER)) {
            final int lifetime = WaterUtils.getLifetime(self, pos);
            return newState.setValue(WaterUtils.LIFETIME, lifetime);
        }

        return newState;
    }
}
