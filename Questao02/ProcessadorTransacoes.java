package Questao02;

public interface ProcessadorTransacoes {
    Autorizacao autorizar(String cartao, double valor, String moeda);
}