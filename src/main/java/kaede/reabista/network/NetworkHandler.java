package kaede.reabista.network;

import kaede.reabista.Reabista;
import kaede.reabista.network.ability.*;
import kaede.reabista.network.ability.gui.AbilityChangePacket;
import kaede.reabista.network.ability.gui.OpenAbilityGuiPacket;
import kaede.reabista.network.ability.item.AbilityWeaponsClutch;
import kaede.reabista.network.ability.item.GetItemPacket;
import kaede.reabista.network.ability.item.GetWeaponPacket;
import kaede.reabista.network.status.StatusGuiButtonMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel CHANNEL;

    // 共通パケットのみ登録（サーバー・クライアント両方安全）
    public static void registerCommonPackets() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Reabista.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        int id = 0;

        CHANNEL.registerMessage(id++,
                StatusGuiButtonMessage.class,
                StatusGuiButtonMessage::encode,
                StatusGuiButtonMessage::decode,
                StatusGuiButtonMessage::handle
        );

        CHANNEL.registerMessage(id++,
                GuardAbilityPacket.class,
                GuardAbilityPacket::encode,
                GuardAbilityPacket::decode,
                GuardAbilityPacket::handle
        );

        CHANNEL.registerMessage(id++,
                HealAbilityPacket.class,
                HealAbilityPacket::encode,
                HealAbilityPacket::decode,
                HealAbilityPacket::handle
        );

        CHANNEL.registerMessage(id++,
                LightningClutch.class,
                LightningClutch::encode,
                LightningClutch::decode,
                LightningClutch::handle
        );

        CHANNEL.registerMessage(id++,
                StrengthClutch.class,
                StrengthClutch::encode,
                StrengthClutch::decode,
                StrengthClutch::handle
        );

        CHANNEL.registerMessage(id++,
                CloneClutch.class,
                CloneClutch::encode,
                CloneClutch::decode,
                CloneClutch::handle
        );

        CHANNEL.registerMessage(id++,
                SmokeClutch.class,
                SmokeClutch::encode,
                SmokeClutch::decode,
                SmokeClutch::handle
        );

        CHANNEL.registerMessage(id++,
                BinaryClutch.class,
                BinaryClutch::encode,
                BinaryClutch::decode,
                BinaryClutch::handle
        );

        CHANNEL.registerMessage(id++,
                BinaryWritePacket.class,
                BinaryWritePacket::encode,
                BinaryWritePacket::decode,
                BinaryWritePacket::handle
        );

        CHANNEL.registerMessage(id++,
                EraseClutch.class,
                EraseClutch::encode,
                EraseClutch::decode,
                EraseClutch::handle
        );

        CHANNEL.registerMessage(id++,
                CreationGetPacket.class,
                CreationGetPacket::encode,
                CreationGetPacket::decode,
                CreationGetPacket::handle
        );

        CHANNEL.registerMessage(id++,
                CreationPlacePacket.class,
                CreationPlacePacket::encode,
                CreationPlacePacket::decode,
                CreationPlacePacket::handle
        );

        CHANNEL.registerMessage(id++,
                DestructionClutch.class,
                DestructionClutch::encode,
                DestructionClutch::decode,
                DestructionClutch::handle
        );

        CHANNEL.messageBuilder(EditCommandPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(EditCommandPacket::encode)
                .decoder(EditCommandPacket::decode)
                .consumerMainThread(EditCommandPacket::handle)
                .add();

        CHANNEL.messageBuilder(EditAbility.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(EditAbility::encode)
                .decoder(EditAbility::decode)
                .consumerMainThread(EditAbility::handle)
                .add();

        CHANNEL.registerMessage(id++,
                AbilityChangePacket.class,
                AbilityChangePacket::encode,
                AbilityChangePacket::decode,
                AbilityChangePacket::handle
        );
        CHANNEL.registerMessage(id++,
                CopyPacket.class,
                CopyPacket::encode,
                CopyPacket::decode,
                CopyPacket::handle
        );
        CHANNEL.registerMessage(id++,
                GetItemPacket.class,
                GetItemPacket::encode,
                GetItemPacket::decode,
                GetItemPacket::handle
        );
        CHANNEL.registerMessage(id++,
                FlyClutch.class,
                FlyClutch::encode,
                FlyClutch::decode,
                FlyClutch::handle
        );
        CHANNEL.registerMessage(id++,
                AbilityWeaponsClutch.class,
                AbilityWeaponsClutch::encode,
                AbilityWeaponsClutch::decode,
                AbilityWeaponsClutch::handle
        );
        CHANNEL.messageBuilder(SyncStoryModePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncStoryModePacket::encode)
                .decoder(SyncStoryModePacket::decode)
                .consumerMainThread(SyncStoryModePacket::handle)
                .add();

        CHANNEL.registerMessage(id++,
                GetWeaponPacket.class,
                GetWeaponPacket::encode,
                GetWeaponPacket::decode,
                GetWeaponPacket::handle
        );
    }

    // クライアント専用パケット登録（GUI 開く系）
    public static void registerClientPackets() {
        if (CHANNEL == null) return; // まずCommonで作られている必要あり
        int id = 100; // Commonで登録したIDと被らないように100から始める

        CHANNEL.messageBuilder(OpenAbilityGuiPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenAbilityGuiPacket::encode)
                .decoder(OpenAbilityGuiPacket::decode)
                .consumerMainThread(OpenAbilityGuiPacket::handle)
                .add();

        CHANNEL.registerMessage(id++,
                CopyPacket.class,
                CopyPacket::encode,
                CopyPacket::decode,
                CopyPacket::handle
        );
    }

    // 型安全な送信メソッド
    public static void sendToServer(Object msg) {
        if (CHANNEL != null) CHANNEL.sendToServer(msg);
    }

    public static void sendToClient(Object msg, ServerPlayer player) {
        if (CHANNEL != null && player != null) {
            CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    /** Nexusに現在接続中の全プレイヤーへ送信 */
    public static void sendToAll(Object msg) {
        if (CHANNEL == null) return;
        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        server.getPlayerList().getPlayers()
                .forEach(player -> CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
    }
}
