package github.jodevnull.immersivefluids;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModConfig
{
    private static final List<String> DEFAULT_BLOCKS = List.of();

    private static boolean m_WaterEvaporation = true;
    private static boolean m_RainPuddles = true;
    private static int m_RainPuddlesInterval = 15;
    private static final Set<Block> m_waterFlowsThrough = new HashSet<>();

    public static boolean hasWaterLevel(Block block) {
        return m_waterFlowsThrough.contains(block);
    }

    public static boolean hasWaterLevel(BlockState state) {
        return m_waterFlowsThrough.contains(state.getBlock());
    }

    public static boolean evaporationEnabled() {
        return m_WaterEvaporation;
    }

    public static boolean rainPuddlesEnabled() {
        return m_RainPuddles;
    }

    public static int rainPuddleTickInterval() {
        return m_RainPuddlesInterval;
    }

    public static void reload() {
        final var configDir = FMLPaths.getOrCreateGameRelativePath(Path.of("config"));
        final var configPath = configDir.resolve("immersivefluids.json");

        var config = getDefaultConfig();

        if (!configPath.toFile().exists()) {
            try {
                FileUtils.writeStringToFile(configPath.toFile(), WaterPhysics.GSON.toJson(config), StandardCharsets.UTF_8);
            } catch (Exception exception) {
                WaterPhysics.LOGGER.error("Error writing default config:");
                WaterPhysics.LOGGER.error(String.valueOf(exception));
            }
        } else {
            try {
                final var json = FileUtils.readFileToString(configPath.toFile(), StandardCharsets.UTF_8);
                config = JsonParser.parseString(json).getAsJsonObject();
            } catch (Exception exception) {
                WaterPhysics.LOGGER.error("Error reading config:");
                WaterPhysics.LOGGER.error(String.valueOf(exception));
            }
        }

        read(config);
    }

    private static void read(JsonObject config) {
        if (config.has("waterEvaporation"))
            m_WaterEvaporation = config.get("waterEvaporation").getAsBoolean();

        if (config.has("rainPuddles"))
            m_RainPuddles = config.get("rainPuddles").getAsBoolean();

        if (config.has("rainPuddlesInterval"))
            m_RainPuddlesInterval = config.get("rainPuddlesInterval").getAsInt();

        if (config.has("waterFlowsThrough")) {
            m_waterFlowsThrough.clear();

            final var blockList = config.get("waterFlowsThrough")
                .getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .toList();

            for (var str : blockList) {
                final var rl = ResourceLocation.tryParse(str);

                if (rl == null)
                    WaterPhysics.LOGGER.warn("Invalid block id: {}", str);

                final var block = ForgeRegistries.BLOCKS.getValue(rl);
                m_waterFlowsThrough.add(block);
            }

            WaterPhysics.LOGGER.info("Loaded {} blocks", m_waterFlowsThrough.size());
        }
    }

    public static JsonObject getDefaultConfig() {
        final var config = new JsonObject();
        final var blockList = new JsonArray();

        for (var str : defaultBlockList)
            blockList.add(str);

        config.addProperty("waterEvaporation", m_WaterEvaporation);
        config.addProperty("rainPuddles", m_RainPuddles);
        config.addProperty("rainPuddlesInterval", m_RainPuddlesInterval);
        config.add("waterFlowsThrough", blockList);

        return config;
    }

    private static boolean validateBlockId(Object obj) {
        if (obj instanceof String str) {
            return ResourceLocation.tryParse(str) != null;
        }

        return false;
    }

    private static final List<String> defaultBlockList = List.of();
}
