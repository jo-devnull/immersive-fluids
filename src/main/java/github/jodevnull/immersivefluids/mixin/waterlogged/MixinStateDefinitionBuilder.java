package github.jodevnull.immersivefluids.mixin.waterlogged;

import github.jodevnull.immersivefluids.ModConfig;
import github.jodevnull.immersivefluids.WaterPhysics;
import github.jodevnull.immersivefluids.properties.WaterProperties;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Function;

@Mixin(StateDefinition.Builder.class)
public class MixinStateDefinitionBuilder<O, S extends StateHolder<O, S>>
{
    @Shadow
    @Final
    private O owner;

    @Shadow
    @Final
    private Map<String, Property<?>> properties;

    @Inject(method = "create", at = @At("HEAD"))
    private void ifc$addWaterLevelProperty(Function<O, S> defaultStateGetter, StateDefinition.Factory<O, S> factory, CallbackInfoReturnable<StateDefinition<O, S>> cir) {
        if (owner instanceof Block block && ifc$shouldAddBlock(block)) {
            if (!properties.containsKey("water_level"))
                properties.put("water_level", WaterPhysics.WATER_LEVEL);

            if (!properties.containsKey("evaporation"))
                properties.put("evaporation", WaterProperties.EVAPORATION);
        }
    }

    @Unique
    private boolean ifc$shouldAddBlock(Block block) {
        if (block instanceof AirBlock)
            return false;

        return block instanceof LeavesBlock
            || block instanceof CampfireBlock
            || block instanceof LanternBlock
            || block instanceof VineBlock
            || block instanceof WoolCarpetBlock
            || block instanceof GlowLichenBlock
            || block instanceof SugarCaneBlock
            || block instanceof LadderBlock
            || block instanceof IronBarsBlock
            || block instanceof FenceBlock
            || block instanceof FenceGateBlock
            || ModConfig.hasWaterLevel(block);
    }
}
