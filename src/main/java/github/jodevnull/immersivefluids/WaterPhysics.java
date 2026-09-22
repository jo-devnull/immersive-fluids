package github.jodevnull.immersivefluids;

import com.mojang.logging.LogUtils;
import github.jodevnull.immersivefluids.features.NonCachedWater;
import github.jodevnull.immersivefluids.registry.ModRecipeSerializers;
import github.jodevnull.immersivefluids.registry.ModRecipeTypes;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(WaterPhysics.MODID)
public class WaterPhysics
{
    public static final String MODID = "immersivefluids";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final IntegerProperty WATER_LEVEL = IntegerProperty.create("water_level", 0, 8);

    public WaterPhysics(IEventBus modEventBus, ModContainer modContainer) {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Immersive Fluids loaded!");
        NeoForge.EVENT_BUS.addListener(this::registerCommand);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
    }

    private void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("waterlevel")
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(context -> {
                    try {
                        int result = NonCachedWater.getWaterLevel(BlockPosArgument.getSpawnablePos(context, "pos"), context.getSource().getLevel());
                        context.getSource().sendSuccess((Supplier<Component>) Component.nullToEmpty("Water level at " + BlockPosArgument.getSpawnablePos(context, "pos") + " is " + result), false);
                        return result;
                    } catch (Exception e) {
                        context.getSource().sendFailure(Component.nullToEmpty("AA Something went wrong"));
                        LOGGER.error(String.valueOf(e));
                        return -9999;
                    }
                })));
    }

}
