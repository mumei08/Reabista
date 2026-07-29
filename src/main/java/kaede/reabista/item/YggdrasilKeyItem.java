package kaede.reabista.item;

import kaede.reabista.events.YggdrasillTeleportEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

public class YggdrasilKeyItem extends Item {
    public YggdrasilKeyItem() {
        super(new Item.Properties()
                .fireResistant()
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        YggdrasillTeleportEvent.teleportToYggdrasill(player);
        return InteractionResult.SUCCESS;
    }
}