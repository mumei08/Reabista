package kaede.reabista.network.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 創造能力(結晶化)のサーバー側処理。
 * クライアントで名前解決済みのブロックResourceLocation文字列と、
 * GUIを開いた時点で照準していたブロック座標を受け取り、その真上に生成する。
 * (座標はクライアント申告値のため、プレイヤーからの距離を必ずサーバー側で検証する)
 */
public record CreationPlacePacket(String blockId, int x, int y, int z) {

    private static final double MAX_DISTANCE_SQ = 12.0 * 12.0;

    public static void encode(CreationPlacePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.blockId, 256);
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
        buf.writeInt(msg.z);
    }

    public static CreationPlacePacket decode(FriendlyByteBuf buf) {
        return new CreationPlacePacket(buf.readUtf(256), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(CreationPlacePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            BlockPos targetBase = new BlockPos(msg.x, msg.y, msg.z);
            if (player.distanceToSqr(targetBase.getX() + 0.5, targetBase.getY() + 0.5, targetBase.getZ() + 0.5) > MAX_DISTANCE_SQ) {
                player.sendSystemMessage(Component.literal("§c対象が遠すぎます"));
                return;
            }

            ResourceLocation id = ResourceLocation.tryParse(msg.blockId);
            if (id == null) return;

            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) {
                player.sendSystemMessage(Component.literal("§c不明なブロックです: " + msg.blockId));
                return;
            }

            BlockPos above = targetBase.above();
            BlockState existing = level.getBlockState(above);
            if (!existing.canBeReplaced()) {
                player.sendSystemMessage(Component.literal("§7そこには既に何かがあります"));
                return;
            }

            level.setBlockAndUpdate(above, block.defaultBlockState());
            player.sendSystemMessage(Component.literal("§a創造した: " + id));
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5, 20, 0.4, 0.4, 0.4, 0.05);
        });
        ctx.get().setPacketHandled(true);
    }
}
