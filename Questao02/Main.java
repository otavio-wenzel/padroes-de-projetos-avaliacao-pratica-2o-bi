package Questao02;

public class Main {
    public static void main(String[] args) {
        var legado = new SistemaBancarioLegado.Simulado();

        var adapter = new LegadoAdapter(legado, "ECOM");

        var brlOk = adapter.autorizar("4111111111111111", 250.00, "BRL");

        var usdNo = adapter.autorizar("0000", 0.00, "USD");

        var eurOk = adapter.autorizar("5555444433332222", 120.50, "EUR");

        System.out.println("\n=== Autorização BRL (OK) ===");
        System.out.println(brlOk);

        System.out.println("\n=== Autorização USD (NEGADA) ===");
        System.out.println(usdNo);

        System.out.println("\n=== Autorização EUR (OK) ===");
        System.out.println(eurOk);
    }
}