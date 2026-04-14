package com.teste.view;

import com.teste.Principal;
import com.teste.controller.FuncionarioController;
import com.teste.dto.DashboardDTO;
import com.teste.dto.FuncionarioDTO;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Menu textual do terminal — chama os mesmos métodos do {@link FuncionarioController} usados na GUI.
 */
public final class ConsoleMenu {

    private ConsoleMenu() {
    }

    public static void executar(FuncionarioController controller) {
        Scanner sc = new Scanner(System.in);
        int mesDashboard = LocalDate.now().getMonthValue();

        while (true) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║     MÓDULO RH — MENU (mesmos requisitos do teste)        ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║  1 - Inserir funcionários                                ║");
            System.out.println("║  2 - Remover João                                        ║");
            System.out.println("║  3 - Listar todos                                        ║");
            System.out.println("║  4 - Aplicar aumento                                     ║");
            System.out.println("║  5 - Agrupar por função                                  ║");
            System.out.println("║  6 - Aniversariantes (10 e 12)                           ║");
            System.out.println("║  7 - Funcionário mais velho                              ║");
            System.out.println("║  8 - Ordenar alfabeticamente                             ║");
            System.out.println("║  9 - Total salários                                      ║");
            System.out.println("║ 10 - Salários mínimos                                    ║");
            System.out.println("║ 11 - Dashboard                                           ║");
            System.out.println("║ 12 - Exportar CSV                                        ║");
            System.out.println("║ 13 - Exportar PDF                                        ║");
            System.out.println("║ 14 - Buscar por nome                                     ║");
            System.out.println("║ 15 - Filtrar por função                                  ║");
            System.out.println("║ 16 - Filtrar aniversariantes por mês                     ║");
            System.out.println("║ 17 - Executar demonstração completa (3.1–3.12)         ║");
            System.out.println("║  0 - Sair                                                ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.print("Opção: ");

            String linha = sc.nextLine().trim();
            int op;
            try {
                op = Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida.\n");
                continue;
            }

            try {
                switch (op) {
                    case 0 -> {
                        System.out.println("Encerrando.");
                        return;
                    }
                    case 1 -> opcaoInserir(controller);
                    case 2 -> opcaoRemoverJoao(controller);
                    case 3 -> opcaoListar(controller);
                    case 4 -> opcaoAumento(controller);
                    case 5 -> opcaoAgrupar(controller);
                    case 6 -> opcaoAniversariantes(controller);
                    case 7 -> opcaoMaisVelho(controller);
                    case 8 -> opcaoOrdenar(sc, controller);
                    case 9 -> opcaoTotalSalarios(controller);
                    case 10 -> opcaoSalariosMinimos(controller);
                    case 11 -> {
                        mesDashboard = lerMes(sc, mesDashboard);
                        imprimirDashboard(controller, mesDashboard);
                    }
                    case 12 -> {
                        mesDashboard = lerMes(sc, mesDashboard);
                        Path p = Path.of("relatorio_rh.csv").toAbsolutePath().normalize();
                        controller.exportarCsv(p, mesDashboard);
                        System.out.println("CSV gerado: " + p);
                    }
                    case 13 -> {
                        mesDashboard = lerMes(sc, mesDashboard);
                        Path p = Path.of("relatorio_rh.pdf").toAbsolutePath().normalize();
                        controller.exportarPdf(p, mesDashboard);
                        System.out.println("PDF gerado: " + p);
                    }
                    case 14 -> opcaoBuscaNome(sc, controller);
                    case 15 -> opcaoFiltroFuncao(sc, controller);
                    case 16 -> opcaoFiltroMesAniversario(sc, controller);
                    case 17 -> System.out.print(Principal.executarRequisitosTexto(controller));
                    default -> System.out.println("Opção inválida.\n");
                }
            } catch (Exception ex) {
                System.out.println("Erro: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private static int lerMes(Scanner sc, int atual) {
        System.out.print("Mês para dashboard/export (1-12) [" + atual + "]: ");
        String m = sc.nextLine().trim();
        if (m.isEmpty()) return atual;
        try {
            int v = Integer.parseInt(m);
            if (v >= 1 && v <= 12) return v;
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Valor inválido; usando " + atual + ".");
        return atual;
    }

    private static void opcaoInserir(FuncionarioController c) {
        c.removerTodos();
        List<com.teste.model.Funcionario> inseridos = c.inserirFuncionariosPadrao();
        System.out.println("\n── Inseridos (DTO) ──");
        for (com.teste.model.Funcionario f : inseridos) {
            FuncionarioDTO dto = c.toDTO(f);
            System.out.printf("  %s | %s | R$ %s | %s | %d anos | %s SM%n",
                    dto.getNome(), dto.getDataNascimentoFormatada(), dto.getSalarioFormatado(),
                    dto.getFuncao(), dto.getIdade(), dto.getSalariosMinimos());
        }
        System.out.println();
    }

    private static void opcaoRemoverJoao(FuncionarioController c) {
        c.removerPorNome("João");
        System.out.println("\n✓ Registros com nome 'João' removidos.\n");
    }

    private static void opcaoListar(FuncionarioController c) {
        System.out.println("\n── Lista (DTO) ──");
        for (FuncionarioDTO f : c.listarTodosDTO()) {
            System.out.printf("  %-12s | %-12s | R$ %-12s | %-14s | %d anos | %s SM%n",
                    f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(),
                    f.getFuncao(), f.getIdade(), f.getSalariosMinimos());
        }
        System.out.println();
    }

    private static void opcaoAumento(FuncionarioController c) {
        List<FuncionarioDTO> antes = c.listarTodosDTO();
        c.aplicarAumento(new BigDecimal("10"));
        List<FuncionarioDTO> depois = c.listarTodosDTO();
        System.out.println("\n── Aumento 10% (DTO) ──");
        for (int i = 0; i < antes.size(); i++) {
            System.out.printf("  %s: R$ %s → R$ %s%n",
                    depois.get(i).getNome(),
                    antes.get(i).getSalarioFormatado(),
                    depois.get(i).getSalarioFormatado());
        }
        System.out.println();
    }

    private static void opcaoAgrupar(FuncionarioController c) {
        System.out.println("\n── Agrupamento (DTO) ──");
        for (Map.Entry<String, List<FuncionarioDTO>> e : c.agruparPorFuncaoDTO().entrySet()) {
            System.out.println("  ┌─ " + e.getKey().toUpperCase());
            for (FuncionarioDTO f : e.getValue()) {
                System.out.printf("  │  • %s (R$ %s)%n", f.getNome(), f.getSalarioFormatado());
            }
            System.out.println("  └────────────────────────\n");
        }
    }

    private static void opcaoAniversariantes(FuncionarioController c) {
        System.out.println("\n── Aniversariantes meses 10 e 12 (DTO) ──");
        List<FuncionarioDTO> list = c.buscarAniversariantesDTO(10, 12);
        if (list.isEmpty()) {
            System.out.println("  (nenhum)\n");
            return;
        }
        for (FuncionarioDTO f : list) {
            System.out.printf("  • %s — %s%n", f.getNome(), f.getDataNascimentoFormatada());
        }
        System.out.println();
    }

    private static void opcaoMaisVelho(FuncionarioController c) {
        FuncionarioDTO m = c.buscarMaisVelhoDTO();
        System.out.println("\n── Mais velho (DTO) ──");
        if (m == null) {
            System.out.println("  (nenhum cadastro)\n");
            return;
        }
        System.out.printf("  %s — %d anos — %s%n%n", m.getNome(), m.getIdade(), m.getDataNascimentoFormatada());
    }

    private static void opcaoOrdenar(Scanner sc, FuncionarioController c) {
        System.out.print("Ordem: 1 = A-Z, 2 = Z-A [1]: ");
        String o = sc.nextLine().trim();
        List<FuncionarioDTO> list = "2".equals(o) ? c.listarPorOrdemAlfabeticaDescDTO() : c.listarPorOrdemAlfabeticaDTO();
        System.out.println("\n── Ordenado (DTO) ──");
        int i = 1;
        for (FuncionarioDTO f : list) {
            System.out.printf("  %2d. %s | %s | R$ %s%n", i++, f.getNome(), f.getFuncao(), f.getSalarioFormatado());
        }
        System.out.println();
    }

    private static void opcaoTotalSalarios(FuncionarioController c) {
        System.out.println("\n── Total da folha ──");
        System.out.printf("  R$ %s%n%n", com.teste.util.FormatUtil.formatarValor(c.calcularTotalSalarios()));
    }

    private static void opcaoSalariosMinimos(FuncionarioController c) {
        System.out.println("\n── Salários mínimos (DTO) ──");
        System.out.println("  (SM = R$ " + com.teste.util.FormatUtil.formatarValor(c.getSalarioMinimo()) + ")");
        for (FuncionarioDTO f : c.listarTodosDTO()) {
            System.out.printf("  %-12s: R$ %-12s = %s SM%n", f.getNome(), f.getSalarioFormatado(), f.getSalariosMinimos());
        }
        System.out.println();
    }

    private static void imprimirDashboard(FuncionarioController c, int mes) {
        DashboardDTO d = c.construirDashboard(mes);
        System.out.println("\n════════════ DASHBOARD ════════════");
        System.out.println("Mês referência aniversariantes: " + mes);
        System.out.println("Total de funcionários: " + d.getTotalFuncionarios());
        System.out.println("Folha salarial: R$ " + d.getTotalFolha());
        System.out.println("Média salarial: R$ " + d.getMediaSalarial());
        if (d.getMaisVelho() != null) {
            System.out.println("Mais velho: " + d.getMaisVelho().getNome() + " (" + d.getMaisVelho().getIdade() + " anos)");
        } else {
            System.out.println("Mais velho: —");
        }
        System.out.println("Aniversariantes no mês " + mes + ":");
        for (FuncionarioDTO f : d.getAniversariantesMes()) {
            System.out.println("  • " + f.getNome() + " — " + f.getDataNascimentoFormatada());
        }
        System.out.println("════════════════════════════════════\n");
    }

    private static void opcaoBuscaNome(Scanner sc, FuncionarioController c) {
        System.out.print("Trecho do nome: ");
        String q = sc.nextLine();
        System.out.println("\n── Resultado (DTO) ──");
        for (FuncionarioDTO f : c.filtrarPorNomeContendo(q)) {
            System.out.printf("  %-12s | %s | R$ %s | %s%n", f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(), f.getFuncao());
        }
        System.out.println();
    }

    private static void opcaoFiltroFuncao(Scanner sc, FuncionarioController c) {
        System.out.print("Função (ex.: Operador): ");
        String fnc = sc.nextLine();
        System.out.println("\n── Resultado (DTO) ──");
        for (FuncionarioDTO f : c.filtrarPorFuncao(fnc)) {
            System.out.printf("  %-12s | %s | R$ %s%n", f.getNome(), f.getFuncao(), f.getSalarioFormatado());
        }
        System.out.println();
    }

    private static void opcaoFiltroMesAniversario(Scanner sc, FuncionarioController c) {
        System.out.print("Mês (1-12): ");
        String m = sc.nextLine().trim();
        try {
            int mes = Integer.parseInt(m);
            if (mes < 1 || mes > 12) throw new IllegalArgumentException();
            System.out.println("\n── Aniversariantes mês " + mes + " (DTO) ──");
            for (FuncionarioDTO f : c.aniversariantesPorMes(mes)) {
                System.out.printf("  • %s — %s%n", f.getNome(), f.getDataNascimentoFormatada());
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Mês inválido.\n");
        }
    }
}
