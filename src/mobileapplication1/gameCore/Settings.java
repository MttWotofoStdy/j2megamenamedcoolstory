/*
 * ============================================================
 *  Settings — НАСТРОЙКИ ИГРЫ (ТЕКСТОВЫЙ ФОРМАТ)
 * ============================================================
 *  Формат файла:
 *    [settings]
 *    sound=1
 *    vibr=1
 * ============================================================
 */

package mobileapplication1.gameCore;

import javax.microedition.rms.RecordStore;

public class Settings {

    // ============================================================
    //  1. ПОЛЯ
    // ============================================================

    private static final String STORE_NAME = "gamesettings";

    private static int sound = 1;
    private static int vibr = 1;

    // ============================================================
    //  2. ЗАГРУЗКА (С ПРИНУДИТЕЛЬНЫМ СОЗДАНИЕМ)
    // ============================================================

    public static void load() {
        try {
            // ---- ПРОВЕРЯЕМ, СУЩЕСТВУЕТ ЛИ ХРАНИЛИЩЕ ----
            RecordStore rs = RecordStore.openRecordStore(STORE_NAME, true);
            int numRecords = rs.getNumRecords();

            if (numRecords > 0) {
                // ---- ФАЙЛ ЕСТЬ — ЗАГРУЖАЕМ ----
                byte[] data = rs.getRecord(1);
                String text = new String(data, "UTF-8");
                System.out.println("Загружен текст:\n" + text);
                parseSettings(text);
                System.out.println("Настройки загружены: sound=" + sound + ", vibr=" + vibr);
            } else {
                // ---- ФАЙЛА НЕТ — СОЗДАЁМ ----
                System.out.println("Файл настроек не найден. Создаём со значениями по умолчанию...");
                sound = 1;
                vibr = 1;
                
                // ЗАКРЫВАЕМ ХРАНИЛИЩЕ ПЕРЕД СОХРАНЕНИЕМ
                rs.closeRecordStore();
                
                // СОХРАНЯЕМ НАСТРОЙКИ (ЭТО СОЗДАСТ ФАЙЛ)
                save();
                
                System.out.println("Настройки по умолчанию созданы.");
                return;
            }

            rs.closeRecordStore();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки настроек: " + e.getMessage());
            sound = 1;
            vibr = 1;
            // ПРИ ОШИБКЕ — ПЫТАЕМСЯ СОЗДАТЬ ФАЙЛ
            save();
        }
    }

    // ============================================================
    //  3. ПАРСИНГ
    // ============================================================

    private static void parseSettings(String text) {
        int pos = 0;
        int len = text.length();

        while (pos < len) {
            int endLine = text.indexOf('\n', pos);
            if (endLine == -1) endLine = len;

            String line = text.substring(pos, endLine).trim();
            pos = endLine + 1;

            if (line.length() == 0) continue;
            if (line.startsWith("[") && line.endsWith("]")) continue;

            int eqPos = line.indexOf('=');
            if (eqPos != -1) {
                String key = line.substring(0, eqPos).trim();
                String value = line.substring(eqPos + 1).trim();

                if (key.equals("sound")) {
                    sound = Integer.parseInt(value);
                } else if (key.equals("vibr")) {
                    vibr = Integer.parseInt(value);
                }
            }
        }
    }

    // ============================================================
    //  4. СОХРАНЕНИЕ
    // ============================================================

    public static void save() {
        try {
            String text = "[settings]\n";
            text += "sound=" + sound + "\n";
            text += "vibr=" + vibr + "\n";

            System.out.println("Сохраняем текст:\n" + text);

            RecordStore rs = RecordStore.openRecordStore(STORE_NAME, true);
            byte[] data = text.getBytes("UTF-8");

            if (rs.getNumRecords() > 0) {
                rs.setRecord(1, data, 0, data.length);
            } else {
                rs.addRecord(data, 0, data.length);
            }

            rs.closeRecordStore();

            System.out.println("Настройки сохранены: sound=" + sound + ", vibr=" + vibr);
        } catch (Exception e) {
            System.out.println("Ошибка сохранения настроек: " + e.getMessage());
        }
    }

    // ============================================================
    //  5. МЕТОДЫ ДОСТУПА
    // ============================================================

    public static void setSound(int value) {
        sound = value;
        save();
    }

    public static int getSound() {
        return sound;
    }

    public static boolean isSoundEnabled() {
        return sound == 1;
    }

    public static void toggleSound() {
        sound = (sound == 1) ? 0 : 1;
        save();
        System.out.println("Звук: " + (sound == 1 ? "ВКЛ" : "ВЫКЛ"));
    }

    public static void setVibr(int value) {
        vibr = value;
        save();
    }

    public static int getVibr() {
        return vibr;
    }

    public static boolean isVibrationEnabled() {
        return vibr == 1;
    }

    public static void toggleVibr() {
        vibr = (vibr == 1) ? 0 : 1;
        save();
        System.out.println("Вибрация: " + (vibr == 1 ? "ВКЛ" : "ВЫКЛ"));
    }

    // ============================================================
    //  6. ВСПОМОГАТЕЛЬНЫЕ
    // ============================================================

    public static void resetToDefaults() {
        sound = 1;
        vibr = 1;
        save();
        System.out.println("Настройки сброшены.");
    }

    public static void deleteStore() {
        try {
            RecordStore.deleteRecordStore(STORE_NAME);
            System.out.println("Хранилище настроек удалено.");
        } catch (Exception e) {
            System.out.println("Ошибка удаления: " + e.getMessage());
        }
    }

    // ============================================================
    //  7. ОТЛАДКА
    // ============================================================

    public static void debugInfo() {
        System.out.println("=== Settings Debug ===");
        System.out.println("sound: " + sound);
        System.out.println("vibr: " + vibr);
        System.out.println("======================");
    }
}