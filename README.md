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
