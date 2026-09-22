package github.jodevnull.immersivefluids.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import github.jodevnull.immersivefluids.features.CachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level
{
    protected MixinServerLevel(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Shadow
    public abstract ServerLevel getLevel();

    @Unique
    private int ifc$tickCounter = 0;

    @Inject(method = "tickPrecipitation", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V"))
    private void ifc$spawnRainWater(BlockPos blockPos, CallbackInfo ci, @Local(name="blockpos") BlockPos blockpos) {
        ifc$tickCounter++;

        if (ifc$tickCounter > 20)
            ifc$tickCounter = 0;
        else return;

        if (CachedWater.world == null)
            CachedWater.world = getLevel();

        if (!isRainingAt(blockpos))
            return;

        final var state = getBlockState(blockpos);
        final var below = getBlockState(blockpos.below());

        if (CachedWater.isNaturalWater(below) || below.is(Blocks.SNOW))
            return;

        if (below.hasProperty(BlockStateProperties.WATERLOGGED) && below.getValue(BlockStateProperties.WATERLOGGED))
            return;

        if (state.is(Blocks.AIR)) {
            CachedWater.addWater(1, blockpos);
        }
    }
}
