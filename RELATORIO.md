# Relatório — Campo Minado

**Disciplina:** Programação Orientada a Objetos (Java)
**Projeto:** Campo Minado (Minesweeper)

## 1. Estrutura do projeto

```
campominado/
├── modelo/
│   ├── Celula.java
│   └── Tabuleiro.java
├── jogo/
│   └── JogoCampoMinado.java
└── ui/
    ├── Principal.java           (main, versão console)
    └── JogoCampoMinadoGUI.java  (versão com interface gráfica, Swing)
```

- **`Celula`** representa uma posição do tabuleiro e todo o seu estado.
- **`Tabuleiro`** possui o array bidimensional `Celula[][] grade`, monta o
  tabuleiro, sorteia as minas e implementa as regras do jogo.
- **`JogoCampoMinado`** é o laço principal em modo console. Ele só chama
  métodos de `Tabuleiro` — nunca importa ou manipula `Celula` diretamente.
- **`Principal`** é o ponto de entrada da versão console (`main`).
- **`JogoCampoMinadoGUI`** é a versão com interface gráfica (Swing,
  diferencial opcional citado no enunciado). Segue a mesma regra de
  arquitetura: só chama métodos de `Tabuleiro`, nunca acessa `Celula`
  diretamente. Clique esquerdo revela, clique direito marca bandeira, e o
  menu "Jogo > Novo jogo..." permite reconfigurar linhas/colunas/minas.

## 2. Lógica do efeito cascata

O efeito cascata está implementado em `Tabuleiro.revelar(int linha, int coluna)`.

**Regra do jogo:** ao revelar uma célula sem nenhuma mina nas 8 vizinhas
(`minasVizinhas == 0`), o jogo deve revelar automaticamente as vizinhas, e
repetir o processo em cadeia para cada vizinha que também for "vazia",
parando nas células numeradas (que servem de borda) ou nas bordas do
tabuleiro.

**Implementação (iterativa, com `ArrayList` como fila de pendentes):**

1. A célula clicada é revelada. Se ela **não** for vazia (é mina, ou tem
   algum número > 0), o método termina ali — sem cascata.
2. Se ela for vazia, ela entra em uma `ArrayList<Celula> pendentes`, que
   funciona como fila de trabalho.
3. Um laço percorre `pendentes` com um índice (sem usar recursão): para
   cada célula pendente, olha as até 8 vizinhas dentro dos limites do
   tabuleiro. Cada vizinha ainda não revelada e não marcada é revelada;
   se essa vizinha **também** for vazia, ela é adicionada ao final da
   lista `pendentes`, para que suas próprias vizinhas sejam processadas
   depois.
4. O laço termina quando não há mais nada em `pendentes` para processar
   (ou seja, quando a "mancha" de células vazias encontrou, em todo o seu
   contorno, apenas células numeradas ou os limites do tabuleiro).

Optamos pela versão **iterativa com `ArrayList`** em vez de recursão pura
por dois motivos: (a) o próprio enunciado sugere `ArrayList` como
estrutura para as "células pendentes de revelação"; (b) evita risco de
`StackOverflowError` em tabuleiros grandes com áreas vazias muito extensas,
o que aconteceria com uma recursão profunda.

**Por que a cascata nunca revela uma mina "por engano":** a condição que
permite que uma célula entre na fila `pendentes` (e portanto continue
propagando a cascata) é `isVazia()`, definida em `Celula` como
`!minada && minasVizinhas == 0`. Uma célula minada tem `minada == true`,
então `isVazia()` é sempre `false` para ela — ela nunca é adicionada à
fila. Ela só pode ser revelada se for **diretamente** clicada pelo
jogador (o que, no jogo, significa perder), nunca como efeito colateral
da cascata. Isso também garante que a cascata para exatamente na "borda"
de células numeradas ao redor de uma região de minas, que é o
comportamento esperado do Campo Minado clássico.

## 3. Decisões de encapsulamento

### Na classe `Celula`

