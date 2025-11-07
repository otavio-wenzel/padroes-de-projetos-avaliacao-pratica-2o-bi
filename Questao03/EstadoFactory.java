package Questao03;

public final class EstadoFactory {
    private EstadoFactory() {}

    public static EstadoUsina create(EstadoId id) {
        return switch (id) {
            case DESLIGADA        -> new Desligada();
            case OPERACAO_NORMAL  -> new OperacaoNormal();
            case ALERTA_AMARELO   -> new AlertaAmarelo();
            case ALERTA_VERMELHO  -> new AlertaVermelho();
            case EMERGENCIA       -> new Emergencia();
            case MANUTENCAO       -> new Manutencao();
        };
    }

    private static boolean acima300(Medidas m){ return m.getTemperaturaC() > 300.0; }
    private static boolean acima400(Medidas m){ return m.getTemperaturaC() > 400.0; }
    private static boolean acima400Por30s(Medidas m){ return acima400(m) && m.getSegundosAcima400() > 30; }
    private static boolean seguroParaNormal(Medidas m){
        return m.getTemperaturaC() <= 300.0 && !m.isFalhaResfriamento() && m.getRadiacaoMsv() <= 5.0;
    }
    private static boolean seguroParaAmarelo(Medidas m){
        return m.getTemperaturaC() > 300.0 && m.getTemperaturaC() < 400.0 && !m.isFalhaResfriamento();
    }

    private static final class Desligada implements EstadoUsina {
        @Override public EstadoId avaliar(UsinaNuclear ctx, Medidas m) {
            if (seguroParaNormal(m)) return EstadoId.OPERACAO_NORMAL;
            return EstadoId.DESLIGADA;
        }
        @Override public String nome(){ return "DESLIGADA"; }
    }

    private static final class OperacaoNormal implements EstadoUsina {
        @Override public EstadoId avaliar(UsinaNuclear ctx, Medidas m) {
            if (acima300(m)) return EstadoId.ALERTA_AMARELO;
            return EstadoId.OPERACAO_NORMAL;
        }
        @Override public String nome(){ return "OPERACAO_NORMAL"; }
    }

    private static final class AlertaAmarelo implements EstadoUsina {
        @Override public EstadoId avaliar(UsinaNuclear ctx, Medidas m) {
            if (acima400Por30s(m)) return EstadoId.ALERTA_VERMELHO;
            if (seguroParaNormal(m)) return EstadoId.OPERACAO_NORMAL;
            if (seguroParaAmarelo(m)) return EstadoId.ALERTA_AMARELO;
            if (m.isFalhaResfriamento()) return EstadoId.ALERTA_VERMELHO;
            return EstadoId.ALERTA_AMARELO;
        }
        @Override public String nome(){ return "ALERTA_AMARELO"; }
    }

    private static final class AlertaVermelho implements EstadoUsina {
        @Override public EstadoId avaliar(UsinaNuclear ctx, Medidas m) {
            if (m.isFalhaResfriamento()) {
                ctx.marcarPassouPorVermelho();
                return EstadoId.EMERGENCIA;
            }
            if (!acima400(m)) return EstadoId.ALERTA_AMARELO;
            return EstadoId.ALERTA_VERMELHO;
        }
        @Override public String nome(){ return "ALERTA_VERMELHO"; }
    }

    private static final class Emergencia implements EstadoUsina {
        @Override public EstadoId avaliar(UsinaNuclear ctx, Medidas m) {
            if (seguroParaAmarelo(m) || seguroParaNormal(m)) {
                return EstadoId.ALERTA_VERMELHO;
            }
            return EstadoId.EMERGENCIA;
        }
        @Override public String nome(){ return "EMERGENCIA"; }
    }

    private static final class Manutencao implements EstadoUsina {
        @Override public EstadoId avaliar(UsinaNuclear ctx, Medidas m) {
            return EstadoId.MANUTENCAO;
        }
        @Override public String nome(){ return "MANUTENCAO"; }
    }
}