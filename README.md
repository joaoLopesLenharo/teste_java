# Módulo RH (ERP) — Java + Maven

Mini módulo de recursos humanos sobre o teste prático original: mesmos requisitos (3.1–3.12), com **Service**, **DTO**, **repositório**, **SQLite**, interface **Swing (FlatLaf)**, **menu no terminal**, **dashboard**, **filtros**, **exportação CSV/PDF** e **testes JUnit 5**.

## Requisitos

- **JDK 17** (não use apenas JRE — o Maven precisa do compilador)
- **Maven 3.6+**

## Passo a passo: colocar o projeto no seu computador e compilar

Siga a ordem abaixo na máquina onde vai desenvolver ou só executar o programa.

### 1. Obter o código do projeto

- **Opção A — pasta ZIP:** descompacte o arquivo num diretório à sua escolha (ex.: `C:\Projetos\teste_pratico_java` ou `~/projetos/teste_pratico_java`).
- **Opção B — Git:** `git clone <url-do-repositório>` e entre na pasta criada.

Confirme que vê ficheiros como `pom.xml` e a pasta `src` na raiz do projeto.

### 2. Instalar o JDK 17 (ou superior na série 17)

1. Descarregue um instalador oficial, por exemplo [Eclipse Temurin 17 (Adoptium)](https://adoptium.net/) ou o instalador da Oracle para Java 17.
2. Execute o instalador e conclua a instalação com as opções predefinidas.
3. Abra um **novo** terminal (PowerShell, CMD ou terminal do Linux/macOS) e confira:

   ```bash
   java -version
   javac -version
   ```

   Deve aparecer algo como `17.x.x` nos dois. O segundo comando (`javac`) confirma que instalou um **JDK** e não só um JRE. Se `javac` não existir, o Maven não compila — veja mais abaixo a subsecção **«No compiler is provided» (Windows)**.

*(Recomendado no Windows)* Defina a variável de ambiente `JAVA_HOME` para a pasta raiz do JDK (ex.: `C:\Program Files\Eclipse Adoptium\jdk-17...`) e inclua `%JAVA_HOME%\bin` no **PATH**.

### 3. Instalar o Apache Maven

1. Descarregue o Maven em [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) (ficheiro `apache-maven-*-bin.zip` ou equivalente).
2. Descompacte para uma pasta fixa (ex.: `C:\tools\apache-maven-3.9.x`).
3. Adicione ao **PATH** do sistema a subpasta `bin` dessa instalação (ex.: `C:\tools\apache-maven-3.9.x\bin`).
4. Feche e volte a abrir o terminal e confira:

   ```bash
   mvn -version
   ```

   Deve mostrar a versão do Maven e a versão do Java usada.

**Alternativa (Windows):** com [winget](https://learn.microsoft.com/pt-pt/windows/package-manager/winget/) — `winget install Apache.Maven` — ou instalar Maven a partir do instalador da sua IDE (IntelliJ IDEA, Eclipse) e usar o Maven embutido no terminal integrado.

### 4. Compilar o projeto

1. No terminal, vá até à **raiz do projeto** (onde está o `pom.xml`):

   ```bash
   cd caminho\para\teste_pratico_java
   ```

2. Compile e execute testes (recomendado):

   ```bash
   mvn clean verify
   ```

   Só compilar sem testes:

   ```bash
   mvn clean compile
   ```

   Se tudo correr bem, a pasta `target/` é criada com as classes compiladas.

### 5. Gerar o JAR executável (para distribuir ou correr sem IDE)

Na raiz do projeto:

```bash
mvn clean package
```

O ficheiro útil é o JAR **com dependências** gerado pelo plugin Shade (nome típico: `target/teste-pratico-java-1.0-SNAPSHOT.jar`). Pode copiar **só este JAR** para outro PC que tenha **Java 17+** instalado e executar:

```bash
java -jar teste-pratico-java-1.0-SNAPSHOT.jar
java -jar teste-pratico-java-1.0-SNAPSHOT.jar --console
```

*(Não precisa de Maven na máquina de destino — apenas o `java` no PATH.)*

### 6. Problemas frequentes

| Sintoma | O que verificar |
|--------|------------------|
| `mvn` não é reconhecido | PATH do Maven ou terminal antigo; reabra o terminal após alterar variáveis. |
| Erro de compilação / “invalid target release” | JDK errado; instale JDK 17+ e confira `java -version` e `mvn -version`. |
| `No compiler is provided` / “Perhaps you are running on a JRE rather than a JDK?” | Veja a secção **abaixo** — é o caso mais comum no Windows. |

#### Erro: `No compiler is provided in this environment` (Windows)

O Maven precisa do **JDK** (inclui `javac`). Se só existir **JRE** no PATH, a compilação falha.

1. **Confirme se o compilador existe** (PowerShell):

   ```powershell
   javac -version
   ```

   - Se aparecer **“javac não é reconhecido…”**, o JDK não está no PATH (ou não está instalado).

2. **Instale um JDK 17 completo** (não basta o “Java Runtime” isolado), por exemplo [Temurin 17](https://adoptium.net/temurin/releases/?version=17). No instalador, mantenha a opção que **define JAVA_HOME** / associações, se existir.

3. **Defina `JAVA_HOME` para a raiz do JDK** (a pasta que contém `bin\javac.exe`), por exemplo:

   ```text
   C:\Program Files\Eclipse Adoptium\jdk-17.0.xx.x-hotspot
   ```

   - Não use a subpasta `jre` dentro do JDK como `JAVA_HOME`.
   - No Windows: *Definições → Sistema → Acerca → Configurações avançadas do sistema → Variáveis de ambiente* → variável de sistema **JAVA_HOME** = caminho acima.

4. **Ajuste o PATH** para o JDK vir **antes** de qualquer outro Java:

   - Adicione (ou mova para o topo): `%JAVA_HOME%\bin`
   - Se existir outra entrada apontando para um JRE antigo, remova-a ou coloque-a depois.

5. **Feche e reabra** o PowerShell e teste de novo:

   ```powershell
   java -version
   javac -version
   mvn -version
   ```

   Em `mvn -version`, a linha **Java version** deve corresponder ao JDK 17.

6. Volte à pasta do projeto e execute:

   ```powershell
   mvn clean verify
   ```

**Dica:** se `where.exe java` listar vários caminhos, o primeiro é o usado — deve ser `...\jdk-17...\bin\java.exe`, não apenas um `java.exe` de runtime antigo.

**Se `javac -version` já funciona mas `mvn clean verify` ainda falha com “No compiler is provided”:** o processo do Maven pode estar a arrancar com outro Java (por exemplo `JAVA_HOME` apontando para um JRE). Faça uma destas opções:

1. **Script incluído no projeto** (define `JAVA_HOME` a partir do `javac` do PATH e chama o Maven):

   ```powershell
   cd C:\caminho\para\teste_pratico_java
   .\scripts\mvn-com-jdk-do-javac.ps1 clean verify
   ```

   Se o PowerShell bloquear scripts: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` (uma vez por utilizador).

2. **Definir `JAVA_HOME` manualmente** para a pasta raiz do JDK onde está o `javac` que pretende (pai da pasta `bin`), depois `mvn clean verify` num terminal novo.

O `pom.xml` está configurado com `maven-compiler-plugin` em modo **`fork`** para preferir o compilador externo (`javac`), o que costuma resolver conflitos entre várias instalações no Windows.

## Tecnologias

| Camada | Tecnologia |
|--------|------------|
| Linguagem / build | Java 17, Maven |
| Persistência | SQLite (`sqlite-jdbc`) |
| UI | Swing + FlatLaf |
| Relatórios PDF | Apache PDFBox 3 |
| Testes | JUnit 5 |

## Arquitetura

```
View (MainFrame, ConsoleMenu)
        │
        ▼
FuncionarioController  ──►  FuncionarioService  ──►  FuncionarioRepository
        │                            │                      (FuncionarioDAO)
        │                            ▼
        │                     DTO (apresentação)
        │                     FuncionarioDTO, DashboardDTO
        ▼
   Model: Pessoa, Funcionario
```

- **Model** (`com.teste.model`): `Pessoa`, `Funcionario`
- **Repository** (`com.teste.repository`): interface de persistência
- **DAO** (`com.teste.dao`): `FuncionarioDAO` (JDBC/SQLite)
- **Service** (`com.teste.service`): regras de negócio e `toDTO` / indicadores / filtros
- **DTO** (`com.teste.dto`): dados já formatados para tela e console
- **Controller** (`com.teste.controller`): fachada usada pela GUI e pelo terminal
- **View** (`com.teste.view`): `MainFrame` (gráfico), `ConsoleMenu` (texto)
- **Util** (`com.teste.util`): `DatabaseUtil`, `FormatUtil`, `RelatorioExportUtil`

Formatação de data, moeda, idade e salários mínimos no **Service** (`toDTO`), não na view.

## Estrutura de pastas

```
src/main/java/com/teste/
├── Principal.java
├── controller/FuncionarioController.java
├── service/FuncionarioService.java
├── repository/FuncionarioRepository.java
├── dao/FuncionarioDAO.java
├── dto/FuncionarioDTO.java, DashboardDTO.java
├── model/Pessoa.java, Funcionario.java
├── view/MainFrame.java, ConsoleMenu.java
└── util/DatabaseUtil.java, FormatUtil.java, RelatorioExportUtil.java

src/test/java/com/teste/
├── PrincipalTest.java
├── service/FuncionarioServiceTest.java
├── dao/FuncionarioDAOIntegrationTest.java
└── support/DatabaseTestSupport.java
```

## Como executar

```bash
# Compilar
mvn -q compile

# Interface gráfica (padrão)
mvn exec:java

# Apenas menu no terminal (sem GUI)
mvn exec:java -Dexec.args="--console"

# Apenas GUI (explícito)
mvn exec:java -Dexec.args="--gui"
```

JAR sombreado (dependências incluídas):

```bash
mvn -q package
java -jar target/teste-pratico-java-1.0-SNAPSHOT.jar
java -jar target/teste-pratico-java-1.0-SNAPSHOT.jar --console
```

## Testes

```bash
mvn test
```

Os testes de integração usam SQLite em **arquivo temporário** (não o `funcionarios.db` de desenvolvimento).

## Requisitos do teste original (mantidos)

| Item | Descrição |
|------|-----------|
| 3.1 | Inserir funcionários (tabela padrão) |
| 3.2 | Remover "João" |
| 3.3 | Listar todos formatados |
| 3.4 | Aumento de 10% |
| 3.5–3.6 | Agrupar por função e listar |
| 3.8 | Aniversariantes meses 10 e 12 |
| 3.9 | Funcionário mais velho |
| 3.10 | Ordem alfabética |
| 3.11 | Total dos salários |
| 3.12 | Salários mínimos (base R$ 1.212,00) |
| 4 | SQLite |
| 5 | CRUD + operações na tela |

No **terminal**, o menu oferece as opções 1–13 (e extras 14–17: busca, filtros, demonstração completa). Na **GUI**, há botões equivalentes, dashboard, filtros e exportação com escolha de arquivo.

## Banco de dados

O ficheiro `funcionarios.db` é criado na diretoria de trabalho ao correr a aplicação. Está listado no `.gitignore` (`*.db`); não deve ser versionado.

## Licença / contexto

Projeto educativo derivado de um teste prático de programação em Java.
