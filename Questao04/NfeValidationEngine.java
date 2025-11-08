package Questao04;

import java.util.List;

public final class NfeValidationEngine {
    private final List<Validator> chain;

    public NfeValidationEngine(List<Validator> chain) {
        this.chain = chain;
    }

    public boolean run(NFEDocument doc, ValidationContext ctx) {
        System.out.println("=== Iniciando validação: " + doc + " ===");

        boolean anyPostDbFailure = false;

        for (Validator v : chain) {
            if (ctx.circuitOpen()) {
                System.out.println(">> Circuit breaker ATIVO (>=3 falhas). Interrompendo cadeia.");
                break;
            }
            if (v.requiresAllPreviousPass() && ctx.getFailureCount() > 0) {
                System.out.println(">> " + v.id() + " PULADO (há falhas anteriores).");
                continue;
            }

            ValidationResult r = v.validate(doc, ctx);
            System.out.println(r);

            if (!r.isPassed()) {
                ctx.incFailure();
                ctx.rollbackAllOnce();
                anyPostDbFailure = true;
            }
        }

        if (ctx.getFailureCount() > 0 && !anyPostDbFailure) {
            ctx.rollbackAllOnce();
        }

        boolean success = ctx.getFailureCount() == 0;
        System.out.println("=== Conclusão: " + (success ? "APROVADO" : "REPROVADO (" + ctx.getFailureCount() + " falha(s))") + " ===");
        return success;
    }
}