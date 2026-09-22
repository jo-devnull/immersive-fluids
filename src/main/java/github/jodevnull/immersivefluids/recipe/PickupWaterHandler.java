package github.jodevnull.immersivefluids.recipe;

import github.jodevnull.immersivefluids.features.CachedWater;
import github.jodevnull.immersivefluids.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class PickupWaterHandler
{
    public static boolean handle(ServerLevel level, Player player, InteractionHand hand, BlockPos pos) {
        final var input = player.getItemInHand(hand);
        final var container = new PickupWaterRecipeInput(input);

        final var recipeOpt =
            level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.PICKUP_WATER.get(),
                container,
                level
            );

        if (recipeOpt.isEmpty())
            return false;

        final var recipe = recipeOpt.get().value();
        final var minWaterRequired = recipe.getAmount();
        final var waterLevel = CachedWater.getWaterLevel(pos);

        if (waterLevel < minWaterRequired)
            return false;

        CachedWater.setWaterLevel(waterLevel - minWaterRequired, pos);
        player.getItemInHand(hand).shrink(1);
        player.getInventory().add(recipe.getOutput().copy());

        return true;
    }
}