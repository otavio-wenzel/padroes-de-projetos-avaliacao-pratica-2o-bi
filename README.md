# Questão 01 — Decisão de Design (para colar no `README.md`)

## Versão completa (explicada, linguagem simples)

### 1) Problema

* Existem vários **algoritmos de risco** (VaR, Expected Shortfall, Stress Testing, Historical VaR).
* O negócio pode **trocar o algoritmo em tempo de execução**.
* Os algoritmos **compartilham muitos parâmetros** financeiros.
* O cliente **não deve conhecer** as classes concretas que implementam cada cálculo.

### 2) Decisão

* **Padrão principal — Strategy:** cada algoritmo implementa o mesmo **contrato** `RiskStrategy.calculate(ctx)`, permitindo **troca em runtime** sem `if/else`.
* **Apoio — Parameter Object:** `RiskContext` reúne todos os parâmetros (exposição, confiança, horizonte, cenários etc.), mantendo a interface limpa e coesa.
* **Apoio — Factory Method:** `RiskStrategyFactory.create(RiskAlgorithm)` devolve a implementação **por enum**, mantendo o cliente **desacoplado** dos concretos.

> **Estilo do professor:** classes públicas marcadas como **`final`** e **classes concretas privadas** (dentro da Factory). O cliente só “enxerga” **abstrações** e **fábrica**.

### 3) Por que atende ao enunciado

* **Intercambiável em runtime:** `RiskEngine#setStrategy(...)`.
* **Contexto complexo compartilhado:** `RiskContext` evita “explosão” de parâmetros.
* **Cliente muda sem conhecer implementação:** usa **enum + Factory**, não referencia classes concretas.
* **≥ 3 algoritmos distintos:** VaR, ES, Stress e **Historical VaR** (4 no total).

### 4) SOLID aplicado

* **SRP:**
  `RiskContext` (dados), **cada Strategy** (um cálculo), `RiskEngine` (orquestra), `RiskStrategyFactory` (cria).
* **OCP:**
  Novo algoritmo = **nova Strategy privada** + 1 `case` no `switch` da Factory. Nada mais muda.
* **LSP:**
  Todas as estratégias respeitam `RiskStrategy` (podem se substituir).
* **ISP:**
  Interface pequena e específica (`calculate(ctx)`).
* **DIP:**
  `RiskEngine` depende de **abstração** (`RiskStrategy`), não de concretos.

### 5) Alternativas consideradas (e por que não)

* **State:** foca em **estados** do objeto; aqui a variação é **algoritmo** → não é o foco.
* **Template Method:** teria “ganchos”, mas aqui trocamos **toda a lógica**; Strategy é mais direto.
* **Chain of Responsibility:** pipeline/triagem; aqui é **escolha única** de algoritmo.
* **Adapter:** útil para integrar **APIs legadas** (poderá ser usado no futuro se algum cálculo vier de fora).

### 6) Estrutura de pastas

```
src/main/java/com/escobar/risk/
├─ RiskStrategy.java          // contrato (Strategy)
├─ RiskContext.java           // Parameter Object
├─ RiskAlgorithm.java         // enum de seleção
├─ RiskStrategyFactory.java   // Factory Method + classes concretas privadas
├─ RiskEngine.java            // orquestra a Strategy atual
└─ Main.java                  // demo: troca dinâmica entre 4 algoritmos
```

### 7) Como executar (demo)

* Rodar `Main.java`. Saída organizada por blocos:

  1. **VaR (Paramétrico)**
  2. **Expected Shortfall (ES)**
  3. **Stress Testing**
  4. **Historical VaR**

### 8) Como adicionar um novo algoritmo

1. Criar **classe privada** dentro de `RiskStrategyFactory.java` que implemente `RiskStrategy`.
2. Adicionar o novo identificador em `RiskAlgorithm`.
3. Acrescentar o `case` correspondente no `switch` de `RiskStrategyFactory#create`.

> Pronto: `RiskEngine`, `RiskContext` e `RiskStrategy` **não mudam** (OCP).

---

## Versão quase resumida (direto ao ponto)

**Problema:** trocar algoritmos de risco **enquanto roda**, usando os mesmos dados, sem o cliente conhecer classes concretas.

**Escolha:**

* **Strategy** para tornar cada algoritmo uma peça **intercambiável** (`RiskStrategy`).
* **Parameter Object** (`RiskContext`) para **juntar todos os parâmetros** financeiros.
* **Factory Method** (`RiskStrategyFactory`) para o cliente pedir por **enum** (`RiskAlgorithm`) e receber a implementação **sem acoplamento**.

**Por que funciona:**

* Troca em runtime (`setStrategy`).
* Contexto complexo organizado (`RiskContext`).
* Cliente desacoplado (enum + factory).
* 4 estratégias implementadas: **VaR**, **ES**, **Stress**, **Historical VaR**.

**SOLID:**
SRP (papéis claros), OCP (novo algoritmo sem mexer no cliente), LSP/ISP (contrato simples), DIP (Engine → abstração).

**Como evoluir:**
Adicionar nova Strategy **privada** na Factory + `enum` + `case`. Só isso.



# Questão 02 — Justificativa de Design (para colar no `README.md`)

## Versão completa (explicada, linguagem simples)

### 1) O problema que precisávamos resolver

* Temos uma **interface moderna** simples:
  `ProcessadorTransacoes.autorizar(String cartao, double valor, String moeda)`
* Precisamos falar com um **sistema bancário legado** que só entende:
  `processarTransacao(HashMap<String, Object> parametros)`
