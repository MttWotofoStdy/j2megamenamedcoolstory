/*
 * ============================================================
 *  SaveManager — УПРАВЛЕНИЕ СОХРАНЕНИЯМИ
 * ============================================================
 *  Задача:
 *    - Сохранять прогресс игры (уровень, очки, позиция игрока, инвентарь)
 *    - Загружать прогресс
 *    - Удалять все сохранения
 *    - Хранить несколько слотов (3 слота)
 * ============================================================
 */

package mobileapplication1.gameCore;

import javax.microedition.rms.RecordStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SaveManager {

    // ============================================================
    //  1. ПОЛЯ
    // ============================================================

    private static final String STORE_NAME = "GameSaves";   // Имя хранилища
    private static final int RECORD_ID = 1;                // ID записи (всегда 1)

    // ---- ДАННЫЕ, КОТОРЫЕ СОХРАНЯЕМ ----
    public static int currentLevel = 1;      // Текущий уровень
    public static int score = 0;             // Очки
    public static int playerX = 150;         // Позиция игрока X
    public static int playerY = 100;         // Позиция игрока Y
    public static int playerHealth = 100;    // Здоровье игрока
    public static int[] inventory = new int[10]; // Инвентарь (0 = пусто, 1 = зелье, 2 = меч...)

    // ---- СЛОТЫ СОХРАНЕНИЙ (3 слота) ----
    public static final int SLOT_1 = 0;
    public static final int SLOT_2 = 1;
    public static final int SLOT_3 = 2;
    private static final int SLOT_COUNT = 3;

    // ---- ВСПОМОГАТЕЛЬНОЕ ----
    private static int activeSlot = SLOT_1;   // Какой слот сейчас активен

    // ============================================================
    //  2. ЗАГРУЗКА ДАННЫХ ПО УМОЛЧАНИЮ
    // ============================================================

    public static void loadDefaults() {
        currentLevel = 1;
        score = 0;
        playerX = 150;
        playerY = 100;
        playerHealth = 100;
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = 0;
        }
    }

    // ============================================================
    //  3. СОХРАНЕНИЕ ВСЕХ ДАННЫХ
    // ============================================================

    public static void saveGame() {
        saveGame(activeSlot);
    }

    public static void saveGame(int slot) {
        try {
            RecordStore rs = RecordStore.openRecordStore(STORE_NAME + "_" + slot, true);

            // Записываем данные в байтовый массив
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // ---- СОХРАНЯЕМ ВСЕ ПЕРЕМЕННЫЕ ----
            dos.writeInt(currentLevel);
            dos.writeInt(score);
            dos.writeInt(playerX);
            dos.writeInt(playerY);
            dos.writeInt(playerHealth);

            // Сохраняем инвентарь
            dos.writeInt(inventory.length);
            for (int i = 0; i < inventory.length; i++) {
                dos.writeInt(inventory[i]);
            }

            // ---- ЗАПИСЫВАЕМ В RECORDSTORE ----
            byte[] data = baos.toByteArray();
            if (rs.getNumRecords() > 0) {
                rs.setRecord(RECORD_ID, data, 0, data.length);
            } else {
                rs.addRecord(data, 0, data.length);
            }

            dos.close();
            baos.close();
            rs.closeRecordStore();

            System.out.println("Игра сохранена в слот " + (slot + 1));

        } catch (Exception e) {
            System.out.println("Ошибка сохранения игры: " + e.getMessage());
        }
    }

    // ============================================================
    //  4. ЗАГРУЗКА ВСЕХ ДАННЫХ
    // ============================================================

    public static boolean loadGame() {
        return loadGame(activeSlot);
    }

    public static boolean loadGame(int slot) {
        try {
            RecordStore rs = RecordStore.openRecordStore(STORE_NAME + "_" + slot, true);
            int numRecords = rs.getNumRecords();

            if (numRecords == 0) {
                System.out.println("Нет сохранений в слоте " + (slot + 1));
                rs.closeRecordStore();
                return false;
            }

            // ---- ЧИТАЕМ ДАННЫЕ ----
            byte[] data = rs.getRecord(RECORD_ID);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

            currentLevel = dis.readInt();
            score = dis.readInt();
            playerX = dis.readInt();
            playerY = dis.readInt();
            playerHealth = dis.readInt();

            // Читаем инвентарь
            int invSize = dis.readInt();
            if (invSize > inventory.length) invSize = inventory.length;
            for (int i = 0; i < invSize; i++) {
                inventory[i] = dis.readInt();
            }

            dis.close();
            rs.closeRecordStore();

            System.out.println("Игра загружена из слота " + (slot + 1));
            return true;

        } catch (Exception e) {
            System.out.println("Ошибка загрузки игры: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    //  5. ПРОВЕРКА НАЛИЧИЯ СОХРАНЕНИЯ
    // ============================================================

    public static boolean hasSave() {
        return hasSave(activeSlot);
    }

    public static boolean hasSave(int slot) {
        try {
            RecordStore rs = RecordStore.openRecordStore(STORE_NAME + "_" + slot, true);
            int numRecords = rs.getNumRecords();
            rs.closeRecordStore();
            return numRecords > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    //  6. УДАЛЕНИЕ СОХРАНЕНИЯ
    // ============================================================

    public static void deleteSave() {
        deleteSave(activeSlot);
    }

    public static void deleteSave(int slot) {
        try {
            RecordStore.deleteRecordStore(STORE_NAME + "_" + slot);
            System.out.println("Сохранение слота " + (slot + 1) + " удалено.");
        } catch (Exception e) {
            System.out.println("Ошибка удаления сохранения слота " + (slot + 1) + ": " + e.getMessage());
        }
    }

    public static void deleteAllSaves() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            try {
                RecordStore.deleteRecordStore(STORE_NAME + "_" + i);
                System.out.println("Сохранение слота " + (i + 1) + " удалено.");
            } catch (Exception e) {
                // Игнорируем, если хранилища не было
            }
        }
    }

    // ============================================================
    //  7. УПРАВЛЕНИЕ СЛОТАМИ
    // ============================================================

    public static int getActiveSlot() {
        return activeSlot;
    }

    public static void setActiveSlot(int slot) {
        if (slot >= 0 && slot < SLOT_COUNT) {
            activeSlot = slot;
            System.out.println("Активный слот: " + (slot + 1));
        }
    }

    public static int getSlotCount() {
        return SLOT_COUNT;
    }

    // ============================================================
    //  8. ПОЛУЧЕНИЕ ДАННЫХ ДЛЯ ОТОБРАЖЕНИЯ
    // ============================================================

    public static String getSlotInfo(int slot) {
        if (hasSave(slot)) {
            // Временно загружаем данные, чтобы показать информацию
            try {
                RecordStore rs = RecordStore.openRecordStore(STORE_NAME + "_" + slot, true);
                byte[] data = rs.getRecord(RECORD_ID);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

                int level = dis.readInt();
                int score = dis.readInt();
                dis.close();
                rs.closeRecordStore();

                return "Уровень " + level + "  Очки: " + score;
            } catch (Exception e) {
                return "Сохранение есть";
            }
        } else {
            return "Пусто";
        }
    }

    // ============================================================
    //  9. ОТЛАДКА
    // ============================================================

    public static void debugInfo() {
        System.out.println("=== SaveManager Debug ===");
        System.out.println("Active slot: " + (activeSlot + 1));
        System.out.println("Level: " + currentLevel);
        System.out.println("Score: " + score);
        System.out.println("Player X: " + playerX);
        System.out.println("Player Y: " + playerY);
        System.out.println("Health: " + playerHealth);
        System.out.println("Inventory: ");
        for (int i = 0; i < inventory.length; i++) {
            System.out.print(inventory[i] + " ");
        }
        System.out.println();
        System.out.println("=========================");
    }
}