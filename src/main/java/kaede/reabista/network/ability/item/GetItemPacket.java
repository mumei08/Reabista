package kaede.reabista.network.ability.item;

import kaede.reabista.registry.ModAttributes;
import kaede.reabista.registry.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record GetItemPacket(int ability) {

    public static void encode(GetItemPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.ability());
    }

    public static GetItemPacket decode(FriendlyByteBuf buf) {
        return new GetItemPacket(buf.readInt());
    }

    public static void handle(GetItemPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            double attr = player.getAttributeValue(ModAttributes.ABILITY_POINT.get());
            if (msg.ability() == 1) {
                if (attr >= 200) {
                    player.getInventory().add(ModItems.EDIT_CRYSTAL.get().getDefaultInstance());
                    player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 100);
                }
            } else if (msg.ability() == 2) {
                if (attr >= 200) {
                    player.getInventory().add(ModItems.COPY_CRYSTAL.get().getDefaultInstance());
                    player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 100);
                }
            }else {
                if (attr >= 100) {
                    switch (msg.ability()) {
                        case 3 -> {
                            player.getInventory().add(ModItems.FLY_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 4 -> {
                            player.getInventory().add(ModItems.TELEPORT_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 5 -> {
                            player.getInventory().add(ModItems.GLUTTONY_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 6 -> {
                            player.getInventory().add(ModItems.GUARD_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 7 -> {
                            player.getInventory().add(ModItems.HEALER_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 8 -> {
                            player.getInventory().add(ModItems.LIGHTNING_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 9 -> {
                            player.getInventory().add(ModItems.STRENGTH_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 10 -> {
                            player.getInventory().add(ModItems.CLONE_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 11 -> {
                            player.getInventory().add(ModItems.SMOKE_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 12 -> {
                            player.getInventory().add(ModItems.BINARY_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 13 -> {
                            player.getInventory().add(ModItems.ERASE_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 14 -> {
                            player.getInventory().add(ModItems.CREATION_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                        case 15 -> {
                            player.getInventory().add(ModItems.DESTRUCTION_CRYSTAL.get().getDefaultInstance());
                            player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(attr - 50);
                        }
                    }
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
