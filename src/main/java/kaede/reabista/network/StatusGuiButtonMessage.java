package kaede.reabista.network;

import kaede.reabista.registry.ModAttributes;
import kaede.reabista.util.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;
import java.util.function.Supplier;

public class StatusGuiButtonMessage {

    private final String type;
    private final int value;

    public StatusGuiButtonMessage(String type, int value) {
        this.type = type;
        this.value = value;
    }

    // ===== エンコード/デコード =====
    public static void encode(StatusGuiButtonMessage msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.type);
        buf.writeInt(msg.value);
    }

    public static StatusGuiButtonMessage decode(FriendlyByteBuf buf) {
        return new StatusGuiButtonMessage(buf.readUtf(), buf.readInt());
    }

    // ===== サーバーでの処理 =====
    public static void handle(StatusGuiButtonMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            switch (msg.type) {
                case "HP" -> {
                    double now = player.getAttribute(ModAttributes.HP_POINT.get()).getBaseValue();
                    double next = now + msg.value;
                    player.getAttribute(ModAttributes.HP_POINT.get()).setBaseValue(next);
                    double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
                    player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - msg.value);

                    AttributeModifier modifier = new AttributeModifier(UUID.fromString("2467de73-f790-4cc1-941d-5b65afbffa10"), "HP", msg.value, AttributeModifier.Operation.ADDITION);
                    var attr = player.getAttribute(Attributes.MAX_HEALTH);
                    attr.removeModifier(modifier.getId());
                    attr.addPermanentModifier(modifier);
                }
                case "ATK" -> {
                    double now = player.getAttribute(ModAttributes.ATK_POINT.get()).getBaseValue();
                    double next = now + msg.value;
                    player.getAttribute(ModAttributes.ATK_POINT.get()).setBaseValue(next);
                    double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
                    player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - msg.value);

                    AttributeModifier modifier = new AttributeModifier(UUID.fromString("fc975381-d0ac-4684-a776-b53e80865d56"), "ATK", msg.value, AttributeModifier.Operation.ADDITION);
                    var attr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    attr.removeModifier(modifier.getId());
                    attr.addPermanentModifier(modifier);
                }
                case "DEF" -> {
                    double now = player.getAttribute(ModAttributes.DEF_POINT.get()).getBaseValue();
                    double next = now + msg.value;
                    player.getAttribute(ModAttributes.DEF_POINT.get()).setBaseValue(next);
                    double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
                    player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - msg.value);

                    AttributeModifier modifier = new AttributeModifier(UUID.fromString("5823e5ab-9c8f-437a-8ec5-656336fb73a9"), "DEF", msg.value, AttributeModifier.Operation.ADDITION);
                    var attr = player.getAttribute(Attributes.ARMOR);
                    attr.removeModifier(modifier.getId());
                    attr.addPermanentModifier(modifier);
                }
                case "AP" -> {
                    double now = player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();
                    double next = now + msg.value;
                    player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(next);
                    double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
                    player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - msg.value);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
