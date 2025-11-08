package Questao04;

import java.util.List;
import java.util.concurrent.*;

public final class ValidatorFactory {
    private ValidatorFactory() {}

    public static List<Validator> defaultChain() {
        return List.of(
                new XmlSchemaValidator(),
                new CertificadoValidator(),
                new RegrasFiscaisValidator(),
                new BancoDadosValidator(),
                new SefazValidator()
        );
    }

    static ValidationResult withTimeout(Callable<ValidationResult> task, String id, ValidationContext ctx, long defTimeoutMs) {
        long timeout = ctx.getTimeoutOrDefault(id, defTimeoutMs);
        ExecutorService ex = Executors.newSingleThreadExecutor();
        try {
            Future<ValidationResult> f = ex.submit(task);
            return f.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            return ValidationResult.timeout(id, "Tempo excedido (" + timeout + "ms)");
        } catch (Exception e) {
            return ValidationResult.fail(id, "Erro: " + e.getMessage());
        } finally {
            ex.shutdownNow();
        }
    }

    private static final class XmlSchemaValidator implements Validator {
        @Override public String id() { return "XML_SCHEMA"; }
        @Override public boolean requiresAllPreviousPass() { return false; }

        @Override public ValidationResult validate(NFEDocument nfe, ValidationContext ctx) {
            return withTimeout(() -> {
                boolean ok = nfe.getXml() != null && nfe.getXml().contains("<NFe>") && nfe.getXml().contains("</NFe>");
                return ok ? ValidationResult.pass(id(), "XML compatível com XSD (simulado)")
                          : ValidationResult.fail(id(), "XML inválido (schema não confere)");
            }, id(), ctx, 600);
        }
    }

    private static final class CertificadoValidator implements Validator {
        @Override public String id() { return "CERTIFICADO"; }
        @Override public boolean requiresAllPreviousPass() { return false; }

        @Override public ValidationResult validate(NFEDocument nfe, ValidationContext ctx) {
            return withTimeout(() -> {
                String c = nfe.getCertificado();
                if (c == null || c.isBlank()) return ValidationResult.fail(id(), "Certificado ausente");
                if ("REVOGADO".equalsIgnoreCase(c)) return ValidationResult.fail(id(), "Certificado revogado");
                return ValidationResult.pass(id(), "Certificado válido (simulado)");
            }, id(), ctx, 600);
        }
    }

    private static final class RegrasFiscaisValidator implements Validator {
        @Override public String id() { return "REGRAS_FISCAIS"; }
        @Override public boolean requiresAllPreviousPass() { return true; }

        @Override public ValidationResult validate(NFEDocument nfe, ValidationContext ctx) {
            return withTimeout(() -> {
                boolean ok = Integer.parseInt(nfe.getNumero()) % 2 == 0;
                return ok ? ValidationResult.pass(id(), "Cálculo de impostos OK (simulado)")
                          : ValidationResult.fail(id(), "Divergência no cálculo de impostos");
            }, id(), ctx, 800);
        }
    }

    private static final class BancoDadosValidator implements Validator {
        @Override public String id() { return "BANCO_DADOS"; }
        @Override public boolean requiresAllPreviousPass() { return false; }

        @Override public ValidationResult validate(NFEDocument nfe, ValidationContext ctx) {
            return withTimeout(() -> {
                if (nfe.isPersisted()) {
                    return ValidationResult.pass(id(), "Documento já persistido (ok)");
                }
                if (nfe.getNumero().endsWith("99")) {
                    return ValidationResult.fail(id(), "Número duplicado no banco (simulado)");
                }

                nfe.markPersisted();
                ctx.pushRollback(() -> nfe.markUnpersisted());

                return ValidationResult.pass(id(), "Inserção/registro no BD concluída (simulado)");
            }, id(), ctx, 500);
        }
    }

    private static final class SefazValidator implements Validator {
        @Override public String id() { return "SEFAZ"; }
        @Override public boolean requiresAllPreviousPass() { return true; }

        @Override public ValidationResult validate(NFEDocument nfe, ValidationContext ctx) {
            return withTimeout(() -> {
                if ("XX".equalsIgnoreCase(nfe.getUf())) {
                    return ValidationResult.fail(id(), "UF não suportada pela SEFAZ (simulado)");
                }
                return ValidationResult.pass(id(), "SEFAZ OK (consulta simulada)");
            }, id(), ctx, 1200);
        }
    }
}