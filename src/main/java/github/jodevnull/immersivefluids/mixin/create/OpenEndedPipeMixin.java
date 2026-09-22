package github.jodevnull.immersivefluids.mixin.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.foundation.fluid.FluidHelper;
import github.jodevnull.immersivefluids.features.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OpenEndedPipe.class)
public abstract class OpenEndedPipeMixin
{
    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    public abstract BlockPos getOutputPos();

    @Shadow
    private Level world;

    @ModifyExpressionValue(method = "removeFluidFromSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    public boolean ifc_addFlowingWaterCheck(boolean original, @Local(name = "fluidState") FluidState fluidState, @Local(name = "state") BlockState state) {
        if (FluidHelper.isWater(fluidState.getType()))
            return true;

        return original;
    }

    @Inject(method = "removeFluidFromSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;createLegacyBlock()Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    public void ifc_removeFlowingWater(boolean simulate, CallbackInfoReturnable<FluidStack> cir, @Local(name = "stack") FluidStack stack) {
        if (simulate || CachedWater.world == null)
            return;

        if (FluidHelper.isWater(stack.getFluid())) {
            final int waterLevel = CachedWater.getWaterLevel(getOutputPos());

            if (waterLevel == 8) {
                CachedWater.setWaterLevel(0, getOutputPos());
                cir.setReturnValue(stack);
            }
            else if (waterLevel > 0) {
                CachedWater.setWaterLevel(waterLevel - 1, getOutputPos());
                cir.setReturnValue(new FluidStack(stack.getFluid(), 125));
            }
        }
    }

    @ModifyExpressionValue(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    public boolean ifc_isSourceCheck(boolean original, @Local(name = "state") BlockState state) {
        if (CachedWater.isWater(state))
            return false;

        return original;
    }

    @WrapOperation(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    public boolean ifc_placeWater(Level instance, BlockPos pos, BlockState newState, int flags, Operation<Boolean> original, @Local(name = "fluid") FluidStack fluid) {
        if (CachedWater.world == null)
            return false;

        final int waterLevel = CachedWater.getWaterLevel(pos);

        if (waterLevel > -1) {
            final int addedLevel = Math.floorDiv(fluid.getAmount() * 8, 1000);
            CachedWater.addWater(addedLevel, getOutputPos());
            return true;
        }

        return false;
    }
}
