package Questao02;

import java.util.Map;

public final class CurrencyCodec {
    private CurrencyCodec() {}

    private static final Map<String,Integer> ISO_TO_CODE = Map.of(
            "USD", 1,
            "EUR", 2,
            "BRL", 3
    );
    private static final Map<Integer,String> CODE_TO_ISO = Map.of(
            1, "USD",
            2, "EUR",
            3, "BRL"
    );

    public static int toLegacyCode(String iso) {
        Integer c = ISO_TO_CODE.get(iso == null ? null : iso.toUpperCase());
        if (c == null) throw new IllegalArgumentException("Moeda não suportada: " + iso + " (use USD/EUR/BRL)");
        return c;
    }
    public static String toISO(Integer code) {
        String iso = CODE_TO_ISO.get(code);
        if (iso == null) throw new IllegalArgumentException("Código de moeda legado inválido: " + code);
        return iso;
    }
}