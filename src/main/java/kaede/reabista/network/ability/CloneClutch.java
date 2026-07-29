package kaede.reabista.network.ability;

import kaede.reabista.entity.CloneEntity;
import kaede.reabista.registry.ModEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 分身能力の発動パケット(GUI無し即時発動)。
 * プレイヤーの隣にCloneEntityを1体召喚する。装備は現在の装備をそのままコピー(ドロップはしない)。
 */
public record CloneClutch() {

    private static final int MAX_CLONES = 2; // 同時召喚数の上限

    public static void encode(CloneClutch msg, FriendlyByteBuf buf) {}

    public static CloneClutch decode(FriendlyByteBuf buf) {
        return new CloneClutch();
    }

    public static void handle(CloneClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            long existing = level.getEntitiesOfClass(CloneEntity.class,
                    player.getBoundingBox().inflate(32.0D)).stream().count();
            if (existing >= MAX_CLONES) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c分身は同時に" + MAX_CLONES + "体までです"));
                return;
            }

            CloneEntity clone = ModEntities.CLONE.get().create(level);
            if (clone == null) return;

            clone.moveTo(player.getX() + (level.random.nextDouble() - 0.5) * 2,
                    player.getY(), player.getZ() + (level.random.nextDouble() - 0.5) * 2,
                    player.getYRot(), 0);
            clone.setOwner(player);
            clone.setCustomName(net.minecraft.network.chat.Component.literal(player.getName().getString() + "の分身"));
            clone.setCustomNameVisible(true);

            // 見た目だけ簡易的に装備をコピー(耐久・エンチャは実体参照ではなく複製)
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                clone.setItemSlot(slot, player.getItemBySlot(slot).copy());
                clone.setDropChance(slot, 0.0F); // 死亡時にドロップさせない
            }

            level.addFreshEntity(clone);
        });
        ctx.get().setPacketHandled(true);
    }
}
