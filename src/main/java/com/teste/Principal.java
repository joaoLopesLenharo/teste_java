package com.teste;

import com.teste.controller.FuncionarioController;
import com.teste.model.Funcionario;
import com.teste.util.DatabaseUtil;
import com.teste.util.FormatUtil;
import com.teste.view.MainFrame;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Classe Principal — ponto de entrada da aplicação.
 *
 * Modos de execução:
 * - Sem argumentos: executa todos os requisitos no console + abre a GUI
 * - --console: executa apenas no console (sem GUI)
 * - --gui: abre apenas a interface gráfica (sem saída console)
 *
 * Arquitetura MVC:
 * - Model: Pessoa, Funcionario (dados)
 * - View: MainFrame (interface gráfica)
 * - Controller: FuncionarioController (lógica de negócio)
 * - DAO: FuncionarioDAO (acesso a dados - SQLite)
 */
public class Principal {

    public static void main(String[] args) {
        // ── Determinar modo de execução ──────────────────────
        String modo = "ambos"; // padrão: console + GUI
        if (args.length > 0) {
            if ("--console".equalsIgnoreCase(args[0])) {
                modo = "console";
            } else if ("--gui".equalsIgnoreCase(args[0])) {
                modo = "gui";
            }
        }

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   TESTE PRÁTICO DE PROGRAMAÇÃO — JAVA + MAVEN + MVC  ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        // ── Inicializar banco de dados (Requisito 4) ──────────
        DatabaseUtil.inicializarBanco();

        // ── Criar o Controller (MVC) ──────────────────────────
        FuncionarioController controller = new FuncionarioController();

        // ── Modo CONSOLE ou AMBOS: executar requisitos no console ──
        if ("console".equals(modo) || "ambos".equals(modo)) {
            executarRequisitos(controller);
        }

        // ── Modo GUI ou AMBOS: abrir interface gráfica ────────
        if ("gui".equals(modo) || "ambos".equals(modo)) {
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println(" ABRINDO INTERFACE GRÁFICA...");
            System.out.println("═══════════════════════════════════════════════════════\n");

            javax.swing.SwingUtilities.invokeLater(() -> {
                MainFrame view = new MainFrame(controller);
                view.setVisible(true);
            });
        }

        if ("console".equals(modo)) {
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println(" EXECUÇÃO FINALIZADA (modo --console)");
            System.out.println("═══════════════════════════════════════════════════════");
        }
    }

