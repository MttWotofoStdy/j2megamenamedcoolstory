/*
 * ============================================================
 *  ISelectorScreen — ИНТЕРФЕЙС ДЛЯ ЭКРАНОВ С ВЫБОРОМ
 * ============================================================
 *  Задача: задать контракт для всех экранов, где есть список для выбора.
 *  Методы:
 *    - getItems()      → получить список пунктов
 *    - getSelectedIndex() → получить выбранный индекс
 *    - setSelectedIndex() → установить выбранный индекс
 *    - onSelect()      → что делать при выборе
 *    - paint()         → отрисовать экран
 * ============================================================
 */

package mobileapplication1.gameCore;

import javax.microedition.lcdui.Graphics;

public interface ISelectorScreen {

    String[] getItems();

    int getSelectedIndex();

    void setSelectedIndex(int index);

    void onSelect(int index);

    void paint(Graphics g, int screenWidth, int screenHeight);
}