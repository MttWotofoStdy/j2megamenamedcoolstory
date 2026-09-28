package mobileapplication1;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;
import mobileapplication1.gameCore.GameFrameRate;
import mobileapplication1.gameCore.OptionsScreen;  // <-- ИМПОРТ

public class HelloMIDlet extends MIDlet {

    // ============================================================
    //  1. ПОЛЯ
    // ============================================================

    private Display display;
    private GameCanvas canvas;

    // ---- ФОНЫ ----
    private Image backgroundMenu;
    private Image backgroundOptions;
    private Image currentBackground;

    private static final int GAME_WIDTH = 320;
    private static final int GAME_HEIGHT = 240;

    private boolean running = true;
    private GameFrameRate fps;

    // ---- ЭКРАН НАСТРОЕК (ОТДЕЛЬНЫЙ КЛАСС) ----
    private OptionsScreen optionsScreen;

    // ---- СОСТОЯНИЯ ----
    private static final int STATE_MENU = 0;
    private static final int STATE_HELP = 1;
    private static final int STATE_OPTIONS = 2;
    private static final int STATE_LOAD = 3;
    private static final int STATE_GAME = 4;
    private int gameState = STATE_MENU;

    // ---- МЕНЮ ----
    private String[] menuItems = {"New game", "Помощь", "Настройки", "Загрузить игру", "Выход"};
    private int selectedIndex = 0;

    // ---- ПОМОЩЬ ----
    private String[] helpItems = {"Управление:", "Стрелки - движение", "0 - выход", "Назад"};
    private int helpIndex = 0;

    // ---- ИГРА ----
    private int playerX = GAME_WIDTH / 2 - 10;
    private int playerY = GAME_HEIGHT / 2 - 10;

    // ============================================================
    //  2. КОНСТРУКТОР
    // ============================================================

    public HelloMIDlet() {
        display = Display.getDisplay(this);

        // ---- ЗАГРУЗКА ФОНОВ ----
        try {
            backgroundMenu = Image.createImage("/backgrounds/zadnik_main_menu.png");
            System.out.println("Фон меню загружен!");
        } catch (Exception e) {
            backgroundMenu = null;
        }

        try {
            backgroundOptions = Image.createImage("/backgrounds/options_bg.png");
            System.out.println("Фон настроек загружен!");
        } catch (Exception e) {
            backgroundOptions = null;
        }

        currentBackground = backgroundMenu;

        // ---- СОЗДАНИЕ ХОЛСТА ----
        canvas = new GameCanvas(false) {
            public void paint(Graphics g) {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return;

                // ---- РИСУЕМ ФОН ----
                if (currentBackground != null) {
                    if (gameState == STATE_OPTIONS) {
                        g.drawImage(currentBackground, optionsScreen.getBgOptionsX(), 0, Graphics.TOP | Graphics.LEFT);
                        g.setColor(0, 0, 0);
                        if (optionsScreen.getBgOptionsX() > 0) {
                            g.fillRect(0, 0, optionsScreen.getBgOptionsX(), h);
                        }
                        int rightOffset = optionsScreen.getBgOptionsX() + currentBackground.getWidth();
                        if (rightOffset < w) {
                            g.fillRect(rightOffset, 0, w - rightOffset, h);
                        }
                    } else {
                        g.drawImage(currentBackground, 0, 0, Graphics.TOP | Graphics.LEFT);
                    }
                } else {
                    g.setColor(0, 0, 0);
                    g.fillRect(0, 0, w, h);
                }

                // ---- РИСУЕМ СОСТОЯНИЕ ----
                switch (gameState) {
                    case STATE_MENU:
                        drawMenu(g, w, h);
                        break;
                    case STATE_HELP:
                        drawHelp(g, w, h);
                        break;
                    case STATE_OPTIONS:
                        optionsScreen.paint(g, w, h);   // <-- РИСУЕМ НАСТРОЙКИ
                        break;
                    case STATE_LOAD:
                        drawLoad(g, w, h);
                        break;
                    case STATE_GAME:
                        drawGame(g, w, h);
                        break;
                }
            }

            protected void keyPressed(int keyCode) {
                System.out.println("MAIN: keyCode = " + keyCode);

                switch (gameState) {
                    case STATE_MENU:
                        menuKeyPressed(keyCode);
                        break;
                    case STATE_HELP:
                        helpKeyPressed(keyCode);
                        break;
                    case STATE_OPTIONS:
                        optionsScreen.keyPressed(keyCode);  // <-- ПЕРЕДАЁМ В НАСТРОЙКИ
                        break;
                    case STATE_LOAD:
                        loadKeyPressed(keyCode);
                        break;
                    case STATE_GAME:
                        gameKeyPressed(keyCode);
                        break;
                }
            }
        };

        canvas.setFullScreenMode(true);
        fps = new GameFrameRate(20);

        // ---- СОЗДАНИЕ ЭКРАНА НАСТРОЕК ----
        optionsScreen = new OptionsScreen(this);
    }

    // ============================================================
    //  3. МЕТОДЫ УПРАВЛЕНИЯ ЭКРАНАМИ
    // ============================================================

    private void switchBackground(Image newBackground) {
        if (currentBackground != newBackground) {
            currentBackground = newBackground;
            System.gc();
            System.out.println("Фон переключён.");
        }
    }

    /**
     * Возврат в меню (вызывается из OptionsScreen).
     */
    public void goToMenu() {
        gameState = STATE_MENU;
        selectedIndex = 2;
        switchBackground(backgroundMenu);
    }

