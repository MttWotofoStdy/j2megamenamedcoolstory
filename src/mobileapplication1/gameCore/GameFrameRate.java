/*
 * ============================================================
 *  GameFrameRate — УПРАВЛЕНИЕ ЧАСТОТОЙ КАДРОВ (FPS)
 * ============================================================
 *  Задача:
 *    - Устанавливать целевой FPS (setTargetFPS)
 *    - Вычислять задержку между кадрами (sleepForNextFrame)
 *    - Считать реальный FPS для отладки (getCurrentFPS)
 * ============================================================
 */

package mobileapplication1.gameCore;

public class GameFrameRate {

    // ============================================================
    //  1. ПОЛЯ (состояние объекта)
    // ============================================================

    private int targetFPS = 60;              // Целевой FPS (по умолчанию 60)
    private long frameDuration = 1000 / targetFPS; // Длительность одного кадра в мс
    private long lastFrameStartTime = 0;     // Время начала последнего кадра
    private int frameCount = 0;              // Счётчик кадров для FPS
    private long lastFPSCheckTime = 0;       // Время последнего обновления FPS
    private boolean isInitialized = false;   // Были ли инициализированы таймеры
    private int totalFrames = 0;             // Общее количество кадров (не сбрасывается)

    // ============================================================
    //  2. КОНСТРУКТОРЫ
    // ============================================================

    public GameFrameRate() {
        this(60);  // По умолчанию 60 FPS
    }

    public GameFrameRate(int targetFPS) {
        setTargetFPS(targetFPS);   // Устанавливаем целевой FPS
        initTimers();              // Инициализируем таймеры
    }

    // ============================================================
    //  3. ИНИЦИАЛИЗАЦИЯ ТАЙМЕРОВ
    // ============================================================

    private void initTimers() {
        if (!isInitialized) {
            long currentTime = System.currentTimeMillis();
            lastFrameStartTime = currentTime;
            lastFPSCheckTime = currentTime;
            isInitialized = true;
        }
    }

    public void resetTimers() {
        long currentTime = System.currentTimeMillis();
        lastFrameStartTime = currentTime;
        lastFPSCheckTime = currentTime;
        frameCount = 0;
        totalFrames = 0;
    }

    // ============================================================
    //  4. ОСНОВНАЯ ЛОГИКА (РАСЧЁТ ЗАДЕРЖКИ)
    // ============================================================

    /**
     * Вычисляет, сколько нужно спать, чтобы сохранить целевой FPS.
     * Возвращает время сна в миллисекундах.
     */
    public long calculateSleepTime() {
        initTimers();

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastFrameStartTime; // Время выполнения кадра
        long sleepTime = frameDuration - elapsed;        // Оставшееся время до следующего кадра

        updateFrameCounter(currentTime);  // Обновляем счётчик FPS

        lastFrameStartTime = currentTime; // Запоминаем время начала нового кадра
        totalFrames++;                    // Увеличиваем общий счётчик

        return Math.max(0, sleepTime);    // Нельзя спать отрицательное время
    }

    /**
     * Засыпает на время, необходимое для сохранения целевого FPS.
     * Используется в игровом цикле.
     */
    public void sleepForNextFrame() {
        try {
            Thread.sleep(frameDuration);   // ВСЕГДА спим ровно frameDuration мс
        } catch (InterruptedException e) {
            // Игнорируем прерывания
        }
    }

    // ============================================================
    //  5. ОБНОВЛЕНИЕ СЧЁТЧИКА FPS
    // ============================================================

    private void updateFrameCounter(long currentTime) {
        frameCount++;  // Увеличиваем счётчик кадров

        // Если прошла 1 секунда — сбрасываем счётчик
        if (currentTime - lastFPSCheckTime >= 1000) {
            frameCount = 0;
            lastFPSCheckTime = currentTime;
        }
    }

    // ============================================================
    //  6. УСТАНОВКА ЦЕЛЕВОГО FPS
    // ============================================================

    public void setTargetFPS(int newFPS) {
        if (newFPS <= 0) {
            throw new IllegalArgumentException("FPS должен быть больше 0!");
        }
        this.targetFPS = newFPS;
        this.frameDuration = 1000 / targetFPS; // Пересчитываем длительность кадра
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    // ============================================================
    //  7. ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ============================================================

    public long getFrameDuration() {
        return frameDuration;
    }

    public boolean isSecondPassed() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastFPSCheckTime) >= 1000;
    }

    public void resetFrameCounter() {
        frameCount = 0;
        lastFPSCheckTime = System.currentTimeMillis();
    }

    // ============================================================
    //  8. ПОЛУЧЕНИЕ ТЕКУЩЕГО FPS
    // ============================================================

    public int getCurrentFPS() {
        return frameCount;  // Количество кадров за последнюю секунду
    }

    public int getInstantFPS() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastFrameStartTime;
        if (elapsed == 0) return 0;
        return (int)(1000 / elapsed); // Мгновенный FPS
    }

    public int getTotalFrameCount() {
        return totalFrames;  // Общее количество кадров с начала игры
    }

    public long getElapsedSinceFrameStart() {
        return System.currentTimeMillis() - lastFrameStartTime;
    }

    // ============================================================
    //  9. ОТЛАДКА
    // ============================================================

    public void debugInfo() {
        System.out.println("=== GameFrameRate Debug ===");
        System.out.println("Target FPS: " + targetFPS);
        System.out.println("Frame duration: " + frameDuration + " ms");
        System.out.println("Current FPS: " + frameCount);
        System.out.println("Instant FPS: " + getInstantFPS());
        System.out.println("Total frames: " + totalFrames);
        System.out.println("Is initialized: " + isInitialized);
        System.out.println("============================");
    }
}