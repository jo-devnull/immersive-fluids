package github.jodevnull.immersivefluids.mixin.fluid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.fluid.FluidHelper;
import github.jodevnull.immersivefluids.features.CachedWater;
import github.jodevnull.immersivefluids.features.RainFeature;
import github.jodevnull.immersivefluids.properties.WaterProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static github.jodevnull.immersivefluids.properties.WaterProperties.EVAPORATION;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin extends Block implements BucketPickup
{
    public LiquidBlockMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    @Inject(at=@At("HEAD"), method = "isRandomlyTicking", cancellable = true)
    private void ifc$enableRandomTickForWater(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        final var fluidState = state.getFluidState();

        cir.setReturnValue(!fluidState.isEmpty()
            && FluidHelper.isWater(fluidState.getType())
            && fluidState.getAmount() == 1
        );
    }

    @Inject(at=@At("HEAD"), method = "randomTick")
    private void ifc$doEvaporation(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (RainFeature.canEvaporate(level, pos, state)) {
            final int evaporation = state.getValue(EVAPORATION);

            if (evaporation == 1) {
                if (CachedWater.world != null) CachedWater.setWaterLevel(0, pos);
            } else {
                if (evaporation > 1) level.setBlock(pos, state.setValue(EVAPORATION, evaporation - 1), 3);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
    protected void appendProperties(StateDefinition.Builder<Fluid, FluidState> builder, CallbackInfo Ci) {
        builder.add(WaterProperties.EVAPORATION);
        builder.add(WaterProperties.ISFINITE);
    }

    @WrapOperation(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    public void ifc_injectProperties(LiquidBlock instance, BlockState blockState, Operation<Void> original) {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(LEVEL, 0).setValue(EVAPORATION, 0)
        );
    }

    @WrapOperation(
        method = "<init>(Lnet/minecraft/world/level/material/FlowingFluid;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    public void ifc_injectProperties2(LiquidBlock instance, BlockState blockState, Operation<Void> original) {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(LEVEL, 0).setValue(EVAPORATION, 0)
        );
    }
}