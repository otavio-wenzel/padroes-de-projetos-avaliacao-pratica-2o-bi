package Questao01;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class RiskStrategyFactory {
    private RiskStrategyFactory() {}

    public static RiskStrategy create(RiskAlgorithm alg) {
        return switch (alg) {
            case VAR                -> new VarStrategy();
            case EXPECTED_SHORTFALL -> new ExpectedShortfallStrategy();
            case STRESS_TESTING     -> new StressTestingStrategy();
            case HISTORICAL_VAR     -> new HistoricalVarStrategy();
        };
    }

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static String brl(BigDecimal v) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(v);
    }
    private static String pct(BigDecimal v) {
        var nf = NumberFormat.getPercentInstance(PT_BR);
        nf.setMaximumFractionDigits(0);
        return nf.format(v.doubleValue());
    }

    private static final class VarStrategy implements RiskStrategy {
        @Override
        public String calculate(RiskContext ctx) {
            BigDecimal aprox = ctx.getPortfolioExposure().multiply(new BigDecimal("0.03"));
            return """
                   Algoritmo: VaR (Paramétrico)
                   Confiança: %s | Horizonte: %sd
                   Exposição da carteira: %s
                   Resultado (aprox.): %s (~3%% da exposição)
                   """.formatted(
                    pct(ctx.getConfidenceLevel()),
                    ctx.getHorizonDays(),
                    brl(ctx.getPortfolioExposure()),
                    brl(aprox)
            ).trim();
        }
    }

    private static final class ExpectedShortfallStrategy implements RiskStrategy {
        @Override
        public String calculate(RiskContext ctx) {
            var ordered = ctx.getScenarioPnLs().stream().sorted().toList();
            int k = Math.max(1, ordered.size() / 5);
            BigDecimal worstSum = BigDecimal.ZERO;
            for (int i = 0; i < k; i++) {
                worstSum = worstSum.add(ordered.get(i).abs());
            }
            return """
                   Algoritmo: Expected Shortfall (ES)
                   Confiança: %s | Horizonte: %sd | Cenários: %d
                   Regra (dummy): somar os %d piores resultados
                   Resultado (aprox.): %s
                   """.formatted(
                    pct(ctx.getConfidenceLevel()),
                    ctx.getHorizonDays(),
                    ordered.size(),
                    k,
                    brl(worstSum)
            ).trim();
        }
    }

    private static final class StressTestingStrategy implements RiskStrategy {
        @Override
        public String calculate(RiskContext ctx) {
            var pior = ctx.getScenarioPnLs().stream()
                    .min(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
            return """
                   Algoritmo: Stress Testing
                   Cenário mais adverso observado: %s
                   Interpretação: considerar este pior caso como referência de estresse
                   """.formatted(brl(pior)).trim();
        }
    }

    private static final class HistoricalVarStrategy implements RiskStrategy {
        @Override
        public String calculate(RiskContext ctx) {
            List<BigDecimal> sorted = ctx.getScenarioPnLs().stream()
                    .sorted()
                    .toList();

            if (sorted.isEmpty()) {
                return "Algoritmo: Historical VaR\nNão há cenários para calcular.";
            }

            double oneMinusConf = 1.0 - ctx.getConfidenceLevel().doubleValue();
            int idx = (int) Math.floor(sorted.size() * oneMinusConf);
            if (idx < 0) idx = 0;
            if (idx >= sorted.size()) idx = sorted.size() - 1;

            BigDecimal quantil = sorted.get(idx);
            return """
                   Algoritmo: Historical VaR
                   Confiança: %s | Horizonte: %sd | Amostras: %d
                   Regra (dummy): pegar o quantil de cauda (~%s) dos PnLs
                   Resultado (quantil): %s
                   """.formatted(
                    pct(ctx.getConfidenceLevel()),
                    ctx.getHorizonDays(),
                    sorted.size(),
                    NumberFormat.getPercentInstance(PT_BR).format(oneMinusConf),
                    brl(quantil)
            ).trim();
        }
    }
}