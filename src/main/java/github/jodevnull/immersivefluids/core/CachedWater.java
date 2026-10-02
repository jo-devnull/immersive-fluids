package github.jodevnull.immersivefluids.core;

import com.simibubi.create.foundation.fluid.FluidHelper;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.mixin.extensions.ServerChunkCacheExtension;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongToIntFunction;

import static github.jodevnull.immersivefluids.WaterUtils.ISFINITE;

public class CachedWater
{
    public static Level world;
    public static boolean useCache = true;
    public static boolean useSections = true;

    private static final Long2ByteMap cache = new Long2ByteOpenHashMap();
    private static final Map<SectionPos, LevelChunkSection> sections = new HashMap<>();
    private static final Long2ByteMap queuedWaterLevels = new Long2ByteOpenHashMap();
    public static final Map<BlockPos, FluidState> fluidsToUpdate = new HashMap<>();

    public static int a = 0;

    static {
        queuedWaterLevels.defaultReturnValue((byte) -1);
    }

    public static int countMa() {
        a += 1;
        return a;
    }

    public static int getWaterLevel(BlockPos ipos) {
        LongToIntFunction func = pos -> {
            BlockState state = getBlockState(BlockPos.of(pos));
            return (byte) getWaterLevelOf(ipos, state);
        };

        if (useCache) {
            return cache.computeIfAbsent(ipos.asLong(), func);
        } else return func.applyAsInt(ipos.asLong());
    }

    public static boolean isInfinite(BlockPos pos) {
        BlockState state = getBlockState(pos);
        return state.hasProperty(ISFINITE) && !state.getValue(ISFINITE);
    }

    public static boolean isNotFull(int waterLevel) {
        return waterLevel < 8 && waterLevel >= 0;
    }

    public static boolean isNotFull(BlockPos pos) {
        return isNotFull(getWaterLevel(pos));
    }

    public static int getWaterLevelOf(BlockPos pos, BlockState state) {
        if (state.isAir())
            return (byte) 0;
        if (state.hasProperty(ISFINITE) && !state.getValue(ISFINITE)) {
            return (byte) -2;
        }

        // TODO: implement this with fluidlogged [done]
        final FluidState fluidstate = world.getFluidState(pos);

        if (fluidstate == Fluids.EMPTY.defaultFluidState() || state.getBlock() == Blocks.LAVA)
            return (byte) -1;

        return fluidstate.isSource() ? 8 : fluidstate.getAmount();
    }

    public static int getWaterLevelForPF(BlockPos pos) {
        BlockState state = getBlockState(pos);

        if (state.isAir())
            return (byte) 0;
        if (state.hasProperty(ISFINITE) && !state.getValue(ISFINITE)) {
            return (byte) 1;
        }

        // TODO: implement this with fluidlogged [done]
        FluidState fluidstate = world.getFluidState(pos);

        if (fluidstate == Fluids.EMPTY.defaultFluidState())
            return (byte) -1;

        return fluidstate.isSource() ? 8 : fluidstate.getAmount();
    }

    public static boolean isWater(BlockPos pos) {
        return FluidHelper.isWater(getFluidState(pos).getType());
    }

    public static void setWaterLevel(int level, BlockPos pos) {
        if (useCache) {
            cache.put(pos.asLong(), (byte) level);
            queuedWaterLevels.put(pos.asLong(), (byte) level);
        } else {
            setWaterLevelDirect(level, pos);
            cache.remove(pos.asLong());
        }
    }

    private static void setWaterLevelDirect(int level, BlockPos pos) {
        final var prev = getBlockState(pos);

        if (level < 0 || level > 8)
            return;

        if (!prev.isAir() && !Fluidlogged.isFluidloggable(prev))
            return;

        // FIXME: implement this with fluidlogged
        if (level == 0) {
            setFluidStateNoNeighbors(pos, Fluids.EMPTY.defaultFluidState());
        } else {
            setFluidStateNoNeighbors(pos, Fluids.WATER.getFlowing(level, false));
        }
    }

    public static void addWater(int level, BlockPos pos) {
        int existingWater = getWaterLevel(pos);

        if (existingWater == -1)
            throw new IllegalStateException("Tried to add water to a full block");

        int totalWater = existingWater + level;

        if (totalWater > 8) {
            addWater(totalWater - 8, pos.above());
            setWaterLevel(8, pos);
        } else {
            setWaterLevel(totalWater, pos);
        }
    }

