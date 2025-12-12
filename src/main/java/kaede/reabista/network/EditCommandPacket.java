package kaede.reabista.network;

import kaede.reabista.events.GluttonyAbilitiesEvent;
import kaede.reabista.events.YggdrasillTeleportEvent;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public record EditCommandPacket(String command) {

    public static void encode(EditCommandPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.command());
    }

    public static EditCommandPacket decode(FriendlyByteBuf buf) {
        return new EditCommandPacket(buf.readUtf(32767));
    }

    public static void handle(EditCommandPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            String cmd = msg.command().trim().toLowerCase();
            if (player == null) return;

            double ap = player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();

            switch (cmd) {
                case "gluttonyp" -> {
                    if (ap < 75) return;
                    GluttonyAbilitiesEvent.clutchGluttony(player);
                }
                case "fly" -> {
                    if (ap < 25) return;
                    player.getAbilities().mayfly = !player.getAbilities().mayfly;
                    player.onUpdateAbilities();
                }
                case "yggdrasill" -> {
                    // yggdrasill能力処理
                    if (ap < 100) return;
                    YggdrasillTeleportEvent.teleportToYggdrasill(player);
                }
                default -> {
                    if (cmd.startsWith("create:")) {
                        if (ap < 100) return;
                        String itemId = cmd.substring("create:".length());
                        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                        if (item != null) {
                            player.getInventory().add(item.getDefaultInstance());
                        }
                    } else {
                        // 未知コマンドはそのままブリガディア実行
                        CommandSourceStack source = player.createCommandSourceStack().withPermission(4);
                        var dispatcher = player.getServer().getCommands().getDispatcher();
                        try {
                            var results = dispatcher.parse(cmd, source);
                            dispatcher.execute(results);
                        } catch (Exception e) {
                            player.sendSystemMessage(Component.literal("コマンド実行中にエラー: " + e.getMessage()));
                            e.printStackTrace();
                        }
                    }
                }
            }
            final ResourceLocation ADV_ID2 =
                    new ResourceLocation("reabista:edit_advancement");
            var adv2 = player.server.getAdvancements().getAdvancement(ADV_ID2);
            if (adv2 != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv2);
                if (!progress.isDone()) {
                    for (String c : progress.getRemainingCriteria())
                        player.getAdvancements().award(adv2, c);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}


