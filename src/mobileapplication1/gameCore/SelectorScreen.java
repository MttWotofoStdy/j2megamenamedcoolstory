/*
 * ============================================================
 *  SelectorScreen — БАЗОВЫЙ КЛАСС ДЛЯ ЭКРАНОВ С ВЫБОРОМ
 * ============================================================
 *  Задача:
 *    - Хранить массив пунктов (items)
 *    - Хранить выбранный индекс (selectedIndex)
 *    - Перемещать выбор вверх/вниз (moveUp/moveDown)
 *    - Обрабатывать выбор (onSelect)
 *    - Рисовать экран (paint)
 * ============================================================
 *  Наследники:
 *    - MenuSelectorScreen
 *    - InventorySelectorScreen
 *    - DialogSelectorScreen
 * ============================================================
 */

package mobileapplication1.gameCore;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;

public abstract class SelectorScreen implements ISelectorScreen {

    // ============================================================
    //  1. ПОЛЯ
    // ============================================================

    protected String[] items;          // Массив пунктов (например, {"Играть", "Выход"})
    protected int selectedIndex = 0;   // Индекс выбранного пункта
    protected String title = "";       // Заголовок экрана
    protected Canvas canvas;           // Ссылка на холст для перерисовки

    // ============================================================
    //  2. КОНСТРУКТОРЫ
    // ============================================================

    public SelectorScreen(String[] items, Canvas canvas) {
        this.items = items;
        this.canvas = canvas;
    }

    public SelectorScreen(String[] items, String title, Canvas canvas) {
        this.items = items;
        this.title = title;
        this.canvas = canvas;
    }

    // ============================================================
    //  3. МЕТОДЫ ДОСТУПА К ДАННЫМ
    // ============================================================

    public String[] getItems() {
        return items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index < 0) index = items.length - 1;
        if (index >= items.length) index = 0;
        this.selectedIndex = index;
        if (canvas != null) canvas.repaint(); // Принудительная перерисовка
    }

    // ============================================================
    //  4. УПРАВЛЕНИЕ ВЫБОРОМ
    // ============================================================

    public void moveUp() {
        selectedIndex--;
        if (selectedIndex < 0) selectedIndex = items.length - 1;
        if (canvas != null) canvas.repaint();
    }

    public void moveDown() {
        selectedIndex++;
        if (selectedIndex >= items.length) selectedIndex = 0;
        if (canvas != null) canvas.repaint();
    }

    public void selectCurrent() {
        onSelect(selectedIndex);  // Вызываем абстрактный метод onSelect
    }

    // ============================================================
    //  5. ОБРАБОТКА КЛАВИШ (ПО УМОЛЧАНИЮ)
    // ============================================================

    public boolean keyPressed(int keyCode) {
        if (keyCode == -7) {   // Стрелка вверх
            moveUp();
            return true;
        } else if (keyCode == -6) {   // Стрелка вниз
            moveDown();
            return true;
        } else if (keyCode == 48 || keyCode == -5 || keyCode == 53) {   // Выбор (0, Fire)
            selectCurrent();
            return true;
        }
        return false;
    }

    // ============================================================
    //  6. АБСТРАКТНЫЙ МЕТОД (ОБЯЗАТЕЛЬНЫЙ ДЛЯ НАСЛЕДНИКОВ)
    // ============================================================

    public abstract void onSelect(int index);

    // ============================================================
    //  7. ОТРИСОВКА (ПО УМОЛЧАНИЮ)
    // ============================================================

    public int getTargetFPS() {
        return 30;  // По умолчанию 30 FPS для экранов
    }

    public void update() {
        // Пустая реализация — может быть переопределена
    }

    public void paint(Graphics g, int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        // ---- ЗАГОЛОВОК (белый) ----
        if (title != null && title.length() > 0) {
            g.setColor(255, 255, 255);
            int tx = (screenWidth - g.getFont().stringWidth(title)) / 2;
            if (tx < 0) tx = 0;
            g.drawString(title, tx, 30, Graphics.TOP | Graphics.LEFT);
        }

        // ---- ПУНКТЫ МЕНЮ ----
        int y = 80;
        for (int i = 0; i < items.length; i++) {
            if (i == selectedIndex) {
                g.setColor(255, 0, 0);       // КРАСНЫЙ — выделенный
            } else {
                g.setColor(255, 255, 255);   // БЕЛЫЙ — обычный
            }
            String item = items[i];
            int x = (screenWidth - g.getFont().stringWidth(item)) / 2;
            if (x < 0) x = 0;
            g.drawString(item, x, y, Graphics.TOP | Graphics.LEFT);
            y += 25;
        }

        // ---- ПОДСКАЗКА (серая) ----
        g.setColor(150, 150, 150);
        String hint = "Стрелки: выбор, 0: выбор";
        int hx = (screenWidth - g.getFont().stringWidth(hint)) / 2;
        if (hx < 0) hx = 0;
        g.drawString(hint, hx, screenHeight - 20, Graphics.TOP | Graphics.LEFT);
    }
}