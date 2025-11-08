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




Decisão e justificativa (para o README, linguagem simples)
Versão completa

Padrão principal: State. Cada estado da usina (DESLIGADA, OPERACAO_NORMAL, ALERTA_AMARELO, ALERTA_VERMELHO, EMERGENCIA, MANUTENCAO) é uma classe de estado que decide quando e para onde transicionar a partir das leituras (temperatura, pressão, radiação, falha no resfriamento, segundos > 400°C).

Por que State aqui? O comportamento muda por estado (regras diferentes em cada fase) e as transições precisam ser controladas/no lugar certo, sem if/else gigantes. Cada estado encapsula suas regras, e o Context (UsinaNuclear) coordena e aplica políticas de segurança globais (ex.: “Emergência só depois de Vermelho”, proibir “Emergência → Normal” direto).

Regras implementadas:

OPERACAO_NORMAL → AMARELO: temperatura > 300°C.

AMARELO → VERMELHO: temperatura > 400°C por mais de 30s.

VERMELHO → EMERGENCIA: somente se falha do resfriamento.

Prevenir ciclos perigosos: p.ex., não permite EMERGENCIA → NORMAL direto. Recuo é controlado (Emergência → Vermelho → Amarelo → Normal).

EMERGENCIA só após VERMELHO: reforçado no Context (trava passouPorVermelho).

Modo MANUTENCAO: sobrepõe a lógica normal e congela o estado até o operador desligar a manutenção (sem transições inadvertidas).

Parameter Object (Medidas): junta todos os sinais (SRP e legibilidade).

Factory Method (EstadoFactory): cria estados por EstadoId, escondendo as classes concretas (estilo “private class”/final).

SOLID:

SRP: cada classe com um papel único (estado decide; context orquestra; Medidas carrega dados; Factory cria).

OCP: adicionar estado/regra nova sem quebrar cliente (só estende).

LSP/ISP: contrato EstadoUsina simples e universal (avaliar(...)).

DIP: UsinaNuclear trabalha com abstração EstadoUsina, não com classes concretas.

Versão resumida

Escolha: State porque o comportamento varia por estado e precisamos validar transições com regras específicas.

Regras-chave codificadas:

Normal → Amarelo (temp > 300°C)

Amarelo → Vermelho (temp > 400°C por >30s)

Vermelho → Emergência (falha no resfriamento)

Emergência só após Vermelho; sem “Emergência → Normal” direto.

Modo Manutenção: congela o sistema (sobrepõe lógica normal) até o operador desligar.

Design limpo: Medidas (Parameter Object), EstadoFactory (concretas privadas), UsinaNuclear (Context) — SRP/OCP/DIP respeitados.




# Questão 04 — Justificativa da escolha do padrão (para o `README.md`)

## Versão completa (explicada em linguagem simples)

### O problema, em uma frase

Precisamos aplicar **várias validações diferentes** sobre a mesma NF-e, **em sequência**, com **condições** (pular alguns passos quando outro falha), **parar cedo** quando houver muitas falhas (circuit breaker), **desfazer mudanças** se algo adiante der errado (rollback) e **respeitar timeouts** individuais — tudo isso **sem acoplar** o código da aplicação aos detalhes de cada validador.

### Padrão escolhido: **Chain of Responsibility (CoR)**

CoR modela um **encadeamento de validadores**, onde **cada nó** decide se:

* processa a requisição (a NF-e),
* **passa adiante** para o próximo,
* **interrompe** a cadeia (no nosso caso, por política de **circuit breaker** ou por falha/timeout).

Isso permite **adicionar, remover ou reordenar** validadores **sem mexer** no restante do sistema. Cada validador fica **isolado**, com sua própria responsabilidade (schema, certificado, regras fiscais, banco, SEFAZ), e o orquestrador aplica as **políticas transversais** (condicional, breaker, rollback, timeout).

### Por que CoR atende exatamente aos requisitos

