package Questao03;

public final class UsinaNuclear {
    private EstadoUsina estado;
    private EstadoId estadoId;
    private boolean passouPorVermelho;
    private boolean modoManutencao;
    private EstadoId estadoAntesManutencao;

    public UsinaNuclear() {
        setEstadoInterno(EstadoId.DESLIGADA);
    }

    public void setModoManutencao(boolean ativo) {
        if (ativo && !modoManutencao) {
            modoManutencao = true;
            estadoAntesManutencao = estadoId;
            setEstadoInterno(EstadoId.MANUTENCAO);
        } else if (!ativo && modoManutencao) {
            modoManutencao = false;
            setEstadoInterno(estadoAntesManutencao != null ? estadoAntesManutencao : EstadoId.DESLIGADA);
        }
    }

    public void processar(Medidas medidas) {
        if (modoManutencao) {
            log("MANUTENCAO ativa; leituras não provocam transições.");
            return;
        }
        EstadoId proximo = estado.avaliar(this, medidas);
        aplicarTransicaoSegura(proximo);
    }

    void marcarPassouPorVermelho() { this.passouPorVermelho = true; }

    private void aplicarTransicaoSegura(EstadoId proximo) {
        if (proximo == null || proximo == estadoId) return;

        if (proximo == EstadoId.EMERGENCIA && !passouPorVermelho) {
            log("Bloqueado: EMERGENCIA exige passagem prévia por ALERTA_VERMELHO.");
            return;
        }

        if (estadoId == EstadoId.EMERGENCIA && proximo == EstadoId.OPERACAO_NORMAL) {
            log("Bloqueado: recuo direto de EMERGENCIA para OPERACAO_NORMAL não é permitido.");
            return;
        }

        setEstadoInterno(proximo);
    }

    private void setEstadoInterno(EstadoId id) {
        this.estadoId = id;
        this.estado = EstadoFactory.create(id);
        if (id == EstadoId.ALERTA_VERMELHO) {
            this.passouPorVermelho = true;
        }
        log("Transição -> " + id);
    }

    public EstadoId getEstadoId() { return estadoId; }

    private void log(String msg){ System.out.println("[USINA] " + msg); }
}