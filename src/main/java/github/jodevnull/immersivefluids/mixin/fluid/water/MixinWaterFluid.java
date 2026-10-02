package github.jodevnull.immersivefluids.mixin.fluid.water;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public class MixinWaterFluid
{
    @Inject(at = @At("HEAD"), method = "canConvertToSource", cancellable = true)
    private void isInfinite(CallbackInfoReturnable<Boolean> bruh) {
        bruh.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "getTickDelay", cancellable = true)
    private void getTickRate(LevelReader level, CallbackInfoReturnable<Integer> bruh) {
        bruh.setReturnValue(2);
    }
}