package kaede.reabista.network.ability;

import kaede.reabista.registry.ModAttributes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EditAbility {
    private final String player;
    private final Integer i;
    private final String Ability;

    public EditAbility(String  player, Integer i, String Ability) {
            this.player = player;
            this.i = i;
            this.Ability = Ability;
    }

    // エンコード
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(player);
        buf.writeInt(i);
        buf.writeUtf(Ability);
    }

    public static EditAbility decode(FriendlyByteBuf buf) {
        String player = buf.readUtf(32767);
        Integer i = buf.readInt();
        String ability = buf.readUtf(32767);
        return new EditAbility(player, i, ability);
    }

    public static void handle(EditAbility msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            // サーバー上の全プレイヤーから名前で取得
            ServerPlayer targetPlayer = null;
            for (ServerPlayer sp : sender.getServer().getPlayerList().getPlayers()) {
                if (sp.getName().getString().equals(msg.player)) {
                    targetPlayer = sp;
                    break;
                }
            }

            if (targetPlayer == null) {
                sender.sendSystemMessage(Component.literal("プレイヤー " + msg.player + " は存在しません。"));
                ctx.get().setPacketHandled(true);
                return;
            }

            boolean success = false; // 成功フラグ

            // i によって処理を分ける
            if (msg.i == 0) {
                targetPlayer.getAttribute(ModAttributes.ABILITY.get()).setBaseValue(0);
                success = true;
            } else if (msg.i == 1) {
                int ability = switch (msg.Ability.toLowerCase()) {
                    case "edit" -> 1;
                    case "copy" -> 2;
                    case "fly" -> 3;
                    case "tp" -> 4;
                    case "gluttony" -> 5;
                    case "guard" -> 6;
                    case "healer" -> 7;
                    case "lightning" -> 8;
                    case "strength" -> 9;
                    case "clone" -> 10;
                    case "smoke" -> 11;
                    case "binary" -> 12;
                    case "erase" -> 13;
                    case "creation" -> 14;
                    case "destruction" -> 15;
                    default -> 0; // 対応しない文字列は 0
                };

                if (ability != 0) {
                    targetPlayer.getAttribute(ModAttributes.ABILITY.get()).setBaseValue(ability);
                    success = true;
                }
            }

            if (success) {
                // Advancement を取得
                Advancement adv = sender.getServer().getAdvancements().getAdvancement(
                        new ResourceLocation("reabista", "ability_grant_advancement")
                );

                if (adv != null) {
                    AdvancementProgress progress = targetPlayer.getAdvancements().getOrStartProgress(adv);

                    // 残りの Criteria をすべて達成扱いにする
                    for (String criterion : progress.getRemainingCriteria()) {
                        targetPlayer.getAdvancements().award(adv, criterion);
                    }
                }
                Advancement adv2 = sender.getServer().getAdvancements().getAdvancement(
                        new ResourceLocation("reabista", "root2")
                );

                if (adv2 != null) {
                    AdvancementProgress progress2 = targetPlayer.getAdvancements().getOrStartProgress(adv2);

                    // 残りの Criteria をすべて達成扱いにする
                    for (String criterion2 : progress2.getRemainingCriteria()) {
                        targetPlayer.getAdvancements().award(adv2, criterion2);
                    }
                }

                sender.sendSystemMessage(Component.literal(
                        "プレイヤー " + targetPlayer.getName().getString() + " に対する操作を実行しました。"
                ));
            } else {
                sender.sendSystemMessage(Component.literal(
                        "プレイヤー " + targetPlayer.getName().getString() + " に対する操作を実行できませんでした。"
                ));
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
