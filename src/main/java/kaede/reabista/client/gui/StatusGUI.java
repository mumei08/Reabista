package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.StatusGuiButtonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import kaede.reabista.registry.ModAttributes;

import java.util.HashMap;
import java.util.UUID;

public class StatusGUI extends Screen {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("reabista:textures/gui/status_gui.png");

    private final Player entity;

    private static final int TEX_WIDTH = 260;
    private static final int TEX_HEIGHT = 166;

    // MCreator互換の guistate（必要なら後で消してOK）
    private final HashMap<String, Object> guistate = new HashMap<>();

    private EditBox pointBox;
    private Button hpBtn, atkBtn, defBtn, apBtn;

    public StatusGUI(Player entity) {
        super(Component.literal("Status"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.width / 2 - TEX_WIDTH / 2;
        int top = this.height / 2 - TEX_HEIGHT / 2;

        // ===== EditBox =====
        this.pointBox = new EditBox(
                this.font,
                left + 184, top + 8,
                70, 18,
                Component.literal("Point"));
        this.pointBox.setMaxLength(10000);
        this.addRenderableWidget(pointBox);
        guistate.put("text:point", pointBox);

        // ===== Buttons（+） =====
        hpBtn = Button.builder(Component.literal("+"), b -> handlePointButton("HP")
        ).bounds(left + 207, top + 39, 22, 20).build();

        atkBtn = Button.builder(Component.literal("+"), b -> handlePointButton("ATK")
        ).bounds(left + 207, top + 64, 22, 20).build();

        defBtn = Button.builder(Component.literal("+"), b -> handlePointButton("DEF")
        ).bounds(left + 207, top + 91, 22, 20).build();

        apBtn = Button.builder(Component.literal("+"), b -> handlePointButton("AP")
        ).bounds(left + 207, top + 119, 22, 20).build();

        this.addRenderableWidget(hpBtn);
        this.addRenderableWidget(atkBtn);
        this.addRenderableWidget(defBtn);
        this.addRenderableWidget(apBtn);

        guistate.put("button:hp", hpBtn);
        guistate.put("button:atk", atkBtn);
        guistate.put("button:def", defBtn);
        guistate.put("button:ap", apBtn);
    }

    private void handlePointButton(String type) {
        if (minecraft == null || minecraft.player == null) return;
        LivingEntity player = minecraft.player;

        // 入力チェック
        String s = pointBox.getValue().trim();
        if (s.isEmpty()) return;

        int value;
        try {
            value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return;
        }
        if (value <= 0) return;

        // サーバーに送信
        NetworkHandler.sendToServer(new StatusGuiButtonMessage(type, value));

        // クライアント側の即時反映
        double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
        if (value > sp) return;

        switch (type) {
            case "HP" -> {
                updateAttribute(player, ModAttributes.HP_POINT.get(), Attributes.MAX_HEALTH, value,
                        UUID.fromString("2467de73-f790-4cc1-941d-5b65afbffa10"), "HP");
            }
            case "ATK" -> {
                updateAttribute(player, ModAttributes.ATK_POINT.get(), Attributes.ATTACK_DAMAGE, value,
                        UUID.fromString("fc975381-d0ac-4684-a776-b53e80865d56"), "ATK");
            }
            case "DEF" -> {
                updateAttribute(player, ModAttributes.DEF_POINT.get(), Attributes.ARMOR, value,
                        UUID.fromString("5823e5ab-9c8f-437a-8ec5-656336fb73a9"), "DEF");
            }
            case "AP" -> {
                double now = player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();
                player.getAttribute(ModAttributes.ABILITY_POINT.get()).setBaseValue(now + value);
                player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - value);
            }
        }
    }

    // 属性更新の共通化
    private void updateAttribute(LivingEntity player, Attribute attrPoint, Attribute targetAttr, int value, UUID uuid, String name) {
        double now = player.getAttribute(attrPoint).getBaseValue();
        double next = now + value;

        player.getAttribute(attrPoint).setBaseValue(next);
        double sp = player.getAttribute(ModAttributes.STATUS_POINT.get()).getBaseValue();
        player.getAttribute(ModAttributes.STATUS_POINT.get()).setBaseValue(sp - value);

        AttributeModifier modifier = new AttributeModifier(uuid, name, next, AttributeModifier.Operation.ADDITION);
        var attr = player.getAttribute(targetAttr);
        attr.removeModifier(modifier.getId());
        attr.addPermanentModifier(modifier);
    }

    // ================================
    //      Render（背景 + テキスト）
    // ================================
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui);

        int cx = this.width / 2;
        int cy = this.height / 2;

        int left = cx - TEX_WIDTH / 2;
        int top = cy - TEX_HEIGHT / 2;

        // 背景
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.enableBlend();
        gui.blit(TEXTURE, left, top, 0, 0, TEX_WIDTH, TEX_HEIGHT);
        RenderSystem.disableBlend();

        super.render(gui, mouseX, mouseY, partialTicks);

        pointBox.render(gui, mouseX, mouseY, partialTicks);

        // ===== 属性値 =====
        int sp = (int) entity.getAttributeValue(ModAttributes.STATUS_POINT.get());
        int hp = (int) entity.getAttributeValue(ModAttributes.HP_POINT.get());
        int atk = (int) entity.getAttributeValue(ModAttributes.ATK_POINT.get());
        int def = (int) entity.getAttributeValue(ModAttributes.DEF_POINT.get());
        int ap = (int) entity.getAttributeValue(ModAttributes.ABILITY_POINT.get());

        // ===== 表示位置 =====
        int lx = left + 21;
        int ly = top + 16;

        gui.drawString(this.font, Component.literal("ステータスポイント"), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("体力ポイント"), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("攻撃力ポイント"), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("防御力ポイント"), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("能力ポイント"), lx, ly, 0x404040, false);

        // 値（右側）
        lx = left + 122;
        ly = top + 16;

        gui.drawString(this.font, Component.literal("" + sp), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("" + hp), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("" + atk), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("" + def), lx, ly, 0x404040, false); ly += 27;
        gui.drawString(this.font, Component.literal("" + ap), lx, ly, 0x404040, false);
    }

    // ESC閉じ
    @Override
    public boolean keyPressed(int key, int scan, int mod) {
        if (key == 256) {
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.keyPressed(key, scan, mod);
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}