package Questao01;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


public final class RiskContext {
    private final BigDecimal portfolioExposure;
    private final BigDecimal confidenceLevel;
    private final int horizonDays;
    private final List<BigDecimal> scenarioPnLs;

    private RiskContext(Builder b) {
        this.portfolioExposure = Objects.requireNonNull(b.portfolioExposure);
        this.confidenceLevel   = Objects.requireNonNull(b.confidenceLevel);
        this.horizonDays       = b.horizonDays;
        this.scenarioPnLs      = Objects.requireNonNull(b.scenarioPnLs);
    }

    public BigDecimal getPortfolioExposure() { return portfolioExposure; }
    public BigDecimal getConfidenceLevel()   { return confidenceLevel; }
    public int getHorizonDays()              { return horizonDays; }
    public List<BigDecimal> getScenarioPnLs(){ return scenarioPnLs; }

    public static final class Builder {
        private BigDecimal portfolioExposure = BigDecimal.ZERO;
        private BigDecimal confidenceLevel   = new BigDecimal("0.95");
        private int horizonDays              = 10;
        private List<BigDecimal> scenarioPnLs = List.of();

        public Builder portfolioExposure(BigDecimal v){ this.portfolioExposure = v; return this; }
        public Builder confidenceLevel(BigDecimal v)  { this.confidenceLevel   = v; return this; }
        public Builder horizonDays(int d)             { this.horizonDays       = d; return this; }
        public Builder scenarioPnLs(List<BigDecimal> s){ this.scenarioPnLs     = s; return this; }

        public RiskContext build(){ return new RiskContext(this); }
    }
}