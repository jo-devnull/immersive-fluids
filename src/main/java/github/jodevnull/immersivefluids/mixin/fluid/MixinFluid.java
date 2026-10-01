package github.jodevnull.immersivefluids.mixin.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
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
        // TODO: implement this
        // if (((Object) this) instanceof WaterFluid) {
        //     cir.setReturnValue(true);
        // }
    }

    @Inject(method = "randomTick", at=@At("HEAD"))
    private void ifc$randomWaterTick(Level level, BlockPos pos, FluidState fluidstate, RandomSource random, CallbackInfo ci) {
    // TODO: implement this
    //    if (!ModConfig.evaporationEnabled())
    //        return;
    //
    //    if (((Object) this) instanceof WaterFluid) {
    //        if (getAmount(fluidstate) < 5) {
    //            final BlockState state = level.getBlockState(pos);
    //            final int evaporation = WaterUtils.getEvaporation(state);
    //
    //            if (evaporation > 1) {
    //                final int flags = Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
    //                level.setBlock(pos, WaterUtils.setEvaporation(state, evaporation - 1), flags);
    //            } else if (evaporation == 1) {
    //                if (CachedWater.world == null)
    //                    return;
    //
    //                final int water = getAmount(fluidstate);
    //
    //                if (water > 0 && water < 5) {
    //                    CachedWater.setWaterLevel(water - 1, pos);
    //                }
    //            }
    //        }
    //    }
    }
}
