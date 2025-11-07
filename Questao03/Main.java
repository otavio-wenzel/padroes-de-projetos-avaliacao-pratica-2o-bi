package Questao03;
public class Main {
    public static void main(String[] args) {

        UsinaNuclear usina = new UsinaNuclear();

        usina.processar(new Medidas.Builder()
                .temperaturaC(250).pressaoBar(20).radiacaoMsv(1.0)
                .falhaResfriamento(false).segundosAcima400(0).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(320).pressaoBar(22).radiacaoMsv(1.2)
                .falhaResfriamento(false).segundosAcima400(0).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(350).pressaoBar(22).radiacaoMsv(1.3)
                .falhaResfriamento(false).segundosAcima400(0).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(410).pressaoBar(25).radiacaoMsv(2.0)
                .falhaResfriamento(false).segundosAcima400(31).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(420).pressaoBar(26).radiacaoMsv(2.5)
                .falhaResfriamento(true).segundosAcima400(60).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(280).pressaoBar(20).radiacaoMsv(1.0)
                .falhaResfriamento(false).segundosAcima400(0).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(300).pressaoBar(20).radiacaoMsv(1.0)
                .falhaResfriamento(false).segundosAcima400(0).build());

        usina.processar(new Medidas.Builder()
                .temperaturaC(350).pressaoBar(20).radiacaoMsv(1.0)
                .falhaResfriamento(false).segundosAcima400(0).build());
        usina.processar(new Medidas.Builder()
                .temperaturaC(290).pressaoBar(20).radiacaoMsv(1.0)
                .falhaResfriamento(false).segundosAcima400(0).build());

        usina.setModoManutencao(true);
        usina.processar(new Medidas.Builder()
                .temperaturaC(420).pressaoBar(28).radiacaoMsv(3.0)
                .falhaResfriamento(true).segundosAcima400(50).build());
        usina.setModoManutencao(false);

        usina.processar(new Medidas.Builder()
                .temperaturaC(305).pressaoBar(21).radiacaoMsv(1.1)
                .falhaResfriamento(false).segundosAcima400(0).build());
    }
}