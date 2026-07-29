package kaede.reabista.item.Crystal;

import kaede.reabista.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EditCrystal extends Item {
    public EditCrystal() {
        super(new Item.Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return; // サーバー側のみ処理

        Player player = event.getEntity();           // 右クリックしたプレイヤー
        Entity target = event.getTarget();           // 右クリックされたエンティティ
        ItemStack held = player.getItemInHand(event.getHand());

        // メインハンドにこのアイテムを持っていない場合は無視
        if (held.isEmpty() || held.getItem() != ModItems.EDIT_CRYSTAL.get()) return;

        // エンティティを消滅
        target.discard();

        // アイテムを1個消費
        held.shrink(1);

        // イベントをキャンセルして右クリック処理を防ぐ
        event.setCanceled(true);
    }
}