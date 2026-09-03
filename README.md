# Campo Minado em Java 

Projeto acadêmico de Campo Minado em Java, seguindo a
especificação da disciplina de Programação Orientada a Objetos.

## Estrutura

```
src/main/java/campominado/
├── modelo/
│   ├── Celula.java       - atributos privados minada, revelada, marcada, minasVizinhas
│   └── Tabuleiro.java    - Celula[][] grade, posicionamento de minas, cascata (ArrayList)
├── jogo/
│   └── JogoCampoMinado.java  - laço principal no console (só usa Tabuleiro)
└── ui/
    ├── Principal.java           - main() da versão console
    └── JogoCampoMinadoGUI.java  - versão com interface gráfica (Swing), também só usa Tabuleiro

src/test/java/campominado/modelo/
├── CelulaTest.java     - 6 testes (encapsulamento)
└── TabuleiroTest.java  - 8 testes (minas, cascata, vitória/derrota)
```

Veja `RELATORIO.md` para a explicação da lógica da cascata e das decisões
de encapsulamento.

## Como compilar e rodar

### Com Maven (recomendado — já roda os testes também)
```bash
mvn test
mvn compile exec:java -Dexec.mainClass=campominado.ui.Principal
```
(Se não tiver o plugin `exec`, basta usar o comando "sem Maven" abaixo
depois de `mvn compile`.)

### Sem Maven (javac puro)
Compilar e rodar o jogo (sem os testes, que exigem JUnit no classpath):
```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out campominado.ui.Principal
```

### Rodando os testes sem Maven
Baixe o jar "consolidado" do JUnit 5
(`junit-platform-console-standalone`, disponível no Maven Central) e rode:
```bash
javac -cp junit-platform-console-standalone.jar -d out $(find src -name "*.java")
java -jar junit-platform-console-standalone.jar -cp out --scan-classpath
```

## Como rodar a versão gráfica (Swing)

**Com Maven:**
```bash
mvn compile exec:java -Dexec.mainClass=campominado.ui.JogoCampoMinadoGUI
```

**Sem Maven (javac puro):**
```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out campominado.ui.JogoCampoMinadoGUI
```

No Windows (PowerShell), troque o `$(find ...)` por:
```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src/main).FullName
java -cp out campominado.ui.JogoCampoMinadoGUI
```

Abre uma janela com o tabuleiro em botões: **clique esquerdo** revela a
célula, **clique direito** marca/desmarca uma bandeira. O menu **Jogo >
Novo jogo...** deixa escolher linhas, colunas e minas a qualquer momento.

## Como jogar (versão console)

Ao rodar, o jogo pede o número de linhas, colunas e minas. Depois, a cada
rodada:

- `r <linha> <coluna>` — revela a célula (dispara a cascata se for vazia)
- `m <linha> <coluna>` — marca/desmarca uma bandeira na célula
- `s` — sai do jogo

Legenda no tabuleiro: `.` célula oculta, `F` marcada com bandeira, número
= quantidade de minas vizinhas, célula em branco = área vazia revelada,
`*` = mina (só aparece ao final da partida).
