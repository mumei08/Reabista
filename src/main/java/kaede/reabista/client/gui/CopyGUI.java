package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.client.ClientData;
import kaede.reabista.network.ability.CopyPacket;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.ability.EditAbility;
import kaede.reabista.registry.ModAttributes;
import kaede.reabista.registry.ModGamerules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class CopyGUI extends Screen {

    private final Player entity;
    private int page = 0;
    private int lastPage = -1;
    private int prevPage = 0;
    private final Stack<Integer> pageHistory = new Stack<>();

    private final HashMap<String, Button> buttons = new HashMap<>();
    private final HashMap<String, EditBox> editBoxes = new HashMap<>();
    private final HashMap<String, CopyGUI.AutocompleteEditBox> editBox = new HashMap<>();

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
        if (page != lastPage) {
            prevPage = lastPage >= 0 ? lastPage : page; // 初回は page を保存
        }
        // 古いウィジェット削除
        buttons.values().forEach(this::removeWidget);
        buttons.clear();
        editBoxes.values().forEach(this::removeWidget);
        editBoxes.clear();
        editBox.values().forEach(this::removeWidget);
        editBox.clear();


        int cx = this.width / 2;
        int cy = this.height / 2;
        int by = cy - 25 + 45;
        int ey = cy - 80 + 45;

        int left = width / 2 - 88;
        int top = height / 2 - 83;

        double ap = minecraft.player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();

        switch (page) {
            case 0:
                if (ap >= 1000){
                    addButton("a", "概念", left+24, top+16, 36, 20, () -> {
                        changePage(1);
                    });
                }
                if (ap >= 200){
                    addButton("b", "能力", left+24, top+52, 36, 20, () -> {
                        changePage(4);
                    });
                }
                addButton("c", "手に持っている物", left+24, top+88, 108, 20, () -> changePage(5));
                if (ap >= 600){
                    addButton("d", "想像", left+24, top+125, 36, 20, () -> {
                        changePage(6);
                    });
                }
                if (ap >= 400){
                    addButton("e", "複製", left+78, top+125, 36, 20, () -> {
                        changePage(8);
                    });
                }
                if (ap >= 1000 && ClientData.storyMode){
                    addButton("z1", "能力付与", left+115, top+16, 50, 20, () -> {
                        changePage(11);
                    });
                }
                if (ap >= 1000 && ClientData.storyMode){
                    addButton("z2", "能力削除", left+115, top+52, 50, 20, () -> {
                        changePage(12);
                    });
                }
                break;
            case 1:
                addButton("f", "能力値ポイント", left+51, top+34, 72, 20, () -> changePage(2));
                addButton("g", "ステータスポイント", left+51, top+97, 72, 20, () -> changePage(3));
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
                addButton("p", "アイテム", left+15, top+79, 54, 20, () -> changePage(7));
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
                addButton("t", "概念", left+25, top+25, 36, 20, () -> changePage(9));
                break;
            case 9:
                addButton("u", "重力", left+69, top+25, 36, 20, () -> changePage(10));
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
                break;
            case 11:
                List<String> playerNames = Minecraft.getInstance().level.players().stream()
                        .map(p -> p.getName().getString())
                        .toList();
                addEditBox1("player1", cx - 59, ey, 118, 18, "プレイヤー", playerNames);
                List<String> abilities = List.of("edit","copy","fly","tp","gluttony","guard");
                addEditBox1("ability", cx - 59, ey + 30, 118, 18, "能力名(英語)", abilities);
                addButton("h","実行",cx - 20, by, 40, 20, () -> AddAbility(getEditBoxValue1("player1"), getEditBoxValue1("ability")));
                break;
            case 12:
                List<String> playerNames2 = Minecraft.getInstance().level.players().stream()
                        .map(p -> p.getName().getString()).toList();
                addEditBox1("player2", cx - 59, ey, 118, 18, "プレイヤー", playerNames2);
                addButton("i","実行",cx - 20, by, 40, 20, () -> DelAbility(getEditBoxValue1("player2")));
                break;
        }
    }

    private void addButton(String key, String text, int x, int y, int w, int h, Runnable onClick) {
        Button b = Button.builder(Component.literal(text), btn -> onClick.run()).bounds(x, y, w, h).build();
        addRenderableWidget(b);
        buttons.put(key, b);
    }

    private void addEditBox1(String key, int x, int y, int w, int h, String placeholder, List<String> suggestions) {
        CopyGUI.AutocompleteEditBox box = new CopyGUI.AutocompleteEditBox(font, x, y, w, h, Component.literal(placeholder), suggestions);
        box.setHint(Component.literal(placeholder));
        addRenderableWidget(box);
        editBox.put(key, box);
    }

    private void addEditBox(String key, int x, int y, int w, int h, String placeholder) {
        EditBox box = new EditBox(font, x, y, w, h, Component.literal(placeholder));
        box.setHint(Component.literal(placeholder));
        addRenderableWidget(box);
        editBoxes.put(key, box);
    }

    private void changePage(int newPage) {
        pageHistory.push(page);
        page = newPage;
    }

    private void AddAbility(String player, String ability) {
        if (player == null || ability == null) return;
        NetworkHandler.sendToServer(new EditAbility(player, 1, ability));
        Minecraft.getInstance().setScreen(null);
    }

    private void DelAbility(String player) {
        if (player == null) return;
        NetworkHandler.sendToServer(new EditAbility(player, 0, "0"));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        boolean handled = false;
        for (CopyGUI.AutocompleteEditBox box : editBox.values()) {
            handled |= box.scrollSuggestions(delta);
        }
        return handled || super.mouseScrolled(mx, my, delta);
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

        for (CopyGUI.AutocompleteEditBox box : editBox.values()) {
            box.render(gui, mouseX, mouseY, partialTicks);
            box.renderSuggestions(gui, box.getX(), box.getY() + box.getHeight(), 5);
        }

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
    private String getEditBoxValue1(String key) {
        CopyGUI.AutocompleteEditBox box = editBox.get(key);
        if (box == null) return null;
        String txt = box.getValue().trim();
        return txt.isEmpty() ? null : txt;
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { // ESC

            // 🔹 まずオートコンプリートを閉じる
            for (AutocompleteEditBox box : editBox.values()) {
                if (box.isFocused() && box.hasSuggestions()) {
                    box.clearSuggestions();
                    return true; // ページ戻らない
                }
            }

            // 🔹 通常の戻る処理
            if (!pageHistory.isEmpty()) {
                page = pageHistory.pop();
                renderPage();
            } else {
                this.minecraft.setScreen(null);
            }
            return true;
        }

        return editBoxes.values().stream()
                .anyMatch(box -> box.keyPressed(key, b, c))
                || super.keyPressed(key, b, c);
    }
    @Override
    public boolean isPauseScreen() { return false; }

    // ============================
    //      Autocomplete EditBox
    // ============================

    private static class AutocompleteEditBox extends EditBox {
        private List<String> baseSuggestions = new ArrayList<>();
        private List<String> filtered = new ArrayList<>();
        private int selectedIndex = -1;
        private int scrollOffset = 0;
        private final net.minecraft.client.gui.Font myFont;

        public AutocompleteEditBox(net.minecraft.client.gui.Font font, int x, int y, int width, int height,
                                   Component msg, List<String> suggestions) {
            super(font, x, y, width, height, msg);
            this.myFont = font;
            setBaseSuggestions(suggestions);
            this.setResponder(this::updateSuggestions);
            updateSuggestions(getValue());
        }

        public boolean hasSuggestions() {
            return !filtered.isEmpty();
        }

        public void clearSuggestions() {
            filtered.clear();
            selectedIndex = -1;
            scrollOffset = 0;
        }


        public void setBaseSuggestions(List<String> suggestions) {
            this.baseSuggestions = (suggestions != null) ? new ArrayList<>(suggestions) : new ArrayList<>();
        }

        public void updateSuggestions(String text) {
            filtered.clear();
            selectedIndex = -1;
            scrollOffset = 0;

            if (text == null) text = "";
            String lower = text.toLowerCase();

            if (text.startsWith("create:")) {
                String prefix = text.substring("create:".length()).toLowerCase();
                ForgeRegistries.ITEMS.getKeys().stream()
                        .map(ResourceLocation::toString)
                        .filter(id -> id.startsWith(prefix))
                        .forEach(filtered::add);
            } else {
                for (String s : baseSuggestions) {
                    if (s.toLowerCase().startsWith(lower)) filtered.add(s);
                }
            }
        }

        public void renderSuggestions(GuiGraphics gui, int x, int y, int maxVisible) {
            if (!this.isFocused() || filtered.isEmpty()) return;

            int yOffset = 0;
            int visibleCount = Math.min(maxVisible, filtered.size() - scrollOffset);
            double guiMouseX = Minecraft.getInstance().mouseHandler.xpos() / Minecraft.getInstance().getWindow().getGuiScale();
            double guiMouseY = Minecraft.getInstance().mouseHandler.ypos() / Minecraft.getInstance().getWindow().getGuiScale();

            for (int i = 0; i < visibleCount; i++) {
                int index = i + scrollOffset;
                String text = filtered.get(index);

                int suggestionX = x - 2;
                int suggestionWidth = this.getWidth() + 4;
                int suggestionHeight = 10;
                int yTop = y + yOffset;

                if (guiMouseX >= suggestionX && guiMouseX <= suggestionX + suggestionWidth &&
                        guiMouseY >= yTop && guiMouseY <= yTop + suggestionHeight) {
                    selectedIndex = index;
                }

                gui.fill(suggestionX, yTop, suggestionX + suggestionWidth, yTop + suggestionHeight, 0xBF000000);
                int color = (index == selectedIndex) ? 0xBFFFFF00 : 0xBFFFFFFF;
                gui.drawString(myFont, Component.literal(text), x, yTop, color, false);

                yOffset += suggestionHeight;
            }
        }

        public void moveSelectionUp() {
            if (filtered.isEmpty()) return;
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = filtered.size() - 1;
            adjustScroll();
        }

        public void moveSelectionDown() {
            if (filtered.isEmpty()) return;
            selectedIndex++;
            if (selectedIndex >= filtered.size()) selectedIndex = 0;
            adjustScroll();
        }

        private void adjustScroll() {
            int maxVisible = 6;
            if (selectedIndex < scrollOffset) scrollOffset = selectedIndex;
            if (selectedIndex >= scrollOffset + maxVisible) scrollOffset = selectedIndex - maxVisible + 1;
        }

        public boolean scrollSuggestions(double delta) {
            if (!this.isFocused() || filtered.isEmpty()) return false;
            int maxVisible = 5;
            scrollOffset -= delta;
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > filtered.size() - maxVisible)
                scrollOffset = Math.max(filtered.size() - maxVisible, 0);
            return true;
        }

        public boolean applySuggestion() {
            if (!this.isFocused() || selectedIndex < 0 || selectedIndex >= filtered.size()) return false;
            String text = filtered.get(selectedIndex);

            if (getValue().startsWith("create:")) this.setValue("create:" + text);
            else this.setValue(text);

            this.setCursorPosition(this.getValue().length());
            filtered.clear();
            selectedIndex = -1;
            scrollOffset = 0;
            return true;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && selectedIndex >= 0) return applySuggestion();
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
