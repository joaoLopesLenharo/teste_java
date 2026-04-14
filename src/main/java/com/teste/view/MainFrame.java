package com.teste.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.teste.controller.FuncionarioController;
import com.teste.model.Funcionario;
import com.teste.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * View na arquitetura MVC.
 * Responsável exclusivamente pela apresentação e interação com o usuário.
 * Toda lógica de negócio é delegada ao FuncionarioController.
 *
 * Requisito 5 - Tela completa com CRUD e todas as funções.
 */
public class MainFrame extends JFrame {

    // ── Controller (MVC) ──────────────────────────────────────
    private final FuncionarioController controller;

    // ── Componentes da interface ──────────────────────────────
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea outputArea;
    private JTextField nomeField, dataNascField, salarioField, funcaoField;
    private JButton btnSalvar;
    private int editandoId = -1; // -1 = novo, > 0 = editando

    // ── Paleta de cores do tema ───────────────────────────────
    private static final Color ACCENT       = new Color(100, 140, 255);
    private static final Color ACCENT_HOVER = new Color(130, 165, 255);
    private static final Color DANGER       = new Color(255, 90, 90);
    private static final Color DANGER_HOVER = new Color(255, 120, 120);
    private static final Color SUCCESS      = new Color(80, 200, 120);
    private static final Color SUCCESS_HOVER= new Color(110, 220, 150);
    private static final Color WARNING      = new Color(255, 180, 50);
    private static final Color WARNING_HOVER= new Color(255, 200, 90);
    private static final Color BG_DARK      = new Color(30, 30, 40);
    private static final Color BG_PANEL     = new Color(40, 42, 54);
    private static final Color BG_INPUT     = new Color(50, 52, 65);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 240);
    private static final Color TEXT_SECONDARY = new Color(160, 165, 180);
    private static final Color BORDER       = new Color(60, 62, 75);

    /**
     * Construtor da View. Recebe o Controller via injeção de dependência.
     */
    public MainFrame(FuncionarioController controller) {
        this.controller = controller;
        initLookAndFeel();
        configurarJanela();
        montarLayout();
        atualizarTabela();
    }

    // ═══════════════════════════════════════════════════════════
    //  CONFIGURAÇÃO INICIAL
    // ═══════════════════════════════════════════════════════════

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("TabbedPane.selectedBackground", BG_PANEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarJanela() {
        setTitle("⚙ Sistema de Gestão de Funcionários — MVC");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
    }

    // ═══════════════════════════════════════════════════════════
    //  MONTAGEM DO LAYOUT
    // ═══════════════════════════════════════════════════════════

    private void montarLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_DARK);

        // Header
        mainPanel.add(criarHeader(), BorderLayout.NORTH);

        // Split vertical: tabela+form em cima, operações+output embaixo
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBackground(BG_DARK);
        splitPane.setBorder(null);
        splitPane.setDividerSize(6);
        splitPane.setResizeWeight(0.55);

        // ── Painel superior: Tabela + Formulário ───────────
        JPanel painelSuperior = new JPanel(new BorderLayout(12, 0));
        painelSuperior.setBackground(BG_DARK);
        painelSuperior.setBorder(new EmptyBorder(10, 16, 5, 16));
        painelSuperior.add(criarPainelTabela(), BorderLayout.CENTER);
        painelSuperior.add(criarPainelFormulario(), BorderLayout.EAST);
        splitPane.setTopComponent(painelSuperior);

        // ── Painel inferior: Operações + Output ────────────
        JPanel painelInferior = new JPanel(new BorderLayout(12, 0));
        painelInferior.setBackground(BG_DARK);
        painelInferior.setBorder(new EmptyBorder(5, 16, 16, 16));
        painelInferior.add(criarPainelOperacoes(), BorderLayout.WEST);
        painelInferior.add(criarPainelResultado(), BorderLayout.CENTER);
        splitPane.setBottomComponent(painelInferior);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // ═══════════════════════════════════════════════════════════
    //  HEADER
    // ═══════════════════════════════════════════════════════════

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Título
        JLabel titulo = new JLabel("Sistema de Gestão de Funcionários");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT_PRIMARY);

        JLabel subtitulo = new JLabel("   Arquitetura MVC • Java + Maven + SQLite");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SECONDARY);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.add(titulo);
        esquerda.add(subtitulo);

        // Botões de ação rápida
        JButton btnAutoCadastro = criarBotaoEstilizado("⚡ Auto-Cadastrar Funcionários", SUCCESS, SUCCESS_HOVER);
        btnAutoCadastro.addActionListener(e -> acaoAutoCadastrar());

        JButton btnLimparBanco = criarBotaoEstilizado("🗑 Limpar Banco", DANGER, DANGER_HOVER);
        btnLimparBanco.addActionListener(e -> acaoLimparBanco());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);
        direita.add(btnAutoCadastro);
        direita.add(btnLimparBanco);

        header.add(esquerda, BorderLayout.WEST);
        header.add(direita, BorderLayout.EAST);

        // Wrapper com separador
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.add(header, BorderLayout.CENTER);
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        wrapper.add(sep, BorderLayout.SOUTH);

        return wrapper;
    }

    // ═══════════════════════════════════════════════════════════
    //  PAINEL DA TABELA
    // ═══════════════════════════════════════════════════════════

    private JPanel criarPainelTabela() {
        JPanel card = criarCard("📋 Funcionários Cadastrados");

        String[] colunas = {"ID", "Nome", "Data Nasc.", "Salário (R$)", "Função"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        estilizarTabela(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BG_INPUT);
        scrollPane.getViewport().setBackground(BG_INPUT);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        // Botões CRUD
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnPanel.setOpaque(false);

        JButton btnEditar = criarBotaoEstilizado("✏ Editar", WARNING, WARNING_HOVER);
        btnEditar.addActionListener(e -> acaoEditar());

        JButton btnRemover = criarBotaoEstilizado("✕ Remover", DANGER, DANGER_HOVER);
        btnRemover.addActionListener(e -> acaoRemover());

        JButton btnAtualizar = criarBotaoEstilizado("↻ Atualizar", ACCENT, ACCENT_HOVER);
        btnAtualizar.addActionListener(e -> atualizarTabela());

        btnPanel.add(btnEditar);
        btnPanel.add(btnRemover);
        btnPanel.add(btnAtualizar);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setBackground(BG_INPUT);
        tabela.setForeground(TEXT_PRIMARY);
        tabela.setSelectionBackground(ACCENT.darker());
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(BORDER);
        tabela.setRowHeight(32);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        // Renderers de alinhamento
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tabela.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // Larguras
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(130);
    }

    // ═══════════════════════════════════════════════════════════
    //  PAINEL DO FORMULÁRIO (CRUD)
    // ═══════════════════════════════════════════════════════════

    private JPanel criarPainelFormulario() {
        JPanel card = criarCard("➕ Adicionar / Editar");
        card.setPreferredSize(new Dimension(280, 0));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(5, 5, 5, 5));

        nomeField = criarCampoTexto("Nome do funcionário");
        dataNascField = criarCampoTexto("dd/mm/aaaa");
        salarioField = criarCampoTexto("Ex: 2500.00");
        funcaoField = criarCampoTexto("Ex: Operador");

        form.add(criarGrupoFormulario("Nome:", nomeField));
        form.add(Box.createVerticalStrut(10));
        form.add(criarGrupoFormulario("Data Nascimento:", dataNascField));
        form.add(Box.createVerticalStrut(10));
        form.add(criarGrupoFormulario("Salário:", salarioField));
        form.add(Box.createVerticalStrut(10));
        form.add(criarGrupoFormulario("Função:", funcaoField));
        form.add(Box.createVerticalStrut(18));

        // Botões
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnSalvar = criarBotaoEstilizado("💾 Salvar", SUCCESS, SUCCESS_HOVER);
        btnSalvar.addActionListener(e -> acaoSalvar());

        JButton btnCancelar = criarBotaoEstilizado("✕ Cancelar", new Color(100, 100, 115), new Color(130, 130, 145));
        btnCancelar.addActionListener(e -> limparFormulario());

        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);
        form.add(btnPanel);

        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private JPanel criarGrupoFormulario(String label, JTextField campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 4));
        grupo.setOpaque(false);
        grupo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_SECONDARY);

        grupo.add(lbl, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);
        return grupo;
    }

    // ═══════════════════════════════════════════════════════════
    //  PAINEL DE OPERAÇÕES (Requisitos 3.x)
    // ═══════════════════════════════════════════════════════════

    private JPanel criarPainelOperacoes() {
        JPanel card = criarCard("🔧 Operações");
        card.setPreferredSize(new Dimension(280, 0));

        JPanel ops = new JPanel();
        ops.setLayout(new BoxLayout(ops, BoxLayout.Y_AXIS));
        ops.setOpaque(false);
        ops.setBorder(new EmptyBorder(4, 4, 4, 4));

        ops.add(criarBotaoOperacao("3.3 - Imprimir Funcionários", e -> operacaoImprimirTodos()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.4 - Aumento de 10%", e -> operacaoAplicarAumento()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.5/3.6 - Agrupar por Função", e -> operacaoAgruparPorFuncao()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.8 - Aniversariantes (Mês 10 e 12)", e -> operacaoAniversariantes()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.9 - Funcionário Mais Velho", e -> operacaoMaisVelho()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.10 - Ordem Alfabética", e -> operacaoOrdemAlfabetica()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.11 - Total dos Salários", e -> operacaoTotalSalarios()));
        ops.add(Box.createVerticalStrut(5));
        ops.add(criarBotaoOperacao("3.12 - Salários Mínimos", e -> operacaoSalariosMinimos()));

        JScrollPane scrollPane = new JScrollPane(ops);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════════════════════
    //  PAINEL DE RESULTADO
    // ═══════════════════════════════════════════════════════════

    private JPanel criarPainelResultado() {
        JPanel card = criarCard("📄 Resultado das Operações");

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(25, 25, 35));
        outputArea.setForeground(new Color(200, 220, 200));
        outputArea.setCaretColor(ACCENT);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        JButton btnLimpar = criarBotaoEstilizado("🧹 Limpar Saída", new Color(100, 100, 115), new Color(130, 130, 145));
        btnLimpar.addActionListener(e -> outputArea.setText(""));

        JPanel barraInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraInferior.setOpaque(false);
        barraInferior.add(btnLimpar);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(barraInferior, BorderLayout.SOUTH);
        return card;
    }

    // ═══════════════════════════════════════════════════════════
    //  AÇÕES DO CRUD (delegam ao Controller)
    // ═══════════════════════════════════════════════════════════

    /**
     * Atualiza a tabela com dados do Controller.
     */
    private void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Funcionario> funcionarios = controller.listarTodos();
        for (Funcionario f : funcionarios) {
            tableModel.addRow(new Object[]{
                    f.getId(),
                    f.getNome(),
                    FormatUtil.formatarData(f.getDataNascimento()),
                    FormatUtil.formatarValor(f.getSalario()),
                    f.getFuncao()
            });
        }
    }

    private void acaoSalvar() {
        // Validação na View (responsabilidade de apresentação)
        String nome = nomeField.getText().trim();
        String dataNascStr = dataNascField.getText().trim();
        String salarioStr = salarioField.getText().trim();
        String funcao = funcaoField.getText().trim();

        if (nome.isEmpty() || dataNascStr.isEmpty() || salarioStr.isEmpty() || funcao.isEmpty()) {
            exibirAlerta("Todos os campos são obrigatórios!", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate dataNasc;
        try {
            dataNasc = FormatUtil.parsearData(dataNascStr);
        } catch (DateTimeParseException e) {
            exibirAlerta("Data inválida! Use o formato dd/mm/aaaa.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal salario;
        try {
            salario = FormatUtil.parsearValor(salarioStr);
        } catch (NumberFormatException e) {
            exibirAlerta("Salário inválido! Use formato numérico (ex: 2500.00).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Delega ao Controller
        if (editandoId > 0) {
            controller.atualizar(editandoId, nome, dataNasc, salario, funcao);
            exibirResultado("✓ Funcionário '" + nome + "' atualizado com sucesso!\n");
        } else {
            Funcionario f = controller.inserir(nome, dataNasc, salario, funcao);
            exibirResultado("✓ Funcionário '" + nome + "' inserido com sucesso! (ID: " + f.getId() + ")\n");
        }

        limparFormulario();
        atualizarTabela();
    }

    private void acaoEditar() {
        int row = table.getSelectedRow();
        if (row < 0) {
            exibirAlerta("Selecione um funcionário na tabela para editar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        editandoId = (int) tableModel.getValueAt(row, 0);
        nomeField.setText((String) tableModel.getValueAt(row, 1));
        dataNascField.setText((String) tableModel.getValueAt(row, 2));
        // Converter formato de exibição para formato de edição
        String salarioDisplay = (String) tableModel.getValueAt(row, 3);
        salarioField.setText(salarioDisplay.replace(".", "").replace(",", "."));
        funcaoField.setText((String) tableModel.getValueAt(row, 4));
        btnSalvar.setText("💾 Atualizar");
    }

    private void acaoRemover() {
        int row = table.getSelectedRow();
        if (row < 0) {
            exibirAlerta("Selecione um funcionário na tabela para remover.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nome = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja remover o funcionário '" + nome + "'?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            controller.remover(id);
            exibirResultado("✓ Funcionário '" + nome + "' removido com sucesso.\n");
            atualizarTabela();
        }
    }

    private void limparFormulario() {
        nomeField.setText("");
        dataNascField.setText("");
        salarioField.setText("");
        funcaoField.setText("");
        editandoId = -1;
        btnSalvar.setText("💾 Salvar");
    }

    // ═══════════════════════════════════════════════════════════
    //  AÇÕES DE AUTO-CADASTRO (delegam ao Controller)
    // ═══════════════════════════════════════════════════════════

    private void acaoAutoCadastrar() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja cadastrar automaticamente todos os funcionários da tabela padrão?\n(Os dados existentes serão mantidos)",
                "Auto-Cadastro", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Delega ao Controller
        List<Funcionario> inseridos = controller.inserirFuncionariosPadrao();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  AUTO-CADASTRO DE FUNCIONÁRIOS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        for (Funcionario f : inseridos) {
            sb.append(String.format("  ✓ %s (ID: %d) cadastrado com sucesso.%n", f.getNome(), f.getId()));
        }
        sb.append(String.format("%n  Total: %d funcionários cadastrados.%n%n", inseridos.size()));

        // Requisito 3.2 - Remover "João"
        controller.removerPorNome("João");
        sb.append("  ✓ Funcionário 'João' removido da lista (Requisito 3.2).\n\n");

        exibirResultado(sb.toString());
        atualizarTabela();
    }

    private void acaoLimparBanco() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover TODOS os funcionários do banco de dados?",
                "Confirmar Limpeza", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.removerTodos();
            exibirResultado("✓ Todos os funcionários foram removidos do banco de dados.\n\n");
            atualizarTabela();
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  OPERAÇÕES 3.x (delegam ao Controller, formatam na View)
    // ═══════════════════════════════════════════════════════════

    /** Requisito 3.3 */
    private void operacaoImprimirTodos() {
        List<Funcionario> lista = controller.listarTodos();
        if (lista.isEmpty()) { exibirVazio(); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.3 - LISTA COMPLETA DE FUNCIONÁRIOS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        sb.append(String.format("  %-10s | %-12s | %-14s | %s%n", "Nome", "Data Nasc.", "Salário (R$)", "Função"));
        sb.append("  " + "─".repeat(55) + "\n");

        for (Funcionario f : lista) {
            sb.append(String.format("  %-10s | %-12s | R$ %-11s | %s%n",
                    f.getNome(),
                    FormatUtil.formatarData(f.getDataNascimento()),
                    FormatUtil.formatarValor(f.getSalario()),
                    f.getFuncao()));
        }
        sb.append("\n");
        exibirResultado(sb.toString());
    }

    /** Requisito 3.4 */
    private void operacaoAplicarAumento() {
        List<Funcionario> antes = controller.listarTodos();
        if (antes.isEmpty()) { exibirVazio(); return; }

        controller.aplicarAumento(new BigDecimal("10"));
        List<Funcionario> depois = controller.listarTodos();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.4 - AUMENTO DE 10% APLICADO\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        for (int i = 0; i < antes.size(); i++) {
            sb.append(String.format("  %s: R$ %s → R$ %s%n",
                    depois.get(i).getNome(),
                    FormatUtil.formatarValor(antes.get(i).getSalario()),
                    FormatUtil.formatarValor(depois.get(i).getSalario())));
        }
        sb.append("\n");
        exibirResultado(sb.toString());
        atualizarTabela();
    }

    /** Requisitos 3.5 e 3.6 */
    private void operacaoAgruparPorFuncao() {
        Map<String, List<Funcionario>> agrupados = controller.agruparPorFuncao();
        if (agrupados.isEmpty()) { exibirVazio(); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.5/3.6 - FUNCIONÁRIOS AGRUPADOS POR FUNÇÃO\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        for (Map.Entry<String, List<Funcionario>> entry : agrupados.entrySet()) {
            sb.append("  ┌─ " + entry.getKey().toUpperCase() + "\n");
            for (Funcionario f : entry.getValue()) {
                sb.append(String.format("  │  • %s (R$ %s)%n", f.getNome(), FormatUtil.formatarValor(f.getSalario())));
            }
            sb.append("  └────────────────────────\n\n");
        }
        exibirResultado(sb.toString());
    }

    /** Requisito 3.8 */
    private void operacaoAniversariantes() {
        List<Funcionario> aniversariantes = controller.buscarAniversariantes(10, 12);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.8 - ANIVERSARIANTES (MESES 10 E 12)\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

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
        exibirResultado(sb.toString());
    }

    /** Requisito 3.9 */
    private void operacaoMaisVelho() {
        Funcionario maisVelho = controller.buscarMaisVelho();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.9 - FUNCIONÁRIO COM MAIOR IDADE\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        if (maisVelho != null) {
            int idade = controller.calcularIdade(maisVelho);
            sb.append(String.format("  Nome: %s%n", maisVelho.getNome()));
            sb.append(String.format("  Idade: %d anos%n", idade));
        } else {
            sb.append("  Nenhum funcionário cadastrado.\n");
        }
        sb.append("\n");
        exibirResultado(sb.toString());
    }

    /** Requisito 3.10 */
    private void operacaoOrdemAlfabetica() {
        List<Funcionario> ordenados = controller.listarPorOrdemAlfabetica();
        if (ordenados.isEmpty()) { exibirVazio(); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.10 - FUNCIONÁRIOS EM ORDEM ALFABÉTICA\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        int i = 1;
        for (Funcionario f : ordenados) {
            sb.append(String.format("  %2d. %s (%s - R$ %s)%n",
                    i++, f.getNome(), f.getFuncao(), FormatUtil.formatarValor(f.getSalario())));
        }
        sb.append("\n");
        exibirResultado(sb.toString());
    }

    /** Requisito 3.11 */
    private void operacaoTotalSalarios() {
        BigDecimal total = controller.calcularTotalSalarios();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.11 - TOTAL DOS SALÁRIOS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        sb.append(String.format("  Total: R$ %s%n%n", FormatUtil.formatarValor(total)));
        exibirResultado(sb.toString());
    }

    /** Requisito 3.12 */
    private void operacaoSalariosMinimos() {
        List<Funcionario> lista = controller.listarTodos();
        if (lista.isEmpty()) { exibirVazio(); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  3.12 - SALÁRIOS MÍNIMOS POR FUNCIONÁRIO\n");
        sb.append("  (Salário Mínimo = R$ " + FormatUtil.formatarValor(controller.getSalarioMinimo()) + ")\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");

        for (Funcionario f : lista) {
            BigDecimal qtd = controller.calcularSalariosMinimos(f);
            sb.append(String.format("  %-10s: R$ %-11s = %s salários mínimos%n",
                    f.getNome(), FormatUtil.formatarValor(f.getSalario()), FormatUtil.formatarValor(qtd)));
        }
        sb.append("\n");
        exibirResultado(sb.toString());
    }

    // ═══════════════════════════════════════════════════════════
    //  MÉTODOS AUXILIARES DE APRESENTAÇÃO
    // ═══════════════════════════════════════════════════════════

    private void exibirResultado(String texto) {
        outputArea.append(texto);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void exibirVazio() {
        exibirResultado("⚠ Nenhum funcionário cadastrado.\n\n");
    }

    private void exibirAlerta(String mensagem, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, tipo);
    }

    // ═══════════════════════════════════════════════════════════
    //  FÁBRICA DE COMPONENTES (UI)
    // ═══════════════════════════════════════════════════════════

    private JPanel criarCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(ACCENT);
        card.add(lbl, BorderLayout.NORTH);

        return card;
    }

    private JButton criarBotaoEstilizado(String texto, Color bg, Color hoverBg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });

        return btn;
    }

    private JButton criarBotaoOperacao(String texto, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_INPUT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ACCENT.darker().darker());
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT),
                        new EmptyBorder(8, 12, 8, 12)));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_INPUT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        });

        btn.addActionListener(acao);
        return btn;
    }

    private JTextField criarCampoTexto(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBackground(BG_INPUT);
        campo.setForeground(TEXT_PRIMARY);
        campo.setCaretColor(ACCENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        campo.setPreferredSize(new Dimension(0, 34));
        campo.setToolTipText(placeholder);
        campo.putClientProperty("JTextField.placeholderText", placeholder);
        return campo;
    }
}
