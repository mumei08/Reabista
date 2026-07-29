package kaede.reabista.client;

import kaede.reabista.client.gui.*;
import kaede.reabista.item.Crystal.*;
import kaede.reabista.network.ability.item.AbilityWeaponsClutch;
import kaede.reabista.network.ability.FlyClutch;
import kaede.reabista.network.ability.LightningClutch;
import kaede.reabista.network.ability.StrengthClutch;
import kaede.reabista.network.ability.CloneClutch;
import kaede.reabista.network.ability.SmokeClutch;
import kaede.reabista.network.ability.BinaryClutch;
import kaede.reabista.network.ability.EraseClutch;
import kaede.reabista.network.ability.DestructionClutch;
import kaede.reabista.network.ability.item.GetItemPacket;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.ability.item.GetWeaponPacket;
import kaede.reabista.registry.ModAttributes;
import kaede.reabista.weapons.item.thaosvenom.GodThaosvenom_1;
import kaede.reabista.weapons.item.thaosvenom.GodThaosvenom_2;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_1;
import kaede.reabista.weapons.item.thaosvenom.Thaosvenom_2;
import kaede.reabista.weapons.item.theusfall.GodTheusfall_1;
import kaede.reabista.weapons.item.theusfall.GodTheusfall_2;
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
            if (mc.player.getMainHandItem().getItem() instanceof EditCrystal){
                mc.setScreen(new EditGUI(mc.player));
            }else if (mc.player.getMainHandItem().getItem() instanceof CopyCrystal){
                mc.setScreen(new CopyGUI(mc.player));
            }else if (mc.player.getOffhandItem().getItem() instanceof EditCrystal){
                mc.setScreen(new EditGUI(mc.player));
            }else if (mc.player.getOffhandItem().getItem() instanceof CopyCrystal){
                mc.setScreen(new CopyGUI(mc.player));
            }else if (tryActivateCrystal(mc)){
                // 結晶所持による即時発動(飛行〜破壊まで)は tryActivateCrystal 内で処理済み
            }else{
                if (ability == 1){
                    if (mc.player.getMainHandItem().getItem() instanceof GodTheusfall_1){
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(1, 1));
                    }else if (mc.player.getMainHandItem().getItem() instanceof GodTheusfall_2) {
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(1, 2));
                    }else{
                        mc.setScreen(new EditGUI(mc.player));
                    }
                }else if (ability == 2) {
                    if (mc.player.getMainHandItem().getItem() instanceof GodThaosvenom_1) {
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(2, 1));
                    } else if (mc.player.getMainHandItem().getItem() instanceof GodThaosvenom_2) {
                        NetworkHandler.sendToServer(new AbilityWeaponsClutch(2, 2));
                    } else {
                        mc.setScreen(new CopyGUI(mc.player));
                    }
                }else {
                    switch (ability) {
                        case 3 -> NetworkHandler.sendToServer(new FlyClutch());
                        case 5 -> mc.setScreen(new GluttonyGUI(mc.player));
                        case 6 -> mc.setScreen(new GuardGUI(mc.player));
                        case 7 -> mc.setScreen(new HealerGUI(mc.player));
                        case 8 -> NetworkHandler.sendToServer(new LightningClutch());
                        case 9 -> NetworkHandler.sendToServer(new StrengthClutch());
                        case 10 -> NetworkHandler.sendToServer(new CloneClutch());
                        case 11 -> NetworkHandler.sendToServer(new SmokeClutch());
                        case 12 -> {
                            if (mc.hitResult instanceof net.minecraft.world.phys.EntityHitResult) {
                                NetworkHandler.sendToServer(new BinaryClutch());
                            } else {
                                mc.setScreen(new kaede.reabista.client.gui.BinaryWriterGUI(mc.player));
                            }
                        }
                        case 13 -> NetworkHandler.sendToServer(new EraseClutch());
                        case 14 -> mc.setScreen(new kaede.reabista.client.gui.CreationGUI(mc.player));
                        case 15 -> NetworkHandler.sendToServer(new DestructionClutch());
                    }
                }
            }
        }

        // ステータスGUI
        if (OPEN_STATUS_KEY.consumeClick()) {
            mc.setScreen(new StatusGUI(mc.player));
        }

        // 結晶化
        if (ABILITY_CRYSTAL_KEY.consumeClick()) {
            int ability = (int) mc.player.getAttributeValue(ModAttributes.ABILITY.get());
            if (ability == 1){
                if (mc.player.getMainHandItem().getItem() instanceof Theusfall_1){
                    NetworkHandler.CHANNEL.sendToServer(new GetWeaponPacket(1));
                }else if (mc.player.getMainHandItem().getItem() instanceof Theusfall_2) {
                    NetworkHandler.CHANNEL.sendToServer(new GetWeaponPacket(2));
                }else{
                    NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(1));
                }
            }else if (ability == 2) {
                if (mc.player.getMainHandItem().getItem() instanceof Thaosvenom_1) {
                    NetworkHandler.CHANNEL.sendToServer(new GetWeaponPacket(3));
                } else if (mc.player.getMainHandItem().getItem() instanceof Thaosvenom_2) {
                    NetworkHandler.CHANNEL.sendToServer(new GetWeaponPacket(4));
                } else {
                    NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(2));
                }
            }else {
                switch (ability) {
                    case 3 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(3));
                    case 4 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(4));
                    case 5 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(5));
                    case 6 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(6));
                    case 7 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(7));
                    case 8 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(8));
                    case 9 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(9));
                    case 10 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(10));
                    case 11 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(11));
                    case 12 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(12));
                    case 13 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(13));
                    case 14 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(14));
                    case 15 -> NetworkHandler.CHANNEL.sendToServer(new GetItemPacket(15));
                }
            }
        }

        if (CHANGE_WEAPON_KEY.consumeClick()) {
            if (mc.player.getMainHandItem().getItem() instanceof Theusfall_1){
                NetworkHandler.sendToServer(new AbilityWeaponsClutch(0, 1));
            }else if (mc.player.getMainHandItem().getItem() instanceof Theusfall_2) {
                NetworkHandler.sendToServer(new AbilityWeaponsClutch(0, 2));
            }else if (mc.player.getMainHandItem().getItem() instanceof Thaosvenom_1) {
                NetworkHandler.sendToServer(new AbilityWeaponsClutch(0, 3));
            } else if (mc.player.getMainHandItem().getItem() instanceof Thaosvenom_2) {
                NetworkHandler.sendToServer(new AbilityWeaponsClutch(0, 4));
            }
        }
    }

    /**
     * メイン/オフハンドが能力結晶(飛行〜破壊)なら該当アクションを即時実行する。
     * 実行した場合はtrueを返す(EditCrystal/CopyCrystalは呼び出し元で個別処理済みのため対象外)。
     */
    private static boolean tryActivateCrystal(Minecraft mc) {
        var main = mc.player.getMainHandItem().getItem();
        var off = mc.player.getOffhandItem().getItem();

        if (main instanceof FlyCrystal || off instanceof FlyCrystal) {
            NetworkHandler.sendToServer(new FlyClutch());
        } else if (main instanceof GluttonyCrystal || off instanceof GluttonyCrystal) {
            mc.setScreen(new GluttonyGUI(mc.player));
        } else if (main instanceof GuardCrystal || off instanceof GuardCrystal) {
            mc.setScreen(new GuardGUI(mc.player));
        } else if (main instanceof HealerCrystal || off instanceof HealerCrystal) {
            mc.setScreen(new HealerGUI(mc.player));
        } else if (main instanceof LightningCrystal || off instanceof LightningCrystal) {
            NetworkHandler.sendToServer(new LightningClutch());
        } else if (main instanceof StrengthCrystal || off instanceof StrengthCrystal) {
            NetworkHandler.sendToServer(new StrengthClutch());
        } else if (main instanceof CloneCrystal || off instanceof CloneCrystal) {
            NetworkHandler.sendToServer(new CloneClutch());
        } else if (main instanceof SmokeCrystal || off instanceof SmokeCrystal) {
            NetworkHandler.sendToServer(new SmokeClutch());
        } else if (main instanceof BinaryCrystal || off instanceof BinaryCrystal) {
            if (mc.hitResult instanceof net.minecraft.world.phys.EntityHitResult) {
                NetworkHandler.sendToServer(new BinaryClutch());
            } else {
                mc.setScreen(new kaede.reabista.client.gui.BinaryWriterGUI(mc.player));
            }
        } else if (main instanceof EraseCrystal || off instanceof EraseCrystal) {
            NetworkHandler.sendToServer(new EraseClutch());
        } else if (main instanceof CreationCrystal) {
            net.minecraft.world.phys.BlockHitResult hit =
                    mc.hitResult instanceof net.minecraft.world.phys.BlockHitResult bhr ? bhr : null;
            mc.setScreen(new kaede.reabista.client.gui.CreationBlockGUI(mc.player, hit));
        } else if (off instanceof CreationCrystal) {
            mc.setScreen(new kaede.reabista.client.gui.CreationGUI(mc.player));
        } else if (main instanceof DestructionCrystal || off instanceof DestructionCrystal) {
            NetworkHandler.sendToServer(new DestructionClutch());
        } else {
            return false;
        }
        return true;
    }
}