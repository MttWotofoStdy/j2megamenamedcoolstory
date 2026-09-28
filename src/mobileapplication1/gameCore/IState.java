/*
 * ============================================================
 *  IState — ИНТЕРФЕЙС ДЛЯ СОСТОЯНИЙ ИГРЫ
 * ============================================================
 *  Задача: задать контракт для всех состояний (меню, игра, босс, инвентарь).
 *  Методы:
 *    - onEnter()   → вызывается при входе в состояние
 *    - onExit()    → вызывается при выходе из состояния
 *    - update()    → обновление логики каждый кадр
 *    - paint()     → отрисовка
 *    - keyPressed() → обработка клавиш
 * ============================================================
 */

package mobileapplication1.gameCore;

import javax.microedition.lcdui.Graphics;

public interface IState {

    void onEnter();

    void onExit();

    void update();

    void paint(Graphics g);

    void keyPressed(int keyCode);
}