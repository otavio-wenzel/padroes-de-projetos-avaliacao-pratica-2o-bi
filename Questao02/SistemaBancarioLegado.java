package Questao02;

import java.util.HashMap;
import java.util.Objects;

public interface SistemaBancarioLegado {
    HashMap<String, Object> processarTransacao(HashMap<String, Object> parametros);

    final class Simulado implements SistemaBancarioLegado {
        @Override
        public HashMap<String, Object> processarTransacao(HashMap<String, Object> p) {
            Objects.requireNonNull(p, "parametros nulos");
            if (!p.containsKey("canal") || p.get("canal") == null) {
                throw new IllegalArgumentException("Legado: campo obrigatório ausente: 'canal'");
            }
            var card   = String.valueOf(p.get("numeroCartao"));
            var valor  = (Double) p.get("valor");
            var moedaC = (Integer) p.get("moeda");

            var ok = (card != null && card.length() >= 4 && valor != null && valor > 0);
            var resp = new HashMap<String, Object>();
            resp.put("status", ok ? 1 : 0);
            resp.put("authCode", ok ? "APV" + Math.abs(card.hashCode() % 100000) : null);
            resp.put("msg", ok ? "Transação aprovada" : "Transação negada");
            resp.put("currency", moedaC);
            return resp;
        }
    }
}