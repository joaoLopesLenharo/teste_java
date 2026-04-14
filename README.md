# Teste Prático de Programação - Java + Maven + MVC

## 📋 Descrição

Projeto Java com Maven desenvolvido como teste prático de programação. O sistema gerencia funcionários de uma indústria, com persistência em SQLite, arquitetura MVC e interface gráfica completa.

## 🛠 Tecnologias

- **Java 17**
- **Maven** - Gerenciamento de dependências e build
- **SQLite** - Banco de dados para persistência
- **Swing + FlatLaf** - Interface gráfica moderna com tema dark

## 🏗 Arquitetura MVC

```
┌─────────────┐      ┌──────────────────────┐      ┌───────────────┐
│    VIEW      │ ───▶ │    CONTROLLER         │ ───▶ │    MODEL      │
│  MainFrame   │      │ FuncionarioController │      │ Pessoa        │
│  (Swing GUI) │ ◀─── │  (Lógica de Negócio)  │ ◀─── │ Funcionario   │
└─────────────┘      └──────────────────────┘      └───────────────┘
                              │                              ▲
                              ▼                              │
                     ┌──────────────────┐           ┌────────────────┐
                     │      DAO         │ ────────▶ │    DATABASE    │
                     │ FuncionarioDAO   │           │    SQLite      │
                     └──────────────────┘           └────────────────┘
```

- **Model** (`com.teste.model`): Classes de domínio — `Pessoa` e `Funcionario`
- **View** (`com.teste.view`): Interface gráfica — `MainFrame` (Swing + FlatLaf)
- **Controller** (`com.teste.controller`): Lógica de negócio — `FuncionarioController`
- **DAO** (`com.teste.dao`): Acesso a dados — `FuncionarioDAO` (SQLite)
- **Util** (`com.teste.util`): Utilitários — `DatabaseUtil`, `FormatUtil`

## 📦 Estrutura do Projeto

```
teste_pratico_java/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── teste/
│                   ├── Principal.java              # Ponto de entrada
│                   ├── model/
│                   │   ├── Pessoa.java              # Classe base (Model)
│                   │   └── Funcionario.java         # Estende Pessoa (Model)
│                   ├── controller/
│                   │   └── FuncionarioController.java # Lógica de negócio
│                   ├── view/
│                   │   └── MainFrame.java           # Interface gráfica (Swing)
│                   ├── dao/
│                   │   └── FuncionarioDAO.java      # CRUD com SQLite
│                   └── util/
│                       ├── DatabaseUtil.java        # Conexão com banco
│                       └── FormatUtil.java          # Formatação BR
```

## ✅ Requisitos Implementados

| # | Requisito | Status |
|---|-----------|--------|
| 1 | Classe Pessoa (nome, data nascimento) | ✅ |
| 2 | Classe Funcionário (salário, função) | ✅ |
| 3.1 | Inserir todos os funcionários | ✅ |
| 3.2 | Remover funcionário "João" | ✅ |
| 3.3 | Imprimir com formatação brasileira | ✅ |
| 3.4 | Aumento de 10% no salário | ✅ |
| 3.5 | Agrupar por função em MAP | ✅ |
| 3.6 | Imprimir agrupados por função | ✅ |
| 3.8 | Aniversariantes meses 10 e 12 | ✅ |
| 3.9 | Funcionário mais velho | ✅ |
| 3.10 | Ordem alfabética | ✅ |
| 3.11 | Total dos salários | ✅ |
| 3.12 | Salários mínimos por funcionário | ✅ |
| 4 | Persistência com SQLite | ✅ |
| 5 | Tela completa com CRUD + auto-cadastro | ✅ |

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+

### Compilar e Executar

```bash
# Compilar o projeto
mvn clean compile

# Executar via Maven
mvn exec:java

# Ou empacotar e executar o JAR
mvn clean package
java -jar target/teste-pratico-java-1.0-SNAPSHOT.jar
```

## 🖥 Interface Gráfica

A aplicação abre uma interface gráfica completa com tema dark moderno:

- **Tabela** de funcionários com visualização completa
- **Formulário CRUD** para adicionar, editar e remover funcionários
- **Auto-cadastro** com um clique para inserir os funcionários da tabela padrão
- **Botões de operações** para executar todos os requisitos 3.x
- **Painel de resultado** estilo console para visualizar a saída das operações
- **Limpar banco** para resetar todos os dados

## 📌 Observações

- As datas são exibidas no formato **dd/mm/aaaa**
- Valores monetários utilizam **separador de milhar (.)** e **decimal (,)**
- O salário mínimo considerado é **R$ 1.212,00**
- O banco SQLite (`funcionarios.db`) é criado automaticamente na raiz do projeto
- A classe `Principal` executa todos os requisitos 3.x no console e depois abre a GUI
- Toda lógica de negócio está no `Controller`, a `View` apenas exibe e delega
