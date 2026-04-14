package com.teste.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.teste.Principal;
import com.teste.controller.FuncionarioController;
import com.teste.dto.DashboardDTO;
import com.teste.dto.FuncionarioDTO;
import com.teste.model.Funcionario;
import com.teste.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * View gráfica — apenas dispara ações no {@link FuncionarioController}; listas exibem {@link FuncionarioDTO}.
 */
public class MainFrame extends JFrame {

    private final FuncionarioController controller;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea outputArea;
    private JTextField nomeField;
    private JTextField dataNascField;
    private JTextField salarioField;
    private JTextField funcaoField;
    private JTextField buscaNomeField;
    private JComboBox<String> comboFuncaoFiltro;
    private JComboBox<String> comboMesFiltro;
    private JComboBox<String> comboMesDashboard;
    private JButton btnSalvar;
    private int editandoId = -1;

    private JLabel dashTotal;
    private JLabel dashFolha;
    private JLabel dashMedia;
    private JLabel dashMaisVelho;
    private JLabel dashAniv;

    private static final Color ACCENT = new Color(100, 140, 255);
    private static final Color ACCENT_HOVER = new Color(130, 165, 255);
    private static final Color DANGER = new Color(255, 90, 90);
    private static final Color DANGER_HOVER = new Color(255, 120, 120);
    private static final Color SUCCESS = new Color(80, 200, 120);
    private static final Color SUCCESS_HOVER = new Color(110, 220, 150);
    private static final Color WARNING = new Color(255, 180, 50);
    private static final Color WARNING_HOVER = new Color(255, 200, 90);
    private static final Color BG_DARK = new Color(30, 30, 40);
    private static final Color BG_PANEL = new Color(40, 42, 54);
    private static final Color BG_INPUT = new Color(50, 52, 65);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 240);
    private static final Color TEXT_SECONDARY = new Color(160, 165, 180);
    private static final Color BORDER = new Color(60, 62, 75);

    public MainFrame(FuncionarioController controller) {
        this.controller = controller;
        initLookAndFeel();
        configurarJanela();
        montarLayout();
        atualizarTabela();
        atualizarDashboard();
    }

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
        setTitle("Módulo RH — ERP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 860);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
    }

    private void montarLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_DARK);
        mainPanel.add(criarHeader(), BorderLayout.NORTH);
        mainPanel.add(criarDashboardPanel(), BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel criarHeader() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel titulo = new JLabel("Módulo RH");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT_PRIMARY);
        JLabel sub = new JLabel("   ERP • MVC + Service + DTO • SQLite");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SECONDARY);
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esq.setOpaque(false);
        esq.add(titulo);
        esq.add(sub);

        JButton btnDemo = criarBotaoEstilizado("▶ Demonstração completa (3.1–3.12)", ACCENT, ACCENT_HOVER);
        btnDemo.addActionListener(e -> acaoExecutarTodos());
        JButton btnLimpar = criarBotaoEstilizado("🗑 Limpar banco", DANGER, DANGER_HOVER);
        btnLimpar.addActionListener(e -> acaoLimparBanco());

        JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        dir.setOpaque(false);
        dir.add(btnDemo);
        dir.add(btnLimpar);

        header.add(esq, BorderLayout.WEST);
        header.add(dir, BorderLayout.EAST);

        wrap.add(header, BorderLayout.NORTH);
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        wrap.add(sep, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel criarDashboardPanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(8, 16, 16, 16));

        JPanel dashRow = criarDashboardCards();
        root.add(dashRow, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setBackground(BG_DARK);
        split.setBorder(null);
        split.setDividerSize(6);
        split.setResizeWeight(0.52);

        JPanel superior = new JPanel(new BorderLayout(10, 0));
        superior.setOpaque(false);
        superior.add(criarBarraFiltros(), BorderLayout.NORTH);
        superior.add(criarPainelTabela(), BorderLayout.CENTER);
        superior.add(criarPainelFormulario(), BorderLayout.EAST);

        JPanel inferior = new JPanel(new BorderLayout(10, 0));
        inferior.setOpaque(false);
        inferior.add(criarPainelOperacoes(), BorderLayout.WEST);
        inferior.add(criarPainelResultado(), BorderLayout.CENTER);

        split.setTopComponent(superior);
        split.setBottomComponent(inferior);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel criarDashboardCards() {
        JPanel grid = new JPanel(new GridLayout(1, 5, 10, 0));
        grid.setOpaque(false);
        grid.add(envolverCard("Total de funcionários", dashTotal = labelCard()));
        grid.add(envolverCard("Folha salarial", dashFolha = labelCard()));
        grid.add(envolverCard("Média salarial", dashMedia = labelCard()));
        grid.add(envolverCard("Funcionário mais velho", dashMaisVelho = labelCard()));
        grid.add(envolverCard("Aniversariantes (mês)", dashAniv = labelCard()));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel mesLinha = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        mesLinha.setOpaque(false);
        mesLinha.add(new JLabel("Mês referência (dashboard / export):"));
        comboMesDashboard = new JComboBox<>();
        for (int m = 1; m <= 12; m++) comboMesDashboard.addItem(String.valueOf(m));
        comboMesDashboard.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        comboMesDashboard.addActionListener(e -> atualizarDashboard());
        mesLinha.add(comboMesDashboard);

        top.add(grid, BorderLayout.CENTER);
        top.add(mesLinha, BorderLayout.SOUTH);
        return top;
    }

    private JLabel labelCard() {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    private JPanel envolverCard(String titulo, JLabel valor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(ACCENT);
        card.add(t, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarBarraFiltros() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setOpaque(false);
        bar.add(new JLabel("Busca (nome):"));
        buscaNomeField = criarCampoTexto("contém…");
        buscaNomeField.setColumns(18);
        bar.add(buscaNomeField);

        bar.add(new JLabel("Função:"));
        comboFuncaoFiltro = new JComboBox<>();
        comboFuncaoFiltro.addItem("Todos");
        bar.add(comboFuncaoFiltro);

        bar.add(new JLabel("Mês nasc.:"));
        comboMesFiltro = new JComboBox<>();
        comboMesFiltro.addItem("Todos");
        for (int m = 1; m <= 12; m++) comboMesFiltro.addItem(String.valueOf(m));
        bar.add(comboMesFiltro);

        JButton b1 = criarBotaoEstilizado("Aplicar filtros", ACCENT, ACCENT_HOVER);
        b1.addActionListener(e -> {
            atualizarTabela();
            atualizarDashboard();
        });
        bar.add(b1);
        JButton b2 = criarBotaoEstilizado("Ordenar A-Z", SUCCESS, SUCCESS_HOVER);
        b2.addActionListener(e -> ordenarGrade(Comparator.comparing(FuncionarioDTO::getNome, String.CASE_INSENSITIVE_ORDER)));
        bar.add(b2);
        JButton b3 = criarBotaoEstilizado("Ordenar Z-A", SUCCESS, SUCCESS_HOVER);
        b3.addActionListener(e -> ordenarGrade(Comparator.comparing(FuncionarioDTO::getNome, String.CASE_INSENSITIVE_ORDER).reversed()));
        bar.add(b3);

        return bar;
    }

    private void ordenarGrade(Comparator<FuncionarioDTO> cmp) {
        List<FuncionarioDTO> rows = coletarLinhasAtuais();
        rows.sort(cmp);
        preencherModelo(rows);
    }

    private List<FuncionarioDTO> coletarLinhasAtuais() {
        List<FuncionarioDTO> list = new ArrayList<>();
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            int id = (int) tableModel.getValueAt(r, 0);
            Funcionario f = controller.buscarPorId(id);
            if (f != null) list.add(controller.toDTO(f));
        }
        return list;
    }

    private void preencherModelo(List<FuncionarioDTO> rows) {
        tableModel.setRowCount(0);
        for (FuncionarioDTO f : rows) {
            tableModel.addRow(new Object[]{
                    f.getId(), f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(),
                    f.getFuncao(), f.getIdade(), f.getSalariosMinimos()
            });
        }
    }

    private JPanel criarPainelTabela() {
        JPanel card = criarCard("📋 Quadro de pessoal (DTO)");

        String[] colunas = {"ID", "Nome", "Data Nasc.", "Salário (R$)", "Função", "Idade", "SM"};
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

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnPanel.setOpaque(false);
        JButton btnEditar = criarBotaoEstilizado("✏ Editar", WARNING, WARNING_HOVER);
        btnEditar.addActionListener(e -> acaoEditar());
        JButton btnRemover = criarBotaoEstilizado("✕ Remover", DANGER, DANGER_HOVER);
        btnRemover.addActionListener(e -> acaoRemover());
        JButton btnAtualizar = criarBotaoEstilizado("↻ Atualizar", ACCENT, ACCENT_HOVER);
        btnAtualizar.addActionListener(e -> { atualizarCombosFuncao(); atualizarTabela(); atualizarDashboard(); });
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
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(center);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center);
        tabela.getColumnModel().getColumn(5).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        tabela.getColumnModel().getColumn(3).setCellRenderer(right);
        tabela.getColumnModel().getColumn(6).setCellRenderer(right);
    }

    private JPanel criarPainelFormulario() {
        JPanel card = criarCard("➕ Cadastro");
        card.setPreferredSize(new Dimension(300, 0));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(5, 5, 5, 5));

        nomeField = criarCampoTexto("Nome");
        dataNascField = criarCampoTexto("dd/mm/aaaa");
        salarioField = criarCampoTexto("Ex: 2500.00");
        funcaoField = criarCampoTexto("Função");

        form.add(criarGrupoFormulario("Nome:", nomeField));
        form.add(Box.createVerticalStrut(8));
        form.add(criarGrupoFormulario("Data nascimento:", dataNascField));
        form.add(Box.createVerticalStrut(8));
        form.add(criarGrupoFormulario("Salário:", salarioField));
        form.add(Box.createVerticalStrut(8));
        form.add(criarGrupoFormulario("Função:", funcaoField));
        form.add(Box.createVerticalStrut(14));

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

    private JPanel criarPainelOperacoes() {
        JPanel card = criarCard("🔧 Operações do teste");
        card.setPreferredSize(new Dimension(300, 0));

        JPanel ops = new JPanel();
        ops.setLayout(new BoxLayout(ops, BoxLayout.Y_AXIS));
        ops.setOpaque(false);
        ops.setBorder(new EmptyBorder(4, 4, 4, 4));

        ops.add(linhaBotao("Inserir Funcionários", e -> acaoInserirPadrao()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Remover João", e -> acaoRemoverJoao()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Listar todos", e -> operacaoListar()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Aplicar Aumento", e -> operacaoAumento()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Agrupar por Função", e -> operacaoAgrupar()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Aniversariantes", e -> operacaoAniversariantes()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Mais Velho", e -> operacaoMaisVelho()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Ordenar", e -> operacaoOrdenar()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Folha Salarial", e -> operacaoFolha()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Salários Mínimos", e -> operacaoSM()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Dashboard (texto)", e -> operacaoDashboardTexto()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Exportar CSV…", e -> acaoExportarCsv()));
        ops.add(Box.createVerticalStrut(4));
        ops.add(linhaBotao("Exportar PDF…", e -> acaoExportarPdf()));

        JScrollPane sp = new JScrollPane(ops);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private JButton linhaBotao(String texto, java.awt.event.ActionListener a) {
        JButton b = criarBotaoOperacao(texto, a);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    private JPanel criarPainelResultado() {
        JPanel card = criarCard("📄 Resultado");

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(25, 25, 35));
        outputArea.setForeground(new Color(200, 220, 200));
        outputArea.setCaretColor(ACCENT);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        JButton btnLimpar = criarBotaoEstilizado("🧹 Limpar", new Color(100, 100, 115), new Color(130, 130, 145));
        btnLimpar.addActionListener(e -> outputArea.setText(""));

        JPanel barraInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraInferior.setOpaque(false);
        barraInferior.add(btnLimpar);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(barraInferior, BorderLayout.SOUTH);
        return card;
    }

    private void atualizarCombosFuncao() {
        String sel = (String) comboFuncaoFiltro.getSelectedItem();
        comboFuncaoFiltro.removeAllItems();
        comboFuncaoFiltro.addItem("Todos");
        for (String f : controller.listarFuncoesDistintas()) {
            comboFuncaoFiltro.addItem(f);
        }
        if (sel != null) {
            for (int i = 0; i < comboFuncaoFiltro.getItemCount(); i++) {
                if (sel.equals(comboFuncaoFiltro.getItemAt(i))) {
                    comboFuncaoFiltro.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void atualizarTabela() {
        atualizarCombosFuncao();
        String nome = buscaNomeField.getText().trim();
        String fn = (String) comboFuncaoFiltro.getSelectedItem();
        String ms = (String) comboMesFiltro.getSelectedItem();
        Integer mes = "Todos".equals(ms) || ms == null ? null : Integer.parseInt(ms);

        List<FuncionarioDTO> rows = controller.filtrarCombinado(nome, fn, mes);
        preencherModelo(rows);
    }

    private void atualizarDashboard() {
        int mes = Integer.parseInt((String) comboMesDashboard.getSelectedItem());
        DashboardDTO d = controller.construirDashboard(mes);
        dashTotal.setText(String.valueOf(d.getTotalFuncionarios()));
        dashFolha.setText("R$ " + d.getTotalFolha());
        dashMedia.setText("R$ " + d.getMediaSalarial());
        if (d.getMaisVelho() != null) {
            dashMaisVelho.setText(d.getMaisVelho().getNome() + " (" + d.getMaisVelho().getIdade() + " a.)");
        } else {
            dashMaisVelho.setText("—");
        }
        StringBuilder an = new StringBuilder();
        for (FuncionarioDTO f : d.getAniversariantesMes()) {
            if (an.length() > 0) an.append(", ");
            an.append(f.getNome());
        }
        dashAniv.setText(an.length() == 0 ? "—" : an.toString());
    }

    private void acaoSalvar() {
        String nome = nomeField.getText().trim();
        String dataNascStr = dataNascField.getText().trim();
        String salarioStr = salarioField.getText().trim();
        String funcao = funcaoField.getText().trim();

        if (nome.isEmpty() || dataNascStr.isEmpty() || salarioStr.isEmpty() || funcao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate dataNasc;
        try {
            dataNasc = FormatUtil.parsearData(dataNascStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida (dd/mm/aaaa).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal salario;
        try {
            salario = FormatUtil.parsearValor(salarioStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Salário inválido.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editandoId > 0) {
            controller.atualizar(editandoId, nome, dataNasc, salario, funcao);
            exibirResultado("✓ Atualizado.\n");
        } else {
            Funcionario f = controller.inserir(nome, dataNasc, salario, funcao);
            FuncionarioDTO dto = controller.toDTO(f);
            exibirResultado(String.format("✓ Inserido: %s | %s | R$ %s%n", dto.getNome(), dto.getDataNascimentoFormatada(), dto.getSalarioFormatado()));
        }
        limparFormulario();
        atualizarTabela();
        atualizarDashboard();
    }

    private void acaoEditar() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma linha.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Funcionario f = controller.buscarPorId(id);
        if (f == null) return;
        editandoId = id;
        nomeField.setText(f.getNome());
        dataNascField.setText(FormatUtil.formatarData(f.getDataNascimento()));
        salarioField.setText(f.getSalario().toPlainString());
        funcaoField.setText(f.getFuncao());
        btnSalvar.setText("💾 Atualizar");
    }

    private void acaoRemover() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma linha.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String nome = (String) tableModel.getValueAt(row, 1);
        int c = JOptionPane.showConfirmDialog(this, "Remover " + nome + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            controller.remover(id);
            exibirResultado("✓ Removido.\n");
            atualizarTabela();
            atualizarDashboard();
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

    private void acaoInserirPadrao() {
        controller.removerTodos();
        List<Funcionario> ins = controller.inserirFuncionariosPadrao();
        StringBuilder sb = new StringBuilder("Inseridos (DTO):\n");
        for (Funcionario f : ins) {
            FuncionarioDTO d = controller.toDTO(f);
            sb.append(String.format("  %s | R$ %s | %s%n", d.getNome(), d.getSalarioFormatado(), d.getFuncao()));
        }
        exibirResultado(sb.toString());
        atualizarTabela();
        atualizarDashboard();
    }

    private void acaoRemoverJoao() {
        controller.removerPorNome("João");
        exibirResultado("✓ 'João' removido (se existia).\n");
        atualizarTabela();
        atualizarDashboard();
    }

    private void acaoExecutarTodos() {
        int c = JOptionPane.showConfirmDialog(this,
                "Executar demonstração 3.1–3.12? Limpa o banco, reinsere dados, remove João e aplica aumento.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        exibirResultado(Principal.executarRequisitosTexto(controller));
        atualizarTabela();
        atualizarDashboard();
    }

    private void acaoLimparBanco() {
        int c = JOptionPane.showConfirmDialog(this, "Remover todos os registros?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            controller.removerTodos();
            exibirResultado("✓ Banco limpo.\n");
            atualizarTabela();
            atualizarDashboard();
        }
    }

    private void operacaoListar() {
        StringBuilder sb = new StringBuilder("Lista (DTO):\n");
        for (FuncionarioDTO f : controller.listarTodosDTO()) {
            sb.append(String.format("  %s | %s | R$ %s | %s | %d a. | %s SM%n",
                    f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(), f.getFuncao(), f.getIdade(), f.getSalariosMinimos()));
        }
        exibirResultado(sb.toString());
    }

    private void operacaoAumento() {
        List<FuncionarioDTO> antes = controller.listarTodosDTO();
        controller.aplicarAumento(new BigDecimal("10"));
        List<FuncionarioDTO> depois = controller.listarTodosDTO();
        StringBuilder sb = new StringBuilder("Aumento 10% (DTO):\n");
        for (int i = 0; i < antes.size(); i++) {
            sb.append(String.format("  %s: %s → %s%n", depois.get(i).getNome(), antes.get(i).getSalarioFormatado(), depois.get(i).getSalarioFormatado()));
        }
        exibirResultado(sb.toString());
        atualizarTabela();
        atualizarDashboard();
    }

    private void operacaoAgrupar() {
        StringBuilder sb = new StringBuilder("Agrupamento (DTO):\n");
        for (var e : controller.agruparPorFuncaoDTO().entrySet()) {
            sb.append("  ").append(e.getKey()).append(":\n");
            for (FuncionarioDTO f : e.getValue()) {
                sb.append(String.format("    • %s — R$ %s%n", f.getNome(), f.getSalarioFormatado()));
            }
        }
        exibirResultado(sb.toString());
    }

    private void operacaoAniversariantes() {
        StringBuilder sb = new StringBuilder("Aniversariantes meses 10 e 12 (DTO):\n");
        for (FuncionarioDTO f : controller.buscarAniversariantesDTO(10, 12)) {
            sb.append(String.format("  • %s — %s%n", f.getNome(), f.getDataNascimentoFormatada()));
        }
        exibirResultado(sb.toString());
    }

    private void operacaoMaisVelho() {
        FuncionarioDTO m = controller.buscarMaisVelhoDTO();
        exibirResultado(m == null ? "—\n" : String.format("Mais velho (DTO): %s — %d anos%n", m.getNome(), m.getIdade()));
    }

    private void operacaoOrdenar() {
        StringBuilder sb = new StringBuilder("Ordem alfabética A-Z (DTO):\n");
        int i = 1;
        for (FuncionarioDTO f : controller.listarPorOrdemAlfabeticaDTO()) {
            sb.append(String.format("  %2d. %s%n", i++, f.getNome()));
        }
        exibirResultado(sb.toString());
    }

    private void operacaoFolha() {
        exibirResultado(String.format("Total da folha: R$ %s%n", FormatUtil.formatarValor(controller.calcularTotalSalarios())));
    }

    private void operacaoSM() {
        StringBuilder sb = new StringBuilder("Salários mínimos (DTO):\n");
        for (FuncionarioDTO f : controller.listarTodosDTO()) {
            sb.append(String.format("  %s: %s SM%n", f.getNome(), f.getSalariosMinimos()));
        }
        exibirResultado(sb.toString());
    }

    private void operacaoDashboardTexto() {
        int mes = Integer.parseInt((String) comboMesDashboard.getSelectedItem());
        DashboardDTO d = controller.construirDashboard(mes);
        StringBuilder sb = new StringBuilder();
        sb.append("Dashboard (mês ").append(mes).append(")\n");
        sb.append("Total funcionários: ").append(d.getTotalFuncionarios()).append("\n");
        sb.append("Folha: R$ ").append(d.getTotalFolha()).append("\n");
        sb.append("Média: R$ ").append(d.getMediaSalarial()).append("\n");
        if (d.getMaisVelho() != null) {
            sb.append("Mais velho: ").append(d.getMaisVelho().getNome()).append("\n");
        }
        sb.append("Aniversariantes:\n");
        for (FuncionarioDTO f : d.getAniversariantesMes()) {
            sb.append("  • ").append(f.getNome()).append("\n");
        }
        exibirResultado(sb.toString());
    }

    private void acaoExportarCsv() {
        int mes = Integer.parseInt((String) comboMesDashboard.getSelectedItem());
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File("relatorio_rh.csv"));
        ch.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.exportarCsv(ch.getSelectedFile().toPath(), mes);
                exibirResultado("CSV exportado: " + ch.getSelectedFile().getAbsolutePath() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoExportarPdf() {
        int mes = Integer.parseInt((String) comboMesDashboard.getSelectedItem());
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File("relatorio_rh.pdf"));
        ch.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.exportarPdf(ch.getSelectedFile().toPath(), mes);
                exibirResultado("PDF exportado: " + ch.getSelectedFile().getAbsolutePath() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exibirResultado(String texto) {
        outputArea.append(texto + (texto.endsWith("\n") ? "" : "\n"));
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private JPanel criarCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)));
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
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
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
                new EmptyBorder(8, 10, 8, 10)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ACCENT.darker().darker());
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT),
                        new EmptyBorder(8, 10, 8, 10)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_INPUT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(8, 10, 8, 10)));
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
                new EmptyBorder(6, 10, 6, 10)));
        campo.setPreferredSize(new Dimension(0, 34));
        campo.putClientProperty("JTextField.placeholderText", placeholder);
        return campo;
    }
}
