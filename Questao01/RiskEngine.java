package Questao01;

import java.util.Objects;

public final class RiskEngine {
    private RiskStrategy strategy;

    public RiskEngine(RiskStrategy initialStrategy) {
        this.strategy = Objects.requireNonNull(initialStrategy);
    }

    public void setStrategy(RiskStrategy newStrategy) {
        this.strategy = Objects.requireNonNull(newStrategy);
    }

    public String run(RiskContext ctx) {
        return strategy.calculate(ctx);
    }
}