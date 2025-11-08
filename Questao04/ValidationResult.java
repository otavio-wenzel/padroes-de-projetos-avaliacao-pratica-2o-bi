package Questao04;

public final class ValidationResult {
    private final String validatorId;
    private final boolean passed;
    private final boolean timedOut;
    private final String message;

    private ValidationResult(String validatorId, boolean passed, boolean timedOut, String message) {
        this.validatorId = validatorId;
        this.passed = passed;
        this.timedOut = timedOut;
        this.message = message;
    }
    public static ValidationResult pass(String id, String msg){ return new ValidationResult(id, true, false, msg); }
    public static ValidationResult fail(String id, String msg){ return new ValidationResult(id, false, false, msg); }
    public static ValidationResult timeout(String id, String msg){ return new ValidationResult(id, false, true, msg); }

    public String getValidatorId() { return validatorId; }
    public boolean isPassed() { return passed; }
    public boolean isTimedOut() { return timedOut; }
    public String getMessage() { return message; }

    @Override public String toString() {
        var status = timedOut ? "TIMEOUT" : (passed ? "PASS" : "FAIL");
        return "[" + validatorId + "] " + status + " - " + message;
    }
}