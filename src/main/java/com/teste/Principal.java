package com.teste;

import com.teste.controller.FuncionarioController;
import com.teste.dto.FuncionarioDTO;
import com.teste.model.Funcionario;
import com.teste.util.DatabaseUtil;
import com.teste.util.FormatUtil;
import com.teste.view.ConsoleMenu;
import com.teste.view.MainFrame;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Ponto de entrada — módulo RH (GUI e terminal compartilham o mesmo {@link FuncionarioController}).
 *
 * <ul>
 *   <li>{@code --console} — menu interativo no terminal (sem GUI)</li>
 *   <li>{@code --gui} — apenas interface gráfica</li>
 *   <li>sem argumentos — abre a interface gráfica (use {@code --console} para o menu)</li>
 * </ul>
 */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        String modo = "gui";
        if (args.length > 0) {
            if ("--console".equalsIgnoreCase(args[0])) {
                modo = "console";
            } else if ("--gui".equalsIgnoreCase(args[0])) {
                modo = "gui";
            }
        }

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   MÓDULO RH — JAVA + MAVEN + MVC + SERVICE + DTO     ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        DatabaseUtil.inicializarBanco();
        FuncionarioController controller = new FuncionarioController();

        if ("console".equals(modo)) {
            ConsoleMenu.executar(controller);
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println(" EXECUÇÃO FINALIZADA (modo --console)");
            System.out.println("═══════════════════════════════════════════════════════");
            return;
        }

        System.out.println("Abrindo interface gráfica… (terminal: java -jar … --console)\n");
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame(controller);
            view.setVisible(true);
        });
    }

    /**
     * Demonstração sequencial dos requisitos 3.1 a 3.12 — saída baseada apenas em DTOs formatados pelo Service.
     */
    public static void executarRequisitos(FuncionarioController controller) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.1 – INSERIR TODOS OS FUNCIONÁRIOS");
        System.out.println("═══════════════════════════════════════════════════════\n");

        controller.removerTodos();
        List<Funcionario> inseridos = controller.inserirFuncionariosPadrao();
        for (Funcionario f : inseridos) {
            FuncionarioDTO dto = controller.toDTO(f);
            System.out.printf("  ✓ Inserido: %s (ID: %d) — %s | R$ %s%n",
                    dto.getNome(), f.getId(), dto.getDataNascimentoFormatada(), dto.getSalarioFormatado());
        }
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.2 – REMOVER O FUNCIONÁRIO 'JOÃO'");
        System.out.println("═══════════════════════════════════════════════════════\n");
        controller.removerPorNome("João");
        System.out.println("  ✓ Funcionário 'João' removido.\n");

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.3 – LISTA COMPLETA (DTO)");
        System.out.println("═══════════════════════════════════════════════════════\n");
        imprimirTabelaDto(controller.listarTodosDTO());

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.4 – AUMENTO DE 10%");
        System.out.println("═══════════════════════════════════════════════════════\n");
        List<FuncionarioDTO> antes = controller.listarTodosDTO();
        controller.aplicarAumento(new BigDecimal("10"));
        List<FuncionarioDTO> depois = controller.listarTodosDTO();
        for (int i = 0; i < antes.size(); i++) {
            System.out.printf("  %s: R$ %s → R$ %s%n",
                    depois.get(i).getNome(), antes.get(i).getSalarioFormatado(), depois.get(i).getSalarioFormatado());
        }
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.5/3.6 – AGRUPADOS POR FUNÇÃO (DTO)");
        System.out.println("═══════════════════════════════════════════════════════\n");
        for (Map.Entry<String, List<FuncionarioDTO>> e : controller.agruparPorFuncaoDTO().entrySet()) {
            System.out.println("  ┌─ " + e.getKey().toUpperCase());
            for (FuncionarioDTO f : e.getValue()) {
                System.out.printf("  │  • %s (R$ %s)%n", f.getNome(), f.getSalarioFormatado());
            }
            System.out.println("  └────────────────────────\n");
        }

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.8 – ANIVERSARIANTES (MESES 10 E 12) — DTO");
        System.out.println("═══════════════════════════════════════════════════════\n");
        List<FuncionarioDTO> aniv = controller.buscarAniversariantesDTO(10, 12);
        if (aniv.isEmpty()) {
            System.out.println("  Nenhum nos meses 10 ou 12.\n");
        } else {
            for (FuncionarioDTO f : aniv) {
                System.out.printf("  • %s — %s%n", f.getNome(), f.getDataNascimentoFormatada());
            }
            System.out.println();
        }

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.9 – FUNCIONÁRIO MAIS VELHO — DTO");
        System.out.println("═══════════════════════════════════════════════════════\n");
        FuncionarioDTO mv = controller.buscarMaisVelhoDTO();
        if (mv != null) {
            System.out.printf("  %s — %d anos%n%n", mv.getNome(), mv.getIdade());
        }

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.10 – ORDEM ALFABÉTICA — DTO");
        System.out.println("═══════════════════════════════════════════════════════\n");
        int idx = 1;
        for (FuncionarioDTO f : controller.listarPorOrdemAlfabeticaDTO()) {
            System.out.printf("  %2d. %s (%s - R$ %s)%n", idx++, f.getNome(), f.getFuncao(), f.getSalarioFormatado());
        }
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.11 – TOTAL DOS SALÁRIOS");
        System.out.println("═══════════════════════════════════════════════════════\n");
        System.out.printf("  Total: R$ %s%n%n", FormatUtil.formatarValor(controller.calcularTotalSalarios()));

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(" 3.12 – SALÁRIOS MÍNIMOS — DTO");
        System.out.println("  (SM = R$ " + FormatUtil.formatarValor(controller.getSalarioMinimo()) + ")");
        System.out.println("═══════════════════════════════════════════════════════\n");
        for (FuncionarioDTO f : controller.listarTodosDTO()) {
            System.out.printf("  %-10s: R$ %-12s = %s SM%n", f.getNome(), f.getSalarioFormatado(), f.getSalariosMinimos());
        }
        System.out.println();
    }

    private static void imprimirTabelaDto(List<FuncionarioDTO> lista) {
        System.out.printf("  %-10s | %-12s | %-14s | %s | %s%n", "Nome", "Data Nasc.", "Salário", "Função", "SM");
        System.out.println("  " + "─".repeat(62));
        for (FuncionarioDTO f : lista) {
            System.out.printf("  %-10s | %-12s | R$ %-11s | %-12s | %s%n",
                    f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(), f.getFuncao(), f.getSalariosMinimos());
        }
        System.out.println();
    }

    /**
     * Mesma sequência de {@link #executarRequisitos}, em texto para a GUI.
     */
    public static String executarRequisitosTexto(FuncionarioController controller) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  DEMONSTRAÇÃO 3.1 – 3.12 (DTO)\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        controller.removerTodos();
        List<Funcionario> inseridos = controller.inserirFuncionariosPadrao();
        for (Funcionario f : inseridos) {
            FuncionarioDTO dto = controller.toDTO(f);
            sb.append(String.format("  ✓ %s | %s | R$ %s%n", dto.getNome(), dto.getDataNascimentoFormatada(), dto.getSalarioFormatado()));
        }
        sb.append("\n");

        sb.append("3.2 – Remover João\n");
        controller.removerPorNome("João");
        sb.append("  ✓ Removido.\n\n");

        sb.append("3.3 – Lista (DTO)\n");
        for (FuncionarioDTO f : controller.listarTodosDTO()) {
            sb.append(String.format("  %s | %s | R$ %s | %s%n",
                    f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(), f.getFuncao()));
        }
        sb.append("\n");

        List<FuncionarioDTO> antes = controller.listarTodosDTO();
        controller.aplicarAumento(new BigDecimal("10"));
        List<FuncionarioDTO> depois = controller.listarTodosDTO();
        sb.append("3.4 – Aumento 10%\n");
        for (int i = 0; i < antes.size(); i++) {
            sb.append(String.format("  %s: %s → %s%n", depois.get(i).getNome(), antes.get(i).getSalarioFormatado(), depois.get(i).getSalarioFormatado()));
        }
        sb.append("\n");

        sb.append("3.5/3.6 – Agrupamento (DTO)\n");
        for (Map.Entry<String, List<FuncionarioDTO>> e : controller.agruparPorFuncaoDTO().entrySet()) {
            sb.append("  ").append(e.getKey()).append(":\n");
            for (FuncionarioDTO f : e.getValue()) {
                sb.append(String.format("    • %s (R$ %s)%n", f.getNome(), f.getSalarioFormatado()));
            }
        }
        sb.append("\n");

        sb.append("3.8 – Aniversariantes 10 e 12\n");
        for (FuncionarioDTO f : controller.buscarAniversariantesDTO(10, 12)) {
            sb.append(String.format("  • %s — %s%n", f.getNome(), f.getDataNascimentoFormatada()));
        }
        sb.append("\n");

        FuncionarioDTO mv = controller.buscarMaisVelhoDTO();
        sb.append("3.9 – Mais velho\n");
        if (mv != null) {
            sb.append(String.format("  %s — %d anos%n", mv.getNome(), mv.getIdade()));
        }
        sb.append("\n");

        sb.append("3.10 – Ordem alfabética\n");
        int i = 1;
        for (FuncionarioDTO f : controller.listarPorOrdemAlfabeticaDTO()) {
            sb.append(String.format("  %2d. %s%n", i++, f.getNome()));
        }
        sb.append("\n");

        sb.append("3.11 – Total\n");
        sb.append(String.format("  R$ %s%n", FormatUtil.formatarValor(controller.calcularTotalSalarios())));
        sb.append("\n");

        sb.append("3.12 – Salários mínimos\n");
        for (FuncionarioDTO f : controller.listarTodosDTO()) {
            sb.append(String.format("  %s: %s SM%n", f.getNome(), f.getSalariosMinimos()));
        }
        sb.append("\n");

        return sb.toString();
    }
}