    /**
     * Executa todos os requisitos 3.1 a 3.12 no console,
     * delegando a lógica ao Controller.
     *
     * Este método pode ser chamado tanto pelo modo console quanto pela GUI.
     */
    public static void executarRequisitos(FuncionarioController controller) {

        // ═══════════════════════════════════════════════════
        // 3.1 – Inserir todos os funcionários
        // 3.2 – Remover o funcionário "João" (feito automaticamente pelo Controller)
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.1 – INSERIR TODOS OS FUNCIONÁRIOS");
        System.out.println("═══════════════════════════════════════════════════════\n");

        controller.removerTodos(); // Limpar para execução limpa
        List<Funcionario> inseridos = controller.inserirFuncionariosPadrao();

        for (Funcionario f : inseridos) {
            System.out.printf("  ✓ Inserido: %s (ID: %d)%n", f.getNome(), f.getId());
        }
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.2 – REMOVER O FUNCIONÁRIO 'JOÃO'");
        System.out.println("═══════════════════════════════════════════════════════\n");
        System.out.println("  ✓ Funcionário 'João' removido automaticamente pelo código.\n");

        // ═══════════════════════════════════════════════════
        // 3.3 – Imprimir todos os funcionários formatados
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.3 – LISTA COMPLETA DE FUNCIONÁRIOS");
        System.out.println("═══════════════════════════════════════════════════════\n");

        List<Funcionario> funcionarios = controller.listarTodos();
        System.out.printf("  %-10s | %-12s | %-14s | %s%n", "Nome", "Data Nasc.", "Salário (R$)", "Função");
        System.out.println("  " + "─".repeat(55));

        for (Funcionario f : funcionarios) {
            System.out.printf("  %-10s | %-12s | R$ %-11s | %s%n",
                    f.getNome(),
                    FormatUtil.formatarData(f.getDataNascimento()),
                    FormatUtil.formatarValor(f.getSalario()),
                    f.getFuncao());
        }
        System.out.println();

        // ═══════════════════════════════════════════════════
        // 3.4 – Aumento de 10% no salário
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.4 – AUMENTO DE 10% NO SALÁRIO");
        System.out.println("═══════════════════════════════════════════════════════\n");

        List<Funcionario> antesAumento = controller.listarTodos();
        controller.aplicarAumento(new BigDecimal("10"));
        List<Funcionario> depoisAumento = controller.listarTodos();

        for (int i = 0; i < antesAumento.size(); i++) {
            System.out.printf("  %s: R$ %s → R$ %s%n",
                    depoisAumento.get(i).getNome(),
                    FormatUtil.formatarValor(antesAumento.get(i).getSalario()),
                    FormatUtil.formatarValor(depoisAumento.get(i).getSalario()));
        }
        System.out.println();

        // ═══════════════════════════════════════════════════
        // 3.5/3.6 – Agrupar por função
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.5/3.6 – FUNCIONÁRIOS AGRUPADOS POR FUNÇÃO");
        System.out.println("═══════════════════════════════════════════════════════\n");

        Map<String, List<Funcionario>> agrupados = controller.agruparPorFuncao();
        for (Map.Entry<String, List<Funcionario>> entry : agrupados.entrySet()) {
            System.out.println("  ┌─ " + entry.getKey().toUpperCase());
            for (Funcionario f : entry.getValue()) {
                System.out.printf("  │  • %s (R$ %s)%n", f.getNome(), FormatUtil.formatarValor(f.getSalario()));
            }
            System.out.println("  └────────────────────────\n");
        }

        // ═══════════════════════════════════════════════════
        // 3.8 – Aniversariantes meses 10 e 12
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.8 – ANIVERSARIANTES (MESES 10 E 12)");
        System.out.println("═══════════════════════════════════════════════════════\n");

        List<Funcionario> aniversariantes = controller.buscarAniversariantes(10, 12);
        if (aniversariantes.isEmpty()) {
            System.out.println("  Nenhum funcionário faz aniversário nos meses 10 ou 12.\n");
        } else {
            for (Funcionario f : aniversariantes) {
                System.out.printf("  • %s - %s (Mês %d)%n",
                        f.getNome(),
                        FormatUtil.formatarData(f.getDataNascimento()),
                        f.getDataNascimento().getMonthValue());
            }
            System.out.println();
        }

        // ═══════════════════════════════════════════════════
        // 3.9 – Funcionário com maior idade
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.9 – FUNCIONÁRIO COM MAIOR IDADE");
        System.out.println("═══════════════════════════════════════════════════════\n");

        Funcionario maisVelho = controller.buscarMaisVelho();
        if (maisVelho != null) {
            int idade = controller.calcularIdade(maisVelho);
            System.out.printf("  Nome: %s%n", maisVelho.getNome());
            System.out.printf("  Idade: %d anos%n%n", idade);
        }

        // ═══════════════════════════════════════════════════
        // 3.10 – Funcionários por ordem alfabética
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.10 – FUNCIONÁRIOS EM ORDEM ALFABÉTICA");
        System.out.println("═══════════════════════════════════════════════════════\n");

        List<Funcionario> ordenados = controller.listarPorOrdemAlfabetica();
        int idx = 1;
        for (Funcionario f : ordenados) {
            System.out.printf("  %2d. %s (%s - R$ %s)%n",
                    idx++, f.getNome(), f.getFuncao(), FormatUtil.formatarValor(f.getSalario()));
        }
        System.out.println();

        // ═══════════════════════════════════════════════════
        // 3.11 – Total dos salários
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.11 – TOTAL DOS SALÁRIOS");
        System.out.println("═══════════════════════════════════════════════════════\n");

        BigDecimal total = controller.calcularTotalSalarios();
        System.out.printf("  Total: R$ %s%n%n", FormatUtil.formatarValor(total));

        // ═══════════════════════════════════════════════════
        // 3.12 – Salários mínimos por funcionário
        // ═══════════════════════════════════════════════════
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.12 – SALÁRIOS MÍNIMOS POR FUNCIONÁRIO");
        System.out.println("  (Salário Mínimo = R$ " + FormatUtil.formatarValor(controller.getSalarioMinimo()) + ")");
        System.out.println("═══════════════════════════════════════════════════════\n");

        funcionarios = controller.listarTodos();
        for (Funcionario f : funcionarios) {
            BigDecimal qtd = controller.calcularSalariosMinimos(f);
            System.out.printf("  %-10s: R$ %-11s = %s salários mínimos%n",
                    f.getNome(), FormatUtil.formatarValor(f.getSalario()), FormatUtil.formatarValor(qtd));
        }
        System.out.println();
    }