* O **legado exige formatos e campos próprios** (ex.: moeda como código numérico e um **campo obrigatório** que **não existe** na interface moderna — usamos `canal` como exemplo, p.ex. `"ECOM"` ou `"POS"`).
* Além disso, a integração deve ser **bidirecional**:

  1. **Pedido moderno → formato legado**
  2. **Resposta do legado → formato moderno** (um DTO claro para o domínio atual)

### 2) Decisão: **Adapter** (com utilitário de moeda)

* **Adapter (`LegadoAdapter`)**: expõe o **contrato moderno** e **tradu z** para a **assinatura legada** (e vice-versa).

  * Entrada moderna → `HashMap` legado com **todas as chaves** esperadas.
  * Saída legado (`HashMap`) → `Autorizacao` (DTO moderno, campos claros).
* **`CurrencyCodec`**: converte **ISO ↔ código legado** conforme a restrição:
  `USD=1, EUR=2, BRL=3`.

> Resultado: o **cliente só conhece a interface moderna**, enquanto o Adapter “fala a língua do legado” sem **quebrar** a arquitetura atual.

### 3) Como o Adapter resolve cada requisito

* **Assinaturas incompatíveis** → o Adapter mapeia `autorizar(...)` para `processarTransacao(HashMap)` **sem alterar o legado**.
* **Bidirecionalidade** → converte **ida e volta** (pedido e resposta).
* **Campo obrigatório inexistente no moderno** → o Adapter **injeta** `canal` (com *default* seguro, ex.: `"ECOM"`), atendendo o legado sem poluir a interface moderna.
* **Códigos de moeda** → `CurrencyCodec.toLegacyCode("EUR") → 2` e `toISO(2) → "EUR"`.

### 4) Por que **Adapter** é a melhor escolha aqui?

* O problema é, essencialmente, de **compatibilizar interfaces**: temos um contrato novo e um contrato antigo **incompatíveis**.
* **Adapter** foi feito exatamente para isso: “fazer dois mundos conversarem” **sem mudar** nenhum dos lados.
* Alternativas e por que não usar:

  * **Strategy**: seleciona **algoritmos intercambiáveis** com a mesma interface; não resolve **mapeamento de assinatura** e nem campos/formatos diferentes.
  * **Facade**: simplifica uma **sub-sistema** complexo, mas **não traduz** assinaturas incompatíveis (poderia ser complementar, não substituto).
  * **Decorator**: adiciona comportamento sem mudar interface; não converge tipos diferentes.
  * **Adapter + Facade**: poderia ser combinado se o legado fosse enorme; aqui o Adapter sozinho atende com clareza.

### 5) SOLID aplicado (onde e como)

* **SRP (Responsabilidade Única)**:

  * `LegadoAdapter`: **só** traduz chamadas (moderno ↔ legado).
  * `CurrencyCodec`: **só** mapeia moedas.
  * `Autorizacao`: **só** representa a resposta moderna.
* **OCP (Aberto/Fechado)**:

  * Se o legado pedir novo campo, ajustamos **apenas** o Adapter (e opcionalmente criamos *helpers*).
  * O **contrato moderno não muda**.
* **LSP (Substituição de Liskov)**:

  * `LegadoAdapter` **implementa** `ProcessadorTransacoes`; qualquer cliente que usa a interface moderna pode usá-lo **sem surpresas**.
* **ISP (Segregação de Interfaces)**:

  * Mantemos a **interface moderna enxuta** (`autorizar(...)`) em vez de espalhar chaves/`HashMap`.
* **DIP (Inversão de Dependência)**:

  * O cliente depende de **abstração** (`ProcessadorTransacoes`), não da lib legada.

### 6) Benefícios práticos

* **Protege o código moderno** dos detalhes feios/obsoletos do legado.
* **Facilita testes**: podemos simular o legado com um *stub* e validar o mapeamento.
* **Evolutivo**: novos campos do legado entram **só no Adapter**.
* **Clareza de domínio**: o DTO `Autorizacao` é legível (mensagem, aprovado, código, moeda ISO).

### 7) Riscos e mitigação

* **Mudanças do legado** (novas chaves, novos códigos): mitigado centralizando conversões no Adapter/Codec.
* **Moedas não suportadas**: validação explícita com mensagens claras (lança exceção se ISO inválido).

---

## Versão resumida (direta, mas não telegráfica)

**Objetivo:** integrar a interface moderna `autorizar(cartao, valor, moeda)` com o legado `processarTransacao(HashMap)`, **indo e voltando**, incluindo campo obrigatório do legado e **códigos de moeda** (USD=1, EUR=2, BRL=3).

**Escolha:** **Adapter (`LegadoAdapter`)**

* Converte **requisições** modernas para o **formato legado** (inclui o campo obrigatório `canal`).
* Converte **respostas** do legado (`HashMap`) para o **DTO moderno** `Autorizacao`.
* Usa **`CurrencyCodec`** para mapear **ISO ↔ código** (ex.: **EUR ↔ 2**).

**Por que Adapter:**

* O problema é **compatibilizar interfaces** diferentes **sem alterar** o legado.
* Strategy/Decorator/Facade não resolvem a **tradução de assinatura e tipos**; Adapter foi criado para isso.

**SOLID (na prática):**

* **SRP:** Adapter só traduz; Codec só converte moeda; DTO só representa resposta.
* **OCP:** mudanças do legado isoladas no Adapter/Codec; contrato moderno permanece.
* **DIP:** clientes dependem de `ProcessadorTransacoes` (abstração).
* **LSP/ISP:** Adapter cumpre o contrato moderno e mantém interface simples.

**Benefício:**

* **Cliente continua falando “moderno”**, o Adapter fala “legado” por ele — e devolve uma resposta moderna clara, pronta para uso.