Os quatro atributos de estado (`minada`, `revelada`, `marcada`,
`minasVizinhas`) são todos `private`. Em vez de expor `setters` genéricos
para cada um, cada mudança de estado passa por um método que já aplica a
regra de negócio correspondente:

- `marcarComoMinada()` — sem parâmetro; só é usado por `Tabuleiro`
  durante a montagem do tabuleiro. Não existe forma de "desminar" uma
  célula durante a partida, porque essa operação nunca faz sentido.
- `revelar()` — retorna `false` (e não faz nada) se a célula já estiver
  revelada ou estiver marcada com bandeira. Assim, é impossível revelar
  uma célula marcada sem primeiro desmarcá-la, sem que quem chama o
  método precise lembrar de checar isso manualmente antes.
- `alternarMarcacao()` — não permite marcar uma célula já revelada.
- `setMinasVizinhas(int)` — valida que o valor está entre 0 e 8,
  lançando `IllegalArgumentException` fora desse intervalo.

O objetivo é que seja **impossível**, usando só a API pública de `Celula`,
colocar o objeto em um estado inconsistente (por exemplo, revelada e
marcada ao mesmo tempo).

### Entre `Tabuleiro` e o restante da aplicação

`Tabuleiro` nunca devolve um objeto `Celula` para quem o chama (o método
`getCelula` existe, mas é *package-private*, usado somente pelos testes
unitários, que estão no mesmo pacote `campominado.modelo`). Para o resto
da aplicação, `Tabuleiro` expõe apenas métodos de consulta pontuais:
`isRevelada(l, c)`, `isMarcada(l, c)`, `isMinada(l, c)` e
`getMinasVizinhas(l, c)`.

Isso faz com que `JogoCampoMinado` (camada de jogo/interface) só precise
conhecer `Tabuleiro`, e nunca `Celula` — reforçando a separação de
responsabilidades sugerida no enunciado: `Celula` guarda e protege seu
próprio estado; `Tabuleiro` conhece a grade inteira; `JogoCampoMinado`
só conversa com `Tabuleiro`.

## 4. Uso de array bidimensional e `ArrayList`

- **Array bidimensional (`Celula[][] grade`)**: usado para a grade fixa do
  tabuleiro, já que as dimensões não mudam durante a partida e o acesso é
  sempre por coordenada `(linha, coluna)`.
- **`ArrayList`**: usado em dois pontos —
  1. `posicionarMinas()`, como lista de posições disponíveis para sorteio
     sem repetição (`List<int[]> posicoesDisponiveis`);
  2. `revelar(...)`, como a fila de células pendentes de revelação durante
     o efeito cascata (`List<Celula> pendentes`), citada na seção 2.

## 5. Testes unitários

Os testes (JUnit 5) estão em `src/test/java/campominado/modelo/`:

- **`CelulaTest`** — 6 casos, cobrindo o encapsulamento: estado inicial,
  `marcarComoMinada`, impossibilidade de revelar célula marcada,
  impossibilidade de marcar célula revelada, regra de `isVazia()`, e
  validação de `setMinasVizinhas`.
- **`TabuleiroTest`** — 8 casos, cobrindo: dimensões e quantidade de minas
  do construtor, validação de parâmetros inválidos, contagem correta de
  minas vizinhas, cascata revelando o tabuleiro inteiro (0 minas), cascata
  **não** revelando outras minas ao clicar em uma mina, bloqueio de
  revelação em célula marcada, condição de vitória, e revelar fora dos
  limites do tabuleiro sem erro.

Como o posicionamento das minas é aleatório (`java.util.Random`), os
testes foram desenhados para não depender de qual posição específica será
minada: usam tabuleiros com 0 minas, com "todas menos uma" célula minada,
ou descobrem a posição das minas consultando o próprio `Tabuleiro` antes
de agir — nunca assumem uma posição fixa.

## 6. Como compilar e rodar

**Com Maven:**
```
mvn test              # roda os testes unitários
mvn compile exec:java -Dexec.mainClass=campominado.ui.Principal
```

**Sem Maven (javac puro, sem testes):**
```
javac -d out $(find src/main/java -name "*.java")
java -cp out campominado.ui.Principal
```
