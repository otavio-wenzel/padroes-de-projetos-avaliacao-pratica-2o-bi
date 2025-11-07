package Questao02;

import java.util.HashMap;
import java.util.Objects;

public final class LegadoAdapter implements ProcessadorTransacoes {

    private final SistemaBancarioLegado legado;
    private final String canalObrigatorio;

    public LegadoAdapter(SistemaBancarioLegado legado, String canalObrigatorio) {
        this.legado = Objects.requireNonNull(legado);
        this.canalObrigatorio = (canalObrigatorio == null || canalObrigatorio.isBlank())
                ? "ECOM" : canalObrigatorio;
    }

    @Override
    public Autorizacao autorizar(String cartao, double valor, String moeda) {
        var params = new HashMap<String, Object>();
        params.put("numeroCartao", cartao);
        params.put("valor", valor);
        params.put("moeda", CurrencyCodec.toLegacyCode(moeda));
        params.put("canal", canalObrigatorio);

        var resp = legado.processarTransacao(params);

        var status      = (Integer) resp.get("status");
        var authCode    = (String)  resp.get("authCode");
        var mensagem    = (String)  resp.get("msg");
        var currencyOld = (Integer) resp.get("currency");

        var autorizado  = (status != null && status == 1);
        var moedaISO    = CurrencyCodec.toISO(currencyOld);
        return new Autorizacao(autorizado, authCode, mensagem, moedaISO);
    }
}