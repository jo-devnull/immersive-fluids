package github.jodevnull.immersivefluids.mixin.waterlogged;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.jodevnull.immersivefluids.properties.WaterProperties;
import github.jodevnull.immersivefluids.properties.WaterUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public class MixinBlockPlacement
{
    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState ifc$fixStatePlacement(BlockState original) {
        return WaterUtils.setEvaporation(original, WaterProperties.MAX_EVAPORATION);
    }
}
