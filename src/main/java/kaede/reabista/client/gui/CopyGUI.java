package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.network.CopyPacket;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class CopyGUI extends Screen {

    private final Player entity;
    private int page = 0;
    private int lastPage = -1;

    private final HashMap<String, Button> buttons = new HashMap<>();
    private final HashMap<String, EditBox> editBoxes = new HashMap<>();

    private static final ResourceLocation TEXTURE = new ResourceLocation("reabista:textures/gui/ability_gui.png");

    public CopyGUI(Player entity) {
        super(Component.literal("能力管理"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        renderPage();
    }

    private void renderPage() {
        // 古いウィジェット削除
        buttons.values().forEach(this::removeWidget);
        buttons.clear();
        editBoxes.values().forEach(this::removeWidget);
        editBoxes.clear();

        int left = width / 2 - 88;
        int top = height / 2 - 83;

        double ap = minecraft.player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();

        switch (page) {
            case 0:
                addButton("a", "概念", left+24, top+16, 36, 20, () -> {
                    if (ap >= 75) {
                        page = 1;
                    }
                });
                addButton("b", "能力", left+24, top+52, 36, 20, () -> {
                    if (ap >= 25) {
                        page = 4;
                    }
                });
                addButton("c", "手に持っている物", left+24, top+88, 108, 20, () -> page=5);
                addButton("d", "想像", left+24, top+124, 36, 20, () -> {
                    if (ap >= 100) {
                        page = 6;
                    }
                });
                addButton("e", "複製", left+78, top+124, 36, 20, () -> {
                    if (ap >= 50) {
                        page = 8;
                    }
                });
                break;
            case 1:
                addButton("f", "能力値ポイント", left+51, top+34, 72, 20, () -> page=2);
                addButton("g", "ステータスポイント", left+51, top+97, 72, 20, () -> page=3);
                break;
            case 2:
                addEditBox("a", left+25, top+26, 124, 18, "体力ポイント");
                addEditBox("b", left+25, top+53, 124, 18, "攻撃力ポイント");
                addEditBox("c", left+25, top+80, 124, 18, "防御力ポイント");
                addEditBox("d", left+25, top+107, 124, 18, "能力ポイント");
                addButton("h", "決定", left+69, top+133, 36, 20, () -> {
                    Integer[] vals = new Integer[4];
                    vals[0] = getEditBoxValue("a");
                    vals[1] = getEditBoxValue("b");
                    vals[2] = getEditBoxValue("c");
                    vals[3] = getEditBoxValue("d");
                    sendActionToServer(0, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                break;
            case 3:
                addEditBox("e", left+25, top+44, 124, 18, "ステータスポイント");
                addButton("i", "決定", left+69, top+97, 36, 20, () -> {
                    Integer[] vals = new Integer[1];
                    vals[0] = getEditBoxValue("e");
                    sendActionToServer(1, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                break;
            case 4:
                addButton("j", "空を飛ぶ程度の能力", left+24, top+16, 126, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(2, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                addButton("k", "大喰い", left+24, top+43, 45, 20, () -> minecraft.setScreen(new GluttonyGUI(entity)));
                addButton("l", "大喰いパッシブ", left+78, top+43, 72, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(3, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                addButton("m", "硬化", left+24, top+70, 36, 20, () -> minecraft.setScreen(new GuardGUI(entity)));
                break;
            case 5:
                addEditBox("f", left+25, top+44, 124, 18, "増やす数");
                addButton("n", "決定", left+69, top+97, 36, 20, () -> {
                    Integer[] vals = new Integer[1];
                    vals[0] = getEditBoxValue("f");
                    sendActionToServer(4, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                break;
            case 6:
                addButton("o", "ユグドラシル", left+15, top+34, 72, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(5, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                addButton("p", "アイテム", left+15, top+79, 54, 20, () -> page=7);
                break;
            case 7:
                addButton("q", "モンスター", left+60, top+30, 54, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(6, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                addButton("r", "ZONE", left+70, top+60, 36, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(7, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                addButton("s", "レッドブル", left+60, top+80, 54, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(8, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                break;
            case 8:
                addButton("t", "概念", left+25, top+25, 36, 20, () -> page=9);
                break;
            case 9:
                addButton("u", "重力", left+69, top+25, 36, 20, () -> page=10);
                addButton("v", "アイテムとの距離", left+51, top+61, 72, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(9, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                addButton("w", "ステータスリセット", left+33, top+97, 108, 20, () -> {
                    Integer[] vals = new Integer[0];
                    sendActionToServer(10, vals);
                    Minecraft.getInstance().setScreen(null);
                });
                break;
            case 10:
                addEditBox("g", left+25, top+35, 125, 18, "割る数");
                addButton("x", "決定", left+69, top+97, 36, 20, () -> {
                    Integer[] vals = new Integer[1];
                    vals[0] = getEditBoxValue("g");
                    sendActionToServer(11, vals);
                    Minecraft.getInstance().setScreen(null);
                });
        }
    }

    private void addButton(String key, String text, int x, int y, int w, int h, Runnable onClick) {
        Button b = Button.builder(Component.literal(text), btn -> onClick.run()).bounds(x, y, w, h).build();
        addRenderableWidget(b);
        buttons.put(key, b);
    }

    private void addEditBox(String key, int x, int y, int w, int h, String placeholder) {
        EditBox box = new EditBox(font, x, y, w, h, Component.literal(placeholder));
        box.setHint(Component.literal(placeholder));
        addRenderableWidget(box);
        editBoxes.put(key, box);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui);

        int left = width / 2 - 88;
        int top = height / 2 - 83;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        gui.blit(TEXTURE, left, top, 0, 0, 176, 166);
        RenderSystem.disableBlend();

        // ページが変わった場合だけ更新
        if (page != lastPage) {
            lastPage = page;
            renderPage();
        }

        super.render(gui, mouseX, mouseY, partialTicks);

        // EditBox の描画は super.render の後で OK
        editBoxes.values().forEach(box -> box.render(gui, mouseX, mouseY, partialTicks));
    }
    private void sendActionToServer(int actionId, Integer[] value) {
        // CopyPacket は actionId と value を持つパケット
        NetworkHandler.CHANNEL.sendToServer(new CopyPacket(actionId, value));
    }

    private Integer getEditBoxValue(String key) {
        EditBox box = editBoxes.get(key);
        if (box == null) return null;
        String txt = box.getValue().trim();

        if (txt.isEmpty()) return null; // ← 空欄なら null
        try {
            return Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return null; // 数字じゃなかったら無視
        }
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { // ESC
            this.minecraft.setScreen(null);
            return true;
        }
        return editBoxes.values().stream().anyMatch(box -> box.keyPressed(key, b, c)) || super.keyPressed(key, b, c);
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
