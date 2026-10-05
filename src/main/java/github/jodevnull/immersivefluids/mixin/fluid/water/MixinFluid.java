package github.jodevnull.immersivefluids.mixin.fluid.water;

import github.jodevnull.immersivefluids.WaterConfig;
import github.jodevnull.immersivefluids.WaterUtils;
import github.jodevnull.immersivefluids.core.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Fluid.class)
public abstract class MixinFluid
{
    @Shadow
    public abstract int getAmount(FluidState p_76141_);

    @Inject(method = "isRandomlyTicking", at=@At("HEAD"), cancellable = true)
    private void ifc$enableRandomWaterTick(CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof WaterFluid) {
            cir.setReturnValue(true);
        }
    }

  @Inject(method = "randomTick", at=@At("HEAD"))
  private void ifc$randomWaterTick(Level level, BlockPos pos, FluidState fluidstate, RandomSource random, CallbackInfo ci) {
        // TODO: implement this
        if (!WaterConfig.fluidLifetimeEnabled())
            return;

        if (((Object) this) instanceof WaterFluid) {
            if (getAmount(fluidstate) < 5) {
                final int lifetime = WaterUtils.getLifetime(level, pos);

                if (lifetime == 0)
                    return;

                if (lifetime > 1) {
                    WaterUtils.setLifetime(level, pos, lifetime-1);
                } else if (lifetime == 1) {
                    if (CachedWater.world == null)
                        return;

                    final int water = getAmount(fluidstate);

                    if (water > 0 && water < 5) {
                        CachedWater.setWaterLevel(water-1, pos);
                    }
                }
            }
        }
  }
}
