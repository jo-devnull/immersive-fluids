package github.jodevnull.immersivefluids.mixin.waterlogged;

import github.jodevnull.immersivefluids.properties.WaterProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static github.jodevnull.immersivefluids.WaterPhysics.WATER_LEVEL;

@Mixin(StateDefinition.class)
public class MixinStateDefinitionAny<O, S extends StateHolder<O, S>>
{
    @Inject(method = "any", at = @At("RETURN"), cancellable = true)
    private void ifc$waterloggedDefault(CallbackInfoReturnable<S> cir) {
        final var state = cir.getReturnValue();
        if (state instanceof BlockState blockState && blockState.hasProperty(WATER_LEVEL)) {
            @SuppressWarnings("unchecked")
            var fixed = (S) blockState.setValue(WATER_LEVEL, 0);

            if (blockState.hasProperty(WaterProperties.EVAPORATION))
                fixed = fixed.setValue(WaterProperties.EVAPORATION, 0);

            cir.setReturnValue(fixed);
        }
    }
}
