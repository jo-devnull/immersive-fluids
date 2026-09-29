package github.jodevnull.immersivefluids.mixin.waterlogged;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static github.jodevnull.immersivefluids.WaterPhysics.WATER_LEVEL;

@Mixin(BlockItem.class)
public class MixinBlockItemPlacement
{
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void ifc$waterlogOnPlace(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();

        if (state == null)
            return;

        if (!state.hasProperty(WATER_LEVEL))
            return;

        final int waterLevel = context.getLevel().getFluidState(context.getClickedPos()).getAmount();

        if (waterLevel > 0) {
            if (state.hasProperty(BlockStateProperties.WATERLOGGED)
                && !state.getValue(BlockStateProperties.WATERLOGGED)
                && waterLevel == 8
            ) {
                cir.setReturnValue(state
                    .setValue(WATER_LEVEL, waterLevel)
                    .setValue(BlockStateProperties.WATERLOGGED, true));
            } else {
                cir.setReturnValue(state.setValue(WATER_LEVEL, waterLevel));
            }
        }
    }
}
