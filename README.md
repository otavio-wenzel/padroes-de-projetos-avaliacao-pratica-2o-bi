Questão 01 — Padrão Strategy

Observando o problema, vi que tínhamos vários algoritmos de risco (VaR, ES, Stress, e mais este que apliquei: Historical VaR) e que eu precisava trocar entre eles em tempo de execução. Todos usam um conjunto grande de parâmetros.

Strategy para transformar cada algoritmo em uma peça com o mesmo contrato (RiskStrategy.calculate(ctx)), permitindo troca em runtime sem if/else gigante.

Parameter Object (RiskContext) para agrupar os parâmetros (exposição, confiança, horizonte, cenários) e manter as assinaturas limpas.

Factory Method para o cliente pedir a estratégia por enum (RiskAlgorithm) e não conhecer as classes concretas.


Isso resolveu: consigo trocar o algoritmo a qualquer momento, o cliente fica desacoplado, e adicionar um novo algoritmo virou rotina (crio a nova Strategy e coloco um case na fábrica).
Também segui o estilo do professor: classes públicas final e concretas privadas na fábrica.

SOLID que guiei na prática:
SRP (cada classe com um papel), OCP (novo algoritmo sem mexer no cliente), DIP (engine depende da abstração), ISP/LSP (contrato simples e respeitado).


---

Questão 02 — Padrão Adapter

Aqui eu tinha uma interface moderna (autorizar(cartao, valor, moeda)) e um legado que só aceita HashMap com chaves e formatos específicos, incluindo código de moeda (USD=1, EUR=2, BRL=3) e um campo obrigatório que nem existe no contrato moderno (usei canal).

Acredito que o problema é de compatibilização de interfaces. Então fiz dessa forma:

Adapter (LegadoAdapter) para expor a interface moderna e traduzir para o legado; e na volta, converter o HashMap de resposta para um DTO moderno (Autorizacao).

CurrencyCodec para mapear ISO - código conforme a restrição do legado.

O campo obrigatório do legado, como o canal, eu injetei no adapter com um valor default seguro (ex.: "ECOM"), sem poluir a interface moderna.


Assim, quem usa meu código continua falando “moderno”, enquanto eu faço o legado entender sem alterá-lo. E ficou fácil de testar: simulo o legado e valido o mapeamento.

SOLID que segui:
SRP (adapter só traduz, codec só converte, DTO só representa), OCP (mudar campo do legado impacta só o adapter/codec), DIP (cliente depende da abstração ProcessadorTransacoes, não do legado).


---

Questão 03 — Padrão State

Na usina, percebi que o comportamento muda conforme o estado (DESLIGADA, NORMAL, AMARELO, VERMELHO, EMERGÊNCIA) e que cada transição tem regras (t > 300°C, t > 400°C por > 30s, falha no resfriamento). Também existiam políticas globais de segurança: EMERGÊNCIA só depois de VERMELHO, evitar ciclos perigosos, e um modo MANUTENÇÃO que congela transições.

Apliquei:

State: cada estado ganhou sua classe e decide as próprias transições a partir das leituras (Medidas).

Um Context (UsinaNuclear) para aplicar as regras globais (bloquear “EMERGÊNCIA → NORMAL”, exigir ter passado por VERMELHO) e controlar o modo MANUTENÇÃO.

Parameter Object (Medidas) para juntar temperatura, pressão, radiação etc.

Factory com estados concretos privados, conforme o padrão de exemplo.


Na prática, o código ficou organizado por estado, sem um if/else monstruoso, e as políticas críticas ficaram centralizadas no Context. O modo manutenção realmente “congela” o sistema até eu desligar.

SOLID na minha implementação:
SRP (estado decide, context orquestra, Medidas carrega dados), OCP (posso inserir novos estados/regras), DIP/ISP (contrato de estado simples).


---

Questão 04 — Usei Chain of Responsibility

Aqui a NF-e passa por vários validadores (schema, certificado, regras fiscais, banco, SEFAZ). Eu precisava
pular validadores quando antes já falhou (3 e 5 só rodam se anteriores passaram), parar depois de 3 falhas (circuit breaker), desfazer o que modificou (rollback do BD) e respeitar timeout por validador.

Então montei uma cadeia (CoR):

Cada validador é um nó especializado.

A engine pula os condicionais quando existe falha anterior, interrompe quando bate 3 falhas e, se algum passo modificou o documento (BD), registra rollback; se algo à frente falhar, eu desfaço.

Cada nó roda com timeout específico, se estourar, vira TIMEOUT e entra no contador de falhas.


Isso deixou o fluxo previsível e extensível: posso adicionar novos validadores sem mexer no resto, e os logs ficam claros (PASS/FAIL/TIMEOUT por etapa).

SOLID que segui:
SRP (cada validador focado no seu tema; engine orquestra; contexto guarda falhas/rollback/timeout), OCP (adicionar validador é “plugar outro nó”), DIP/ISP (engine fala com a interface Validator).