    /**
     * Executa todos os requisitos 3.1 a 3.12 e retorna o resultado como String.
     * Usado pela interface gráfica para exibir no painel de resultado.
     */
    public static String executarRequisitosTexto(FuncionarioController controller) {
        StringBuilder sb = new StringBuilder();

        // ── 3.1 – Inserir todos os funcionários ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.1 – INSERIR TODOS OS FUNCIONÁRIOS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        controller.removerTodos();
        List<Funcionario> inseridos = controller.inserirFuncionariosPadrao();

        for (Funcionario f : inseridos) {
            sb.append(String.format("  ✓ Inserido: %s (ID: %d)%n", f.getNome(), f.getId()));
        }
        sb.append("\n");

        // ── 3.2 – Remover "João" (já feito automaticamente) ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.2 – REMOVER O FUNCIONÁRIO 'JOÃO'\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        sb.append("  ✓ Funcionário 'João' removido automaticamente pelo código.\n\n");

        // ── 3.3 – Imprimir todos os funcionários ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.3 – LISTA COMPLETA DE FUNCIONÁRIOS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        List<Funcionario> funcionarios = controller.listarTodos();
        sb.append(String.format("  %-10s | %-12s | %-14s | %s%n", "Nome", "Data Nasc.", "Salário (R$)", "Função"));
        sb.append("  " + "─".repeat(55) + "\n");

        for (Funcionario f : funcionarios) {
            sb.append(String.format("  %-10s | %-12s | R$ %-11s | %s%n",
                    f.getNome(),
                    FormatUtil.formatarData(f.getDataNascimento()),
                    FormatUtil.formatarValor(f.getSalario()),
                    f.getFuncao()));
        }
        sb.append("\n");

        // ── 3.4 – Aumento de 10% ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.4 – AUMENTO DE 10% NO SALÁRIO\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        List<Funcionario> antesAumento = controller.listarTodos();
        controller.aplicarAumento(new BigDecimal("10"));
        List<Funcionario> depoisAumento = controller.listarTodos();

        for (int i = 0; i < antesAumento.size(); i++) {
            sb.append(String.format("  %s: R$ %s → R$ %s%n",
                    depoisAumento.get(i).getNome(),
                    FormatUtil.formatarValor(antesAumento.get(i).getSalario()),
                    FormatUtil.formatarValor(depoisAumento.get(i).getSalario())));
        }
        sb.append("\n");

        // ── 3.5/3.6 – Agrupar por função ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.5/3.6 – FUNCIONÁRIOS AGRUPADOS POR FUNÇÃO\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        Map<String, List<Funcionario>> agrupados = controller.agruparPorFuncao();
        for (Map.Entry<String, List<Funcionario>> entry : agrupados.entrySet()) {
            sb.append("  ┌─ " + entry.getKey().toUpperCase() + "\n");
            for (Funcionario f : entry.getValue()) {
                sb.append(String.format("  │  • %s (R$ %s)%n", f.getNome(), FormatUtil.formatarValor(f.getSalario())));
            }
            sb.append("  └────────────────────────\n\n");
        }

        // ── 3.8 – Aniversariantes meses 10 e 12 ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.8 – ANIVERSARIANTES (MESES 10 E 12)\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        List<Funcionario> aniversariantes = controller.buscarAniversariantes(10, 12);
        if (aniversariantes.isEmpty()) {
            sb.append("  Nenhum funcionário faz aniversário nos meses 10 ou 12.\n");
        } else {
            for (Funcionario f : aniversariantes) {
                sb.append(String.format("  • %s - %s (Mês %d)%n",
                        f.getNome(),
                        FormatUtil.formatarData(f.getDataNascimento()),
                        f.getDataNascimento().getMonthValue()));
            }
        }
        sb.append("\n");

        // ── 3.9 – Funcionário mais velho ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.9 – FUNCIONÁRIO COM MAIOR IDADE\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        Funcionario maisVelho = controller.buscarMaisVelho();
        if (maisVelho != null) {
            int idade = controller.calcularIdade(maisVelho);
            sb.append(String.format("  Nome: %s%n", maisVelho.getNome()));
            sb.append(String.format("  Idade: %d anos%n", idade));
        }
        sb.append("\n");

        // ── 3.10 – Ordem alfabética ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.10 – FUNCIONÁRIOS EM ORDEM ALFABÉTICA\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        List<Funcionario> ordenados = controller.listarPorOrdemAlfabetica();
        int idx = 1;
        for (Funcionario f : ordenados) {
            sb.append(String.format("  %2d. %s (%s - R$ %s)%n",
                    idx++, f.getNome(), f.getFuncao(), FormatUtil.formatarValor(f.getSalario())));
        }
        sb.append("\n");

        // ── 3.11 – Total dos salários ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.11 – TOTAL DOS SALÁRIOS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        BigDecimal total = controller.calcularTotalSalarios();
        sb.append(String.format("  Total: R$ %s%n%n", FormatUtil.formatarValor(total)));

        // ── 3.12 – Salários mínimos ──
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.12 – SALÁRIOS MÍNIMOS POR FUNCIONÁRIO\n");
        sb.append("  (Salário Mínimo = R$ " + FormatUtil.formatarValor(controller.getSalarioMinimo()) + ")\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        funcionarios = controller.listarTodos();
        for (Funcionario f : funcionarios) {
            BigDecimal qtd = controller.calcularSalariosMinimos(f);
            sb.append(String.format("  %-10s: R$ %-11s = %s salários mínimos%n",
                    f.getNome(), FormatUtil.formatarValor(f.getSalario()), FormatUtil.formatarValor(qtd)));
        }
        sb.append("\n");

        return sb.toString();
    }
}