* **“Cada validador especializado verifica um aspecto”** → cada handler na cadeia é um validador de responsabilidade única.
* **“Validações condicionais (se X falhar, pule Y)”** → o orquestrador conhece o histórico; se houve falha anterior, **não chama** validadores que exigem cadeia limpa (como Regras Fiscais e SEFAZ).
* **“Circuit breaker após 3 falhas”** → o contexto da cadeia acumula falhas; ao atingir 3, **interrompe** a execução.
* **“Rollback para validadores que modificam o documento”** → sempre que um validador efetua uma mudança (ex.: gravação em BD), ele **registra** uma ação de rollback no **contexto**; se algum posterior falhar, a engine aciona o **rollback** de tudo que foi registrado.
* **“Timeout individual por validador”** → cada handler roda envolto em um **executor com timeout** próprio; se estourar, retorna **TIMEOUT** e conta como falha (influenciando o breaker e as condicionais).
* **“Validadores 3 e 5 só executam se os anteriores passarem”** → o CoR/engine checa o estado (nenhuma falha anterior) antes de invocá-los.

### Por que **não** escolhemos outros padrões

* **Strategy**: troca de algoritmo com a **mesma** interface; aqui precisamos **encadear** passos e **pular/ interromper** — não é só escolher “um algoritmo”.
* **State**: muda comportamento por **estado interno** do objeto; nosso foco é **pipeline de validações** com políticas de fluxo, não a máquina de estados do domínio.
* **Template Method**: definiria um esqueleto fixo com ganchos; **engessa a ordem** e dificulta **pular/ interromper** dinamicamente (especialmente com breaker e timeouts).
* **Observer**: notificação assíncrona; não precisamos “avisar interessados”, e sim **controlar a sequência e o resultado** de cada etapa.
* **Facade**: simplifica acesso a subsistemas, mas **não resolve controle fino** de fluxo (pular, parar, rollback, timeout) etapa a etapa.
* **Pipeline puro** sem CoR: possível, mas você reimplementaria, na prática, o que o CoR já oferece (handlers encadeados com repasse/curto-circuito).

### SOLID aplicado

* **SRP**: cada validador faz **uma** checagem; a engine **orquestra**; o contexto guarda **estado transversal** (falhas, rollback, timeouts).
* **OCP**: novo validador entra **sem mudar** os existentes — só encadeia outro nó.
* **LSP/ISP**: contrato de validador **mínimo e estável** (id/validate/flag condicional).
* **DIP**: a engine depende da **abstração** `Validator`, não das classes concretas.

### Benefícios práticos

* **Extensível**: fica fácil incluir um 6º validador (ex.: antifraude) sem tocar no resto.
* **Observabilidade**: cada nó reporta seu próprio **PASS/FAIL/TIMEOUT** — logs claros.
* **Robustez**: **parada antecipada** evita custo desnecessário; **rollback** garante consistência mesmo quando há falhas no fim da cadeia.

---

## Versão resumida (direta, para leitura rápida)

**Escolha:** **Chain of Responsibility (CoR)** porque precisamos executar **múltiplas validações em sequência**, **pular** etapas quando houver falhas anteriores, **interromper** após **3 falhas** (circuit breaker), **desfazer mudanças** (rollback) e aplicar **timeout por etapa** — tudo **desacoplado** e **fácil de estender**.

**Por que funciona aqui:**

* Cada validador é um **handler** especializado (schema, certificado, fiscal, BD, SEFAZ).
* Condicionais: os validadores 3 e 5 **só rodam** se **ninguém antes falhou**.
* Circuit breaker: ao atingir 3 falhas, a cadeia **para**.
* Rollback: mudanças do BD são **desfeitas** se algo depois falhar.
* Timeout: cada validador tem **tempo máximo** próprio.

**Por que não Strategy/State/Template/Observer:** eles não resolvem bem o **encadeamento com controle de fluxo** (pular/ interromper/ fazer rollback/ timeout). O CoR foi feito exatamente para **processar pedidos passando por uma cadeia de handlers** com **curto-circuito** quando necessário.

**SOLID:** SRP (responsabilidades separadas), OCP (validador novo entra fácil), DIP (engine → interface `Validator`).