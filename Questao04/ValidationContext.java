package Questao04;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public final class ValidationContext {
    private int failureCount = 0;
    private boolean rolledBack = false;
    private final Deque<Runnable> rollbackStack = new ArrayDeque<>();
    private final Map<String, Long> timeoutsMillis = new HashMap<>();

    public void putTimeout(String validatorId, long ms) { timeoutsMillis.put(validatorId, ms); }
    public long getTimeoutOrDefault(String validatorId, long def) { return timeoutsMillis.getOrDefault(validatorId, def); }

    public void incFailure() { failureCount++; }
    public int getFailureCount() { return failureCount; }
    public boolean circuitOpen() { return failureCount >= 3; }

    public void pushRollback(Runnable r) { if (r != null) rollbackStack.push(r); }

    public void rollbackAllOnce() {
        if (rolledBack) return;
        while (!rollbackStack.isEmpty()) {
            try { rollbackStack.pop().run(); } catch (Exception ignored) {}
        }
        rolledBack = true;
    }
}