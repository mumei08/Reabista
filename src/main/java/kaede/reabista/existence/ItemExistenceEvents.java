package kaede.reabista.existence;

import kaede.reabista.Reabista;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * アイテムのexistence初期化・管理イベント
 */
@Mod.EventBusSubscriber(modid = Reabista.MODID)
public class ItemExistenceEvents {

    /**
     * アイテムがドロップされた時、existenceを初期化
     */
    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        Player player = event.getPlayer();
        ItemEntity itemEntity = event.getEntity();
        ItemStack stack = itemEntity.getItem();

        String currentWorldId = getCurrentWorldId(player);
        if (currentWorldId != null && !ItemExistenceHelper.hasExistenceData(stack)) {
            ItemExistenceHelper.initializeExistence(stack, currentWorldId);
        }
    }

    /**
     * アイテムを拾った時、existenceがなければ初期化
     */
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItem().getItem();

        String currentWorldId = getCurrentWorldId(player);
        if (currentWorldId != null && !ItemExistenceHelper.hasExistenceData(stack)) {
            ItemExistenceHelper.initializeExistence(stack, currentWorldId);
        }
    }

    /**
     * クラフト時にexistenceを初期化
     */
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getCrafting();

        String currentWorldId = getCurrentWorldId(player);
        if (currentWorldId != null) {
            ItemExistenceHelper.initializeExistence(stack, currentWorldId);
        }
    }

    /**
     * プレイヤーの現在ワールドIDを取得
     */
    private static String getCurrentWorldId(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.serverLevel().dimension().location().getPath();
        }
        return null;
    }
}
