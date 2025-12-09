package kaede.reabista.item.Crystal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TeleportCrystal extends Item {

    public TeleportCrystal() {
        super(new Item.Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // =========================
    //     空中右クリック
    // =========================
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide) {
            teleportForward(player);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    // =========================
    //     ブロック右クリック
    // =========================
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!ctx.getLevel().isClientSide) {
            teleportForward(ctx.getPlayer());
        }
        return InteractionResult.SUCCESS;
    }

    // =========================
    //     プレイヤー前方TP
    // =========================
    private void teleportForward(Player player) {
        if (player == null) return;

        Vec3 look = player.getLookAngle();
        Vec3 newPos = player.position().add(look.scale(5)); // 5ブロック前へ

        player.teleportTo(newPos.x, newPos.y, newPos.z);
    }
}
