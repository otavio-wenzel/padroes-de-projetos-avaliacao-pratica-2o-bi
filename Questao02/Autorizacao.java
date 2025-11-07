package Questao02;

public final class Autorizacao {
    private final boolean autorizado;
    private final String codigoAutorizacao;
    private final String mensagem;
    private final String moeda;

    public Autorizacao(boolean autorizado, String codigoAutorizacao, String mensagem, String moeda) {
        this.autorizado = autorizado;
        this.codigoAutorizacao = codigoAutorizacao;
        this.mensagem = mensagem;
        this.moeda = moeda;
    }

    public boolean isAutorizado()        { return autorizado; }
    public String getCodigoAutorizacao() { return codigoAutorizacao; }
    public String getMensagem()          { return mensagem; }
    public String getMoeda()             { return moeda; }

    @Override public String toString() {
        return "Autorizacao{" +
                "autorizado=" + autorizado +
                ", codigoAutorizacao='" + codigoAutorizacao + '\'' +
                ", mensagem='" + mensagem + '\'' +
                ", moeda='" + moeda + '\'' +
                '}';
    }
}
