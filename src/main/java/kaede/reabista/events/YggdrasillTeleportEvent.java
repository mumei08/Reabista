package kaede.reabista.events;


import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class YggdrasillTeleportEvent {

    public static void teleportToYggdrasill(Player entity) {
        if (!(entity instanceof ServerPlayer _player)) return;

        var nbt = entity.getPersistentData();
        boolean inYggdrasill = nbt.getBoolean("yggdrasill_active");

        if (!inYggdrasill) {
            // 現在のディメンションと座標をNBTに保存
            nbt.putString("prev_dimension", entity.level().dimension().location().toString());
            nbt.putDouble("prev_x", entity.getX());
            nbt.putDouble("prev_y", entity.getY());
            nbt.putDouble("prev_z", entity.getZ());

            // Yggdrasill次元に移動
            ServerLevel yggLevel = _player.getServer().getLevel(
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation("reabista:yggdrasill"))
            );
            if (yggLevel != null) {
                _player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
                _player.teleportTo(yggLevel, 0.5, 0, 0.5, -90, 0);
                _player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
                for (MobEffectInstance effect : _player.getActiveEffects())
                    _player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), effect));
                _player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            }

            nbt.putBoolean("yggdrasill_active", true);

        } else {
            // 保存されたディメンションと座標に戻す
            String prevDim = nbt.getString("prev_dimension");
            double x = nbt.getDouble("prev_x");
            double y = nbt.getDouble("prev_y");
            double z = nbt.getDouble("prev_z");

            ServerLevel prevLevel = _player.getServer().getLevel(
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation(prevDim))
            );
            if (prevLevel != null) {
                _player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
                _player.teleportTo(prevLevel, x, y, z, _player.getYRot(), _player.getXRot());
                _player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
                for (MobEffectInstance effect : _player.getActiveEffects())
                    _player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), effect));
                _player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            }

            nbt.putBoolean("yggdrasill_active", false);
        }
    }
}