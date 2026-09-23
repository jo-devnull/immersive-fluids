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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OpenEndedPipe.class)
public abstract class MixinOpenEndedPipe
{
    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    public abstract BlockPos getOutputPos();

    @Shadow
    private Level world;

    @ModifyExpressionValue(method = "removeFluidFromSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    public boolean ifc_allowNonSourceWater(boolean original, @Local(name = "fluidState") FluidState fluidState, @Local(name = "state") BlockState state) {
        return FluidHelper.isWater(fluidState.getType());
    }

    @WrapOperation(method = "removeFluidFromSpace", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/material/Fluid;I)Lnet/minecraftforge/fluids/FluidStack;"), remap = false)
    public FluidStack ifc_modifyFluidStack(Fluid fluid, int amount, Operation<FluidStack> original) {
        if (FluidHelper.isWater(fluid) && CachedWater.world != null)
            if (CachedWater.getWaterLevel(getOutputPos()) > 0)
                return new FluidStack(fluid, 125);

        return original.call(fluid, amount);
    }

    @WrapOperation(method = "removeFluidFromSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    public boolean ifc_removeWater(Level instance, BlockPos p_46601_, BlockState p_46602_, int p_46603_, Operation<Boolean> original, @Local(name = "fluidState") FluidState fluidState) {
        if (FluidHelper.isWater(fluidState.getType()) && CachedWater.world != null) {
            final int waterLevel = CachedWater.getWaterLevel(getOutputPos());

            if (waterLevel > 0) {
                CachedWater.setWaterLevel(waterLevel - 1, getOutputPos());
                return true;
            }
        }

        return original.call(instance, p_46601_, p_46602_, p_46603_);
    }

    @ModifyExpressionValue(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    public boolean ifc_isSourceCheck(boolean original, @Local(name = "state") BlockState state) {
        if (CachedWater.isWater(state))
            return false;

        return original;
    }

    @WrapOperation(method = "provideFluidToSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    public boolean ifc_placeWater(Level instance, BlockPos p_46601_, BlockState p_46602_, int p_46603_, Operation<Boolean> original, @Local(name = "fluid") FluidStack fluid) {
        if (CachedWater.world == null)
            return false;

        final int waterLevel = CachedWater.getWaterLevel(p_46601_);

        if (waterLevel > -1) {
            final int addedLevel = Math.floorDiv(fluid.getAmount() * 8, 1000);
            CachedWater.addWater(addedLevel, getOutputPos());
            return true;
        }

        return false;
    }
}
