package github.jodevnull.immersivefluids.mixin.fluid.water;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import github.jodevnull.immersivefluids.WaterUtils;
import github.jodevnull.immersivefluids.core.simulation.FlowWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public class MixinFlowingFluid
{
    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    @Inject(
        at = {@At("HEAD")},
        method = {"spread"},
        cancellable = true
    )
    private void tryFlow(Level level, BlockPos pos, FluidState state, CallbackInfo bruh) {
        if (ifc$isWater(state.getType()) && !WaterUtils.isNatural(level.getBlockState(pos))) {
            FlowWater.flowWater(level, pos, state);
            bruh.cancel();
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"getNewLiquid"},
        cancellable = true
    )
    private void getUpdatedState(Level level, BlockPos pos, BlockState blockState, CallbackInfoReturnable<FluidState> bruh) {
        FluidState fluidstate = blockState.getFluidState();
        if (ifc$isWater(fluidstate.getType()) && !WaterUtils.isNatural(level.getBlockState(pos))) {
            bruh.setReturnValue(Fluids.FLOWING_WATER.getFlowing(blockState.getFluidState().getAmount(), false));
        }
    }

    @WrapMethod(method = "getFlow")
    public Vec3 ifc$getFlow(BlockGetter blockReader, BlockPos pos, FluidState fluidState, Operation<Vec3> original) {
        if (!WaterUtils.isNatural(blockReader.getBlockState(pos)))
            return Vec3.ZERO;
        else
            return original.call(blockReader, pos, fluidState);
    }

    @Inject(method = "canConvertToSource(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", at=@At("HEAD"), cancellable = true, remap = false)
    private void ifc$canConvertToSource(FluidState state, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(WaterUtils.isNatural(level.getBlockState(pos)));
    }

    @Inject(method = "createFluidStateDefinition", at=@At("TAIL"))
    private void ifc$addWaterLevelProperty(StateDefinition.Builder<Fluid, FluidState> builder, CallbackInfo ci) {
        if (((Object) this) instanceof WaterFluid.Source) {
            builder.add(LEVEL);
        }
    }

    @ModifyReturnValue(method = "getSource(Z)Lnet/minecraft/world/level/material/FluidState;", at=@At("RETURN"))
    private FluidState ifc$getSource(FluidState original) {
        if (original.hasProperty(LEVEL)) {
            return original.setValue(FlowingFluid.LEVEL, 8);
        }

        return original;
    }

    @Unique
    public boolean ifc$isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }

}