    public static BlockState getBlockState(BlockPos pos) {
        if (useSections) {
            if (pos.getY() < world.getMinBuildHeight() || pos.getY() > world.getMaxBuildHeight()) {
                return Blocks.AIR.defaultBlockState();
            }

            return getBlockStateSection(pos);
        } else {
            return world.getBlockState(pos);
        }
    }

    public static BlockState getBlockStateSection(BlockPos pos) {
        return sections.computeIfAbsent(SectionPos.of(pos), CachedWater::getChunkSection)
            .getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static FluidState getFluidState(BlockPos pos) {
        if (useSections) {
            if (pos.getY() < world.getMinBuildHeight() || pos.getY() > world.getMaxBuildHeight()) {
                return Fluids.EMPTY.defaultFluidState();
            }

            return getFluidStateSection(pos);
        } else {
            return world.getFluidState(pos);
        }
    }

    public static FluidState getFluidStateSection(BlockPos pos) {
        return ((LevelChunkSectionExtension) sections.computeIfAbsent(SectionPos.of(pos), CachedWater::getChunkSection))
            .getFluidStateExact(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static void setFluidStateSection(BlockPos pos, FluidState state) {
        ((ServerChunkCacheExtension) ((ServerLevel) world).getChunkSource()).fluidChanged(pos);
        ((LevelChunkSectionExtension) sections.computeIfAbsent(SectionPos.of(pos), CachedWater::getChunkSection))
            .setFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
    }

    public static LevelChunkSection getChunkSection(SectionPos pos) {
        LevelChunkSection result = world.getChunk(pos.center()).getSections()[world.getSectionIndexFromSectionY(pos.getY())];
        result.release(); // FIXME
        result.acquire();
        return result;
    }

    public static void setFluidStateNoNeighbors(BlockPos pos, FluidState newFluid) {
        if (!newFluid.isEmpty() && useSections) {
            final int flags = Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
            setFluidStateSection(pos, newFluid);
            ((LevelExtension) world).sendFluidUpdated(pos, flags);
            fluidsToUpdate.put(pos, newFluid);
        } else if (newFluid.isEmpty()) {
            final int flags = Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
            setFluidStateSection(pos, newFluid);
            ((LevelExtension) world).sendFluidUpdated(pos, flags);
        } else {
            final int flags = Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS;
            ((LevelExtension) world).setFluid(pos, newFluid, flags);
        }
    }

    public static void setup(ServerLevel world, BlockPos fluidPos) {
        CachedWater.world = world;
    }

    public static void beforeTick(ServerLevel serverWorld) {
        assert cache.isEmpty(); //FIXME
    }

    /**
     * The majority of time spent in setBlockState is spent updating neighbors, which, when a lot of water is moving,
     * is mostly just fluid updating fluid.
     * <p>
     * Since fluid updates don't care about the source block, we can queue them all and run only once
     * (rather than each setBlock potentially running up to 6 neighbor updates)
     */
    private static void updateNeighbor(BlockPos pos, BlockPos neighborPos) {
        final FluidState neighborState = getFluidState(pos);

        if (!neighborState.isEmpty()) {
            fluidsToUpdate.put(pos, world.getFluidState(neighborPos));
        }
    }

    public static void afterTick(ServerLevel serverWorld) {
        // TODO cache per dimension
        cache.clear();

        for (var entry : queuedWaterLevels.long2ByteEntrySet()) {
            BlockPos pos = BlockPos.of(entry.getLongKey());

            setWaterLevelDirect(entry.getByteValue(), pos);
            updateNeighbor(pos.west(), pos);
            updateNeighbor(pos.east(), pos);
            updateNeighbor(pos.below(), pos);
            updateNeighbor(pos.above(), pos);
            updateNeighbor(pos.north(), pos);
            updateNeighbor(pos.south(), pos);
        }

        for (var entry : fluidsToUpdate.entrySet()) {
            var state = entry.getValue();
            var pos = entry.getKey();
            world.scheduleTick(pos, state.getType(), state.getType().getTickDelay(world));
        }

        sections.forEach((sectionPos, section) -> section.release());
        fluidsToUpdate.clear();
        queuedWaterLevels.clear();
        sections.clear();
    }
}
