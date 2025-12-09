package kaede.reabista.client;

import kaede.reabista.client.gui.*;
import kaede.reabista.network.AbilityWeaponsClutch;
import kaede.reabista.network.FlyClutch;
import kaede.reabista.network.GetItemPacket;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.registry.ModAttributes;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_1;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_2;
import kaede.reabista.weapons.item.theusfall.Theusfall_1;
import kaede.reabista.weapons.item.theusfall.Theusfall_2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;

import static kaede.reabista.client.Keybinds.*;

@Mod.EventBusSubscriber(modid = "reabista", value = Dist.CLIENT)
public class ClientEvents {

    // キー入力イベントで処理
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 能力GUI
        if (OPEN_ABILITY_KEY.consumeClick()) {
            int ability = (int) mc.player.getAttributeValue(ModAttributes.ABILITY.get());
            switch (ability) {
                case 1 -> {
                    if (mc.player.getMainHandItem().getItem() instanceof Theusfall_1){
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(1, 1));
                    }else if (mc.player.getMainHandItem().getItem() instanceof Theusfall_2){
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(1, 2));
                    }else {
                        mc.setScreen(new EditGUI(mc.player));
                    }
                }
                case 2 -> {
                    if (mc.player.getMainHandItem().getItem() instanceof Thaosvenom_1){
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(2, 1));
                    }else if (mc.player.getMainHandItem().getItem() instanceof Thaosvenom_2){
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(2, 2));
                    }else {
                        mc.setScreen(new CopyGUI(mc.player));
                    }
                }
                case 3 -> NetworkHandler.sendToServer(new FlyClutch(!mc.player.getAbilities().mayfly));
                case 5 -> mc.setScreen(new GluttonyGUI(mc.player));
                case 6 -> mc.setScreen(new GuardGUI(mc.player));
            }
        }

        // ステータスGUI
        if (OPEN_STATUS_KEY.consumeClick()) {
            mc.setScreen(new StatusGUI(mc.player));
        }

        // 結晶化
        if (ABILITY_CRYSTAL_KEY.consumeClick()) {
            int ability = (int) mc.player.getAttributeValue(ModAttributes.ABILITY.get());
            switch (ability) {
                case 1 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(1));
                case 2 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(2));
                case 3 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(3));
                case 4 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(4));
                case 5 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(5));
                case 6 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(6));
            }
        }
    }
}