    // ============================================================
    //  4. ВСПОМОГАТЕЛЬНЫЙ МЕТОД
    // ============================================================

    private void drawCentered(Graphics g, String text, int y, int color, int screenWidth) {
        g.setColor(color);
        int x = (screenWidth - g.getFont().stringWidth(text)) / 2;
        if (x < 0) x = 0;
        g.drawString(text, x, y, Graphics.TOP | Graphics.LEFT);
    }

    // ============================================================
    //  5. МЕНЮ
    // ============================================================

    private void drawMenu(Graphics g, int w, int h) {
        drawCentered(g, "ГЛАВНОЕ МЕНЮ", 30, 0xFFFFFF, w);
        int y = 80;
        for (int i = 0; i < menuItems.length; i++) {
            int color = (i == selectedIndex) ? 0xFF0000 : 0xFFFFFF;
            drawCentered(g, menuItems[i], y, color, w);
            y += 25;
        }
    }
/**
 * Принудительная перерисовка холста.
 * Используется из других классов (например, OptionsScreen).
 */
public void repaintCanvas() {
    if (canvas != null) {
        canvas.repaint();
    }
}

    private void menuKeyPressed(int keyCode) {
        if (keyCode == -1 || keyCode == 50) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = menuItems.length - 1;
        } else if (keyCode == -2 || keyCode == 56) {
            selectedIndex++;
            if (selectedIndex >= menuItems.length) selectedIndex = 0;
        } else if (keyCode == -5 || keyCode == 53) {
            switch (selectedIndex) {
                case 0:
                    gameState = STATE_GAME;
                    break;
                case 1:
                    gameState = STATE_HELP;
                    break;
                case 2:
                    gameState = STATE_OPTIONS;
                    switchBackground(backgroundOptions);
                    break;
                case 3:
                    gameState = STATE_LOAD;
                    break;
                case 4:
                    exitMIDlet();
                    break;
            }
        }
    }

    // ============================================================
    //  6. ПОМОЩЬ
    // ============================================================

    private void drawHelp(Graphics g, int w, int h) {
        drawCentered(g, "ПОМОЩЬ", 30, 0xFFFFFF, w);
        int y = 80;
        for (int i = 0; i < helpItems.length; i++) {
            int color = (i == helpIndex) ? 0xFF0000 : 0xFFFFFF;
            drawCentered(g, helpItems[i], y, color, w);
            y += 25;
        }
        drawCentered(g, "Нажми 0 для возврата", h - 20, 0x969696, w);
    }

    private void helpKeyPressed(int keyCode) {
        if (keyCode == -1 || keyCode == 50) {
            helpIndex--;
            if (helpIndex < 0) helpIndex = helpItems.length - 1;
        } else if (keyCode == -2 || keyCode == 56) {
            helpIndex++;
            if (helpIndex >= helpItems.length) helpIndex = 0;
        } else if (keyCode == -5 || keyCode == 53) {
            if (helpIndex == helpItems.length - 1) {
                gameState = STATE_MENU;
                selectedIndex = 1;
            }
        }
    }

    // ============================================================
    //  7. ЗАГРУЗКА
    // ============================================================

    private void drawLoad(Graphics g, int w, int h) {
        drawCentered(g, "ЗАГРУЗКА ИГРЫ", 30, 0xFFFFFF, w);
        drawCentered(g, "Выберите сохранение:", 60, 0xFFFFFF, w);

        String[] loadItems = {"Слот 1", "Слот 2", "Слот 3", "Назад"};
        int y = 90;
        for (int i = 0; i < loadItems.length; i++) {
            int color = (i == 0) ? 0xFF0000 : 0xFFFFFF;
            drawCentered(g, loadItems[i], y, color, w);
            y += 25;
        }
        drawCentered(g, "Нажми 0 для возврата", h - 20, 0x969696, w);
    }

    private void loadKeyPressed(int keyCode) {
        if (keyCode == 48) {
            gameState = STATE_MENU;
            selectedIndex = 3;
        }
    }

    // ============================================================
    //  8. ИГРА
    // ============================================================

    private void drawGame(Graphics g, int w, int h) {
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);
        drawCentered(g, "ИГРА", 30, 0xFFFFFF, w);
        g.setColor(0, 0, 255);
        g.fillRect(playerX, playerY, 20, 20);
        drawCentered(g, "Нажми 0 для выхода в меню", h - 20, 0xFFFFFF, w);
    }

    private void gameKeyPressed(int keyCode) {
        if (keyCode == 48) {
            gameState = STATE_MENU;
            return;
        }
        if (keyCode == -1 || keyCode == 50) playerY -= 10;
        if (keyCode == -2 || keyCode == 56) playerY += 10;
        if (keyCode == -3) playerX -= 10;
        if (keyCode == -4) playerX += 10;
    }

    // ============================================================
    //  9. ЖИЗНЕННЫЙ ЦИКЛ
    // ============================================================

    public void startApp() {
        display.setCurrent(canvas);
        running = true;

        new Thread() {
            public void run() {
                while (running) {
                    try {
                        // ---- ОБНОВЛЯЕМ ФОН НАСТРОЕК ----
                        if (gameState == STATE_OPTIONS) {
                            optionsScreen.updateBackground();
                        }

                        canvas.repaint();
                        fps.sleepForNextFrame();
                    } catch (Throwable t) {
                        running = false;
                    }
                }
            }
        }.start();
    }

    public void pauseApp() {
        running = false;
    }

    public void destroyApp(boolean unconditional) {
        running = false;
    }

    public void exitMIDlet() {
        running = false;
        destroyApp(true);
        notifyDestroyed();
    }
}