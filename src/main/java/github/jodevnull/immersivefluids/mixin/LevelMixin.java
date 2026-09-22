package github.jodevnull.immersivefluids.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.immersivefluids.features.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static github.jodevnull.immersivefluids.properties.WaterFluidProperties.NATURAL;

@Mixin(Level.class)
public abstract class LevelMixin
{
    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @ModifyVariable(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At("HEAD"),
        index = 2,
        argsOnly = true
    )
    private BlockState ifc$destroyBlock(BlockState state, @Local(argsOnly = true) BlockPos p_46605_) {
        final BlockState old = getBlockState(p_46605_);

        if (state.isAir() || old.isAir())
            return state;

        if (!CachedWater.isWater(state))
            return state;

        if (old.hasProperty(BlockStateProperties.WATERLOGGED) && old.getValue(BlockStateProperties.WATERLOGGED))
            if (state.hasProperty(NATURAL))
                return state.setValue(NATURAL, false);

        return state;
    }
}
