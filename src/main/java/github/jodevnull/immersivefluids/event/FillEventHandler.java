package github.jodevnull.immersivefluids.event;

import github.jodevnull.immersivefluids.WaterUtils;
import github.jodevnull.immersivefluids.core.CachedWater;
import github.jodevnull.immersivefluids.recipe.FillRecipeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FillEventHandler
{
    public static void register() {}

    @SubscribeEvent
    public static void waterContainerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getSide().isClient())
            return;

        final var world = event.getLevel();
        final var player = event.getEntity();
        final var hand = event.getHand();
        final var pos = event.getPos().relative(event.getHitVec().getDirection());
        final var state = world.getBlockState(pos);

        if (player.getItemInHand(hand).isEmpty()) {
            return;
        }

        if (player.getItemInHand(hand).is(Items.STICK)) {
            final int lifetime = WaterUtils.getLifetime(world, pos);
            player.sendSystemMessage(Component.literal("LifeTime = " + lifetime));
            WaterUtils.setLifetime(world, pos, (lifetime+1) % (WaterUtils.MAX_LIFETIME+1));
            return;
        }

        if (CachedWater.world == null)
            return;

        if (!CachedWater.isWater(state) || WaterUtils.isNatural(state))
            return;

        if (FillRecipeHandler.handle((ServerLevel) event.getLevel(), player, hand, pos)) {
            event.setCanceled(true);
        }
    }
}
