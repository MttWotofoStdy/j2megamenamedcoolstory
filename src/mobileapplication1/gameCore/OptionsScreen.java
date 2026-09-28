package mobileapplication1.gameCore;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import mobileapplication1.HelloMIDlet;

public class OptionsScreen {

    // ============================================================
    //  1. ПОЛЯ
    // ============================================================

    private String[] optionsItems = {"Вибрация", "Звук", "Удалить сохранения", "Назад"};
    private int selectedIndex = 0;

    private int bgOptionsTimer = 0;
    private int bgOptionsX = 0;

    // ---- МАССИВ ТЕКСТУР: [0] = OFF, [1] = ON ----
    private Image[] toggleTextures;
    private int toggleWidth = 20;
    private int toggleHeight = 24;

    private HelloMIDlet midlet;

    // ============================================================
    //  2. КОНСТРУКТОР
    // ============================================================

    public OptionsScreen(HelloMIDlet midlet) {
        this.midlet = midlet;
        loadTextures();
    }

    // ============================================================
    //  3. ЗАГРУЗКА ТЕКСТУР
    // ============================================================

    private void loadTextures() {
        toggleTextures = new Image[2];
        try {
            toggleTextures[0] = Image.createImage("/textures/menus/options/off.png");
            toggleTextures[1] = Image.createImage("/textures/menus/options/on.png");
            System.out.println("Текстуры рычага загружены!");
        } catch (Exception e) {
            toggleTextures[0] = null;
            toggleTextures[1] = null;
            System.out.println("Ошибка загрузки текстур рычага: " + e.getMessage());
        }
    }

    public void onEnter() {
        if (toggleTextures == null || toggleTextures[0] == null) {
            loadTextures();
        }
        bgOptionsTimer = 0;
        if (midlet != null) midlet.repaintCanvas();
    }

    // ============================================================
    //  4. ОБНОВЛЕНИЕ ФОНА
    // ============================================================

    public void updateBackground() {
        bgOptionsTimer++;
        if (bgOptionsTimer > 240) {
            bgOptionsTimer = 0;
        }
        double rad = bgOptionsTimer * 0.05;
        double sinVal = Math.sin(rad);
        bgOptionsX = (int)(-352 + sinVal * 362) - 20;
    }

    public int getBgOptionsX() {
        return bgOptionsX;
    }

    // ============================================================
    //  5. ОТРИСОВКА
    // ============================================================

    public void paint(Graphics g, int screenWidth, int screenHeight) {
        drawCentered(g, "НАСТРОЙКИ", 30, 0xFFFFFF, screenWidth);

        int y = 80;
        for (int i = 0; i < optionsItems.length; i++) {
            int color = (i == selectedIndex) ? 0xFF0000 : 0xFFFFFF;
            drawCentered(g, optionsItems[i], y, color, screenWidth);

            if (i == 0) {
                int toggleX = screenWidth / 2 + 80;
                int toggleY = y - toggleHeight / 2 + 10;
                drawToggle(g, toggleX, toggleY, Settings.isVibrationEnabled());
            } else if (i == 1) {
                int toggleX = screenWidth / 2 + 80;
                int toggleY = y - toggleHeight / 2 + 10;
                drawToggle(g, toggleX, toggleY, Settings.isSoundEnabled());
            }

            y += 25;
        }

        // drawCentered(g, "Нажми 0 для возврата", screenHeight - 20, 0x969696, screenWidth);
    }

    // ============================================================
    //  6. ОБРАБОТКА КЛАВИШ
    // ============================================================

    public void keyPressed(int keyCode) {
        System.out.println("OPTIONS: keyCode = " + keyCode);

        if (keyCode == -1 || keyCode == 50) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = optionsItems.length - 1;
        } else if (keyCode == -2 || keyCode == 56) {
            selectedIndex++;
            if (selectedIndex >= optionsItems.length) selectedIndex = 0;
        } else if (keyCode == -5 || keyCode == 53) {
            System.out.println("OPTIONS: ВЫБОР пункта " + selectedIndex);
            switch (selectedIndex) {
                case 0:
                    // ============================================================
                    //  ИСПРАВЛЕНО: toggleVibr() (было toggleVibration())
                    // ============================================================
                    Settings.toggleVibr();
                    break;
                case 1:
                    // ============================================================
                    //  ИСПРАВЛЕНО: toggleSound() (работает)
                    // ============================================================
                    Settings.toggleSound();
                    break;
                case 2:
                    System.out.println("Удаление сохранений...");
                    break;
                case 3:
                    if (midlet != null) midlet.goToMenu();
                    break;
            }
        }
    }

    // ============================================================
    //  7. ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ============================================================

    private void drawCentered(Graphics g, String text, int y, int color, int screenWidth) {
        g.setColor(color);
        int x = (screenWidth - g.getFont().stringWidth(text)) / 2;
        if (x < 0) x = 0;
        g.drawString(text, x, y, Graphics.TOP | Graphics.LEFT);
    }

    private void drawToggle(Graphics g, int x, int y, boolean isOn) {
        if (toggleTextures == null || toggleTextures[0] == null || toggleTextures[1] == null) {
            g.setColor(isOn ? 0x00FF00 : 0xFF0000);
            g.drawString(isOn ? "ON" : "OFF", x, y, Graphics.TOP | Graphics.LEFT);
            return;
        }

        int index = isOn ? 1 : 0;
        g.drawImage(toggleTextures[index], x, y, Graphics.TOP | Graphics.LEFT);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index < 0) index = optionsItems.length - 1;
        if (index >= optionsItems.length) index = 0;
        this.selectedIndex = index;
    }
}