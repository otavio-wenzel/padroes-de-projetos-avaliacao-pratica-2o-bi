package Questao03;

public final class Medidas {
    private final double temperaturaC;
    private final double pressaoBar;
    private final double radiacaoMsv;
    private final boolean falhaResfriamento;
    private final int segundosAcima400;

    private Medidas(Builder b) {
        this.temperaturaC = b.temperaturaC;
        this.pressaoBar = b.pressaoBar;
        this.radiacaoMsv = b.radiacaoMsv;
        this.falhaResfriamento = b.falhaResfriamento;
        this.segundosAcima400 = b.segundosAcima400;
    }

    public double getTemperaturaC() { return temperaturaC; }
    public double getPressaoBar() { return pressaoBar; }
    public double getRadiacaoMsv() { return radiacaoMsv; }
    public boolean isFalhaResfriamento() { return falhaResfriamento; }
    public int getSegundosAcima400() { return segundosAcima400; }

    public static final class Builder {
        private double temperaturaC;
        private double pressaoBar;
        private double radiacaoMsv;
        private boolean falhaResfriamento;
        private int segundosAcima400;

        public Builder temperaturaC(double v){ this.temperaturaC = v; return this; }
        public Builder pressaoBar(double v){ this.pressaoBar = v; return this; }
        public Builder radiacaoMsv(double v){ this.radiacaoMsv = v; return this; }
        public Builder falhaResfriamento(boolean v){ this.falhaResfriamento = v; return this; }
        public Builder segundosAcima400(int v){ this.segundosAcima400 = v; return this; }

        public Medidas build(){ return new Medidas(this); }
    }
}