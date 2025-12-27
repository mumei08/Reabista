package kaede.reabista.network;

import kaede.reabista.events.GluttonyAbilitiesEvent;
import kaede.reabista.events.YggdrasillTeleportEvent;
import kaede.reabista.registry.ModAttributes;
import kaede.reabista.registry.ModItems;
import kaede.reabista.util.AttributeUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CopyPacket {
    private final int actionId;     // どのボタンの処理か
    private final Integer[] values;     // EditBox の値（最大4つ）

    public CopyPacket(int actionId, Integer[] values) {
        this.actionId = actionId;
        this.values = values;
    }

    // デコード
    public static CopyPacket decode(FriendlyByteBuf buf) {
        int actionId = buf.readInt();
        int len = buf.readInt();
        Integer[] values = new Integer[len];

        for (int i = 0; i < len; i++) {
            int read = buf.readInt();
            values[i] = (read == -1 ? null : read);  // ← 復元
        }
        return new CopyPacket(actionId, values);
    }

    // エンコード
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(actionId);
        buf.writeInt(values.length);
        for (Integer val : values) {
            if (val == null) {
                buf.writeInt(-1);  // null の代わり
            } else {
                buf.writeInt(val);
            }
        }
    }

    public int getActionId() {
        return actionId;
    }

    public Integer[] getValues() {
        return values;
    }
    public static void handle(CopyPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Integer[] vals = pkt.getValues();

            switch (pkt.getActionId()) {
                case 0: // applyAbilityPoints
                    if (vals.length >= 4) {
                        if (vals[0] != null) AttributeUtils.setAttribute(player, "HP", vals[0]);
                        if (vals[1] != null) AttributeUtils.setAttribute(player, "ATK", vals[1]);
                        if (vals[2] != null) AttributeUtils.setAttribute(player, "DEF", vals[2]);
                        if (vals[3] != null) AttributeUtils.setAttribute(player, "AP", vals[3]);
                    }
                    break;

                case 1: // applyStatusPoint
                    if (vals.length >= 1) {
                        player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(vals[0]);
                    }
                    break;

                case 2: // toggleFly
                    player.getAbilities().mayfly = !player.getAbilities().mayfly;
                    player.onUpdateAbilities();
                    break;

                case 3: // applyGluttonyPassive
                    GluttonyAbilitiesEvent.clutchGluttony(player);
                    break;

                case 4: // giveHeldItems
                    if (vals.length >= 1) {
                        int amount = vals[0]; // GUI から送られた数
                        var mainHand = player.getMainHandItem();
                        if (!mainHand.isEmpty() && amount > 0) {
                            ItemStack copy = mainHand.copy();
                            copy.setCount(amount);
                            player.getInventory().add(copy);
                        }
                    }
                    break;

                case 5: // yggdrasilEvent
                    YggdrasillTeleportEvent.teleportToYggdrasill(player);
                    break;

                case 6: // givemonster
                    player.getInventory().add(ModItems.MONSTER.get().getDefaultInstance());
                    break;

                case 7: // givezone
                    player.getInventory().add(ModItems.ZONE.get().getDefaultInstance());
                    break;

                case 8: // giveredbull
                    player.getInventory().add(ModItems.REDBULL.get().getDefaultInstance());
                    break;

                case 9: // tpItemsToPlayer
                    ServerLevel level = player.serverLevel();

                    // コマンド実行位置をプレイヤー座標に合わせる
                    CommandSourceStack src = new CommandSourceStack(
                            CommandSource.NULL,
                            player.position(),
                            player.getRotationVector(),
                            level,
                            4,              // 権限
                            "",             // name
                            Component.literal(""),
                            level.getServer(),
                            null
                    ).withSuppressedOutput(); // チャット出力なし

                    level.getServer().getCommands().performPrefixedCommand(
                            src,
                            "tp @e[type=item] " + player.getX() + " " + player.getY() + " " + player.getZ()
                    );
                    break;

                case 10: // resetStatus
                    double sp = player.getAttributeBaseValue(ModAttributes.STATUS_POINT.get());
                    double hp = player.getAttributeBaseValue(ModAttributes.HP_POINT.get());
                    double atk = player.getAttributeBaseValue(ModAttributes.ATK_POINT.get());
                    double def = player.getAttributeBaseValue(ModAttributes.DEF_POINT.get());
                    double ap = player.getAttributeBaseValue(ModAttributes.ABILITY_POINT.get());
                    double next = sp + hp + atk + def + ap ;

                    player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(next);
                    player.getAttribute(ModAttributes.HP_POINT.get()).setBaseValue(0);
                    player.getAttribute(ModAttributes.ATK_POINT.get()).setBaseValue(0);
                    player.getAttribute(ModAttributes.DEF_POINT.get()).setBaseValue(0);
                    player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(0);

                    AttributeUtils.updateAttributeModifier(player, "HP");
                    AttributeUtils.updateAttributeModifier(player, "ATK");
                    AttributeUtils.updateAttributeModifier(player, "DEF");

                    break;

                case 11: // applyGravity
                    if (vals.length >= 1) {
                        Double EG = player.getAttributeBaseValue(ForgeMod.ENTITY_GRAVITY.get());
                        player.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(EG / vals[0]);
                    }
                    break;
            }
            final ResourceLocation ADV_ID =
                    new ResourceLocation("reabista:copy_advancement");
            var adv = player.server.getAdvancements().getAdvancement(ADV_ID);
            if (adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String c : progress.getRemainingCriteria())
                        player.getAdvancements().award(adv, c);
                }
            }
        });
    }
}