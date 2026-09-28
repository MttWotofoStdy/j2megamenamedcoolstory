/*
 * ============================================================
 *  GameStateManager — МЕНЕДЖЕР СОСТОЯНИЙ ИГРЫ
 * ============================================================
 *  Задача:
 *    - Хранить текущее состояние (currentState)
 *    - Переключать состояния (setState)
 *    - Передавать update/paint/keyPressed текущему состоянию
 *    - Возвращаться к предыдущему состоянию (goBack)
 * ============================================================
 */

package mobileapplication1.gameCore;

import javax.microedition.lcdui.Graphics;

public class GameStateManager {

    // ============================================================
    //  1. ПОЛЯ
    // ============================================================

    private IState currentState;    // Текущее активное состояние
    private IState previousState;   // Предыдущее состояние (для возврата)
    private boolean isRunning = true; // Флаг: обновлять ли состояние

    // ============================================================
    //  2. ПЕРЕКЛЮЧЕНИЕ СОСТОЯНИЙ
    // ============================================================

    public void setState(IState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("Состояние не может быть null!");
        }

        // Выход из текущего состояния
        if (currentState != null) {
            currentState.onExit();
        }

        // Сохраняем предыдущее состояние
        previousState = currentState;

        // Устанавливаем новое состояние
        currentState = newState;

        // Вход в новое состояние
        currentState.onEnter();

        System.out.println("Переключено на: " + currentState.getClass().getName());
    }

    public void goBack() {
        if (previousState != null) {
            setState(previousState);
        }
    }

    // ============================================================
    //  3. ПРОКСИ-МЕТОДЫ (ПЕРЕДАЮТ ВЫЗОВЫ ТЕКУЩЕМУ СОСТОЯНИЮ)
    // ============================================================

    public void update() {
        if (currentState != null && isRunning) {
            currentState.update();
        }
    }

    public void paint(Graphics g) {
        if (currentState != null) {
            currentState.paint(g);
        }
    }

    public void keyPressed(int keyCode) {
        if (currentState != null) {
            currentState.keyPressed(keyCode);
        }
    }

    public IState getCurrentState() {
        return currentState;
    }

    // ============================================================
    //  4. ПАУЗА / ВОЗОБНОВЛЕНИЕ
    // ============================================================

    public void pause() {
        isRunning = false;
    }

    public void resume() {
        isRunning = true;
    }
}