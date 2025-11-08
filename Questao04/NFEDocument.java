package Questao04;

import java.util.Objects;

public final class NFEDocument {
    private String numero;
    private String xml;
    private String certificado;
    private String uf;
    private boolean persisted;

    public NFEDocument(String numero, String xml, String certificado, String uf) {
        this.numero = Objects.requireNonNull(numero);
        this.xml = Objects.requireNonNull(xml);
        this.certificado = Objects.requireNonNull(certificado);
        this.uf = Objects.requireNonNull(uf);
    }

    public String getNumero() { return numero; }
    public String getXml() { return xml; }
    public String getCertificado() { return certificado; }
    public String getUf() { return uf; }

    public boolean isPersisted() { return persisted; }
    public void markPersisted() { this.persisted = true; }
    public void markUnpersisted() { this.persisted = false; }

    @Override public String toString() {
        return "NFEDocument{numero='" + numero + "', uf='" + uf + "', persisted=" + persisted + "}";
    }
}