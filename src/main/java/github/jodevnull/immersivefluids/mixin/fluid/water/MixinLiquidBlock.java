package github.jodevnull.immersivefluids.mixin.fluid.water;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import github.jodevnull.immersivefluids.WaterUtils;
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

@Mixin(LiquidBlock.class)
public abstract class MixinLiquidBlock extends Block implements BucketPickup
{
    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    public MixinLiquidBlock(Properties properties) {
        super(properties);
    }

    @WrapOperation(
        method = "<init>*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    public void ifc_injectProperties(LiquidBlock instance, BlockState blockState, Operation<Void> original) {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LEVEL, 0)
                .setValue(WaterUtils.LIFETIME, 0)
        );
    }

    @Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
    protected void appendProperties(StateDefinition.Builder<Fluid, FluidState> builder, CallbackInfo Ci) {
        builder.add(WaterUtils.LIFETIME);
    }
}