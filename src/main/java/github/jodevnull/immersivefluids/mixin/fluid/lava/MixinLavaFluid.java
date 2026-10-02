package github.jodevnull.immersivefluids.mixin.fluid.lava;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.LavaFluid.class)
public class MixinLavaFluid
{
    @Inject(at = @At("HEAD"), method = "canConvertToSource", cancellable = true)
    private void isInfinite(CallbackInfoReturnable<Boolean> lbruh) {
        lbruh.setReturnValue(false);
    }
}