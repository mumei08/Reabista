package kaede.reabista.network.ability;

import kaede.reabista.events.GluttonyAbilitiesEvent;
import kaede.reabista.registry.ModAttributes;
import kaede.reabista.registry.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class EditCommandPacket {

    private final String command;

    public EditCommandPacket(String command){
        this.command = command;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(command);
    }

    public static EditCommandPacket decode(FriendlyByteBuf buf) {
        String command = buf.readUtf(32767);
        return new EditCommandPacket(command);
    }

    public static void handle(EditCommandPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            double ap = player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();

            switch (msg.command) {
                case "copy" -> {
                    if (ap < 2000) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.COPY_CRYSTAL.get().getDefaultInstance());
                }
                case "gluttony" -> {
                    if (ap < 1400) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.GLUTTONY_CRYSTAL.get().getDefaultInstance());
                }
                case "guard" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.GUARD_CRYSTAL.get().getDefaultInstance());
                }
                case "healer" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.HEALER_CRYSTAL.get().getDefaultInstance());
                }
                case "lightning" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.LIGHTNING_CRYSTAL.get().getDefaultInstance());
                }
                case "strength" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.STRENGTH_CRYSTAL.get().getDefaultInstance());
                }
                case "clone" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.CLONE_CRYSTAL.get().getDefaultInstance());
                }
                case "smoke" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.SMOKE_CRYSTAL.get().getDefaultInstance());
                }
                case "binary" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.BINARY_CRYSTAL.get().getDefaultInstance());
                }
                case "erase" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.ERASE_CRYSTAL.get().getDefaultInstance());
                }
                case "creation" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.CREATION_CRYSTAL.get().getDefaultInstance());
                }
                case "destruction" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.DESTRUCTION_CRYSTAL.get().getDefaultInstance());
                }

                case "gluttonyp" -> {
                    if (ap < 1400) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    GluttonyAbilitiesEvent.clutchGluttony(player);
                }
                case "fly" -> {
                    if (ap < 1100) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.FLY_CRYSTAL.get().getDefaultInstance());
                }
                case "teleport" -> {
                    if (ap < 1200) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.TELEPORT_CRYSTAL.get().getDefaultInstance());
                }
                case "yggdrasill" -> {
                    if (ap < 1500) {
                        player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                        return;
                    }
                    player.getInventory().add(ModItems.YGGDRASIL_KEY.get().getDefaultInstance());
                }
                default -> {
                    if (msg.command.startsWith("create:")) {
                        if (ap < 1500) {
                            player.sendSystemMessage(Component.literal("操作が失敗しました。"));
                            return;
                        }
                        String itemId = msg.command.substring("create:".length());
                        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                        if (item != null) {
                            player.getInventory().add(item.getDefaultInstance());
                        }
                    } else {
                        // 未知コマンドはそのままブリガディア実行
                        int i = 1;
                        if (ap >= 1300)i = 2;
                        if (ap >= 1500)i = 3;
                        if (ap >= 2000)i = 4;

                        CommandSourceStack source = player.createCommandSourceStack().withPermission(i);
                        var dispatcher = player.getServer().getCommands().getDispatcher();
                        try {
                            var results = dispatcher.parse(msg.command, source);
                            dispatcher.execute(results);
                            final ResourceLocation ADV_ID =
                                    new ResourceLocation("reabista:edit_advancement_1");
                            var adv = player.server.getAdvancements().getAdvancement(ADV_ID);
                            if (adv != null) {
                                var progress1 = player.getAdvancements().getOrStartProgress(adv);
                                if (!progress1.isDone()) {
                                    for (String c : progress1.getRemainingCriteria())
                                        player.getAdvancements().award(adv, c);
                                }
                            }
                        } catch (Exception e) {
                            player.sendSystemMessage(Component.literal("コマンド実行中にエラー: " + e.getMessage()));
                            e.printStackTrace();
                        }
                    }
                }
            }
            final ResourceLocation ADV_ID =
                    new ResourceLocation("reabista:edit_advancement_1");
            final ResourceLocation ADV_ID2 =
                    new ResourceLocation("reabista:edit_advancement_2");
            var adv = player.server.getAdvancements().getAdvancement(ADV_ID);
            var adv2 = player.server.getAdvancements().getAdvancement(ADV_ID2);
            if (adv2 != null && adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                var progress2 = player.getAdvancements().getOrStartProgress(adv2);
                if (!progress2.isDone() && progress.isDone()) {
                    for (String c : progress2.getRemainingCriteria())
                        player.getAdvancements().award(adv2, c);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}


