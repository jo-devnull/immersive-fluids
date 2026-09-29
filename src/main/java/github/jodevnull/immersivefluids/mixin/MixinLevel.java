package github.jodevnull.immersivefluids.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.immersivefluids.features.CachedWater;
import github.jodevnull.immersivefluids.properties.WaterProperties;
import github.jodevnull.immersivefluids.properties.WaterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static github.jodevnull.immersivefluids.WaterPhysics.WATER_LEVEL;

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
    private BlockState ifc$destroyBlock(BlockState state, @Local(argsOnly = true) BlockPos p_46605_) {
        final BlockState old = getBlockState(p_46605_);

        if (state.isAir() || old.isAir())
            return state;

        if (!CachedWater.isWater(state))
            return state;

        if (WaterUtils.isWaterlogged(old))
            return WaterUtils.setEvaporation(state, WaterUtils.hasEvaporation(old)
                ? WaterUtils.getEvaporation(old)
                : WaterProperties.MAX_EVAPORATION);

        if (WaterUtils.hasWater(old))
            return WaterUtils.setEvaporation(
                Fluids.FLOWING_WATER.getFlowing(WaterUtils.getWaterLevel(old), false).createLegacyBlock(),
                WaterUtils.getEvaporation(old)
            );

        return state;
    }

    @Inject(method = "neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"))
    private void ifc$scheduleFluidTick(BlockPos pos, Block sourceBlock, BlockPos fromPos, CallbackInfo ci) {
        final Level self = (Level) (Object) this;
        final BlockState state = self.getBlockState(pos);

        if (state.hasProperty(WATER_LEVEL) && state.getValue(WATER_LEVEL) > 0) {
            self.scheduleTick(pos, state.getFluidState().getType(), state.getFluidState().getType().getTickDelay(self));
        }
    }
}
