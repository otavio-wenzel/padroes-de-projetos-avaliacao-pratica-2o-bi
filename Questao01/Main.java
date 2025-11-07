package Questao01;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    private static void bloco(String titulo, Runnable r) {
        System.out.println("\n================ " + titulo + " ================");
        r.run();
    }

    public static void main(String[] args) {
        var ctx = new RiskContext.Builder()
                .portfolioExposure(new BigDecimal("1000000"))
                .confidenceLevel(new BigDecimal("0.99"))
                .horizonDays(10)
                .scenarioPnLs(List.of(
                        new BigDecimal("-15000"),
                        new BigDecimal("-40000"),
                        new BigDecimal("-5000"),
                        new BigDecimal("8000"),
                        new BigDecimal("-22000"),
                        new BigDecimal("-70000"),
                        new BigDecimal("12000"),
                        new BigDecimal("-3500"),
                        new BigDecimal("-11000"),
                        new BigDecimal("5000")
                ))
                .build();

        var engine = new RiskEngine(RiskStrategyFactory.create(RiskAlgorithm.VAR));

        bloco("1) VaR (Paramétrico)", () -> System.out.println(engine.run(ctx)));

        engine.setStrategy(RiskStrategyFactory.create(RiskAlgorithm.EXPECTED_SHORTFALL));
        bloco("2) Expected Shortfall (ES)", () -> System.out.println(engine.run(ctx)));

        engine.setStrategy(RiskStrategyFactory.create(RiskAlgorithm.STRESS_TESTING));
        bloco("3) Stress Testing", () -> System.out.println(engine.run(ctx)));

        engine.setStrategy(RiskStrategyFactory.create(RiskAlgorithm.HISTORICAL_VAR));
        bloco("4) Historical VaR", () -> System.out.println(engine.run(ctx)));

        System.out.println("\n(Obs.: valores são ilustrativos/dummy para facilitar leitura.)");
    }
}