package Questao04;

public class Main {
    public static void main(String[] args) {
        var ctx = new ValidationContext();
        ctx.putTimeout("XML_SCHEMA", 600);
        ctx.putTimeout("CERTIFICADO", 600);
        ctx.putTimeout("REGRAS_FISCAIS", 800);
        ctx.putTimeout("BANCO_DADOS", 500);
        ctx.putTimeout("SEFAZ", 1200);

        var engine = new NfeValidationEngine(ValidatorFactory.defaultChain());

        var nfeOk = new NFEDocument("000124", "<NFe>...</NFe>", "OK", "PR");
        engine.run(nfeOk, ctx);

        System.out.println("\n--------------------------------------------\n");

        var ctx2 = new ValidationContext();
        var nfeFiscalFail = new NFEDocument("000125", "<NFe>...</NFe>", "OK", "PR");
        engine.run(nfeFiscalFail, ctx2);

        System.out.println("\n--------------------------------------------\n");

        var ctx3 = new ValidationContext();
        var nfeRollback = new NFEDocument("000126", "<NFe>...</NFe>", "OK", "XX"); // UF "XX" força falha na SEFAZ
        engine.run(nfeRollback, ctx3);
        System.out.println("Documento após execução (esperado persisted=false por rollback): " + nfeRollback);

        System.out.println("\n--------------------------------------------\n");

        var ctx4 = new ValidationContext();
        var nfeBreaker = new NFEDocument("000199", "<INVALIDO>", "REVOGADO", "XX");        engine.run(nfeBreaker, ctx4);
    }
}