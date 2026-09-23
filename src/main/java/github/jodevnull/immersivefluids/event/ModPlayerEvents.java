package github.jodevnull.immersivefluids.event;

import github.jodevnull.immersivefluids.features.CachedWater;
import github.jodevnull.immersivefluids.recipe.PickupWaterHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModPlayerEvents
{
    public static void register() {}

    @SubscribeEvent
    public static void waterContainerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getSide().isClient())
            return;

        final var player = event.getEntity();
        final var hand = event.getHand();
        final var pos = event.getPos().relative(event.getHitVec().getDirection());
        final var state = event.getLevel().getBlockState(pos);

        if (player.getItemInHand(hand).isEmpty())
            return;

        if (CachedWater.world == null)
            return;

        if (!CachedWater.isWater(state) || CachedWater.isNatural(state))
            return;

        if (PickupWaterHandler.handle((ServerLevel) event.getLevel(), player, hand, pos)) {
            event.setCanceled(true);
        }
    }
}
