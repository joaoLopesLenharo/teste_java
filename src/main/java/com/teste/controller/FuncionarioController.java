package com.teste.controller;

import com.teste.dao.FuncionarioDAO;
import com.teste.model.Funcionario;
import com.teste.util.FormatUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller na arquitetura MVC.
 * Contém toda a lógica de negócio e mediação entre View e Model/DAO.
 * Nenhuma lógica de apresentação ou acesso a dados direto está aqui,
 * apenas orquestração.
 */
public class FuncionarioController {

    private final FuncionarioDAO dao;

    /** Salário mínimo considerado: R$ 1.212,00 */
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");

    public FuncionarioController() {
        this.dao = new FuncionarioDAO();
    }

    // ═══════════════════════════════════════════════════════════
    //  CRUD
    // ═══════════════════════════════════════════════════════════

    /**
     * Insere um novo funcionário.
     *
     * @return o funcionário inserido com ID gerado
     */
    public Funcionario inserir(String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        Funcionario f = new Funcionario(nome, dataNascimento, salario, funcao);
        dao.inserir(f);
        return f;
    }

    /**
     * Atualiza um funcionário existente.
     */
    public void atualizar(int id, String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        Funcionario f = new Funcionario(id, nome, dataNascimento, salario, funcao);
        dao.atualizar(f);
    }

    /**
     * Remove um funcionário pelo ID.
     */
    public void remover(int id) {
        dao.remover(id);
    }

    /**
     * Remove um funcionário pelo nome.
     */
    public void removerPorNome(String nome) {
        dao.removerPorNome(nome);
    }

    /**
     * Busca um funcionário pelo ID.
     */
    public Funcionario buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    /**
     * Lista todos os funcionários cadastrados.
     */
    public List<Funcionario> listarTodos() {
        return dao.listarTodos();
    }

    /**
     * Remove todos os registros do banco.
     */
    public void removerTodos() {
        dao.removerTodos();
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.1 – Inserir funcionários padrão
    // ═══════════════════════════════════════════════════════════

    /**
     * Requisito 3.1 – Insere todos os funcionários da tabela padrão do teste.
     * O João é incluído normalmente na inserção.
     * A remoção do João (Requisito 3.2) é feita separadamente.
     *
     * @return lista dos funcionários inseridos
     */
    public List<Funcionario> inserirFuncionariosPadrao() {
        List<Funcionario> padrao = criarFuncionariosPadrao();
        for (Funcionario f : padrao) {
            dao.inserir(f);
        }
        return padrao;
    }

    /**
     * Cria a lista de funcionários conforme a tabela do teste prático.
     */
    public List<Funcionario> criarFuncionariosPadrao() {
        List<Funcionario> lista = new ArrayList<>();
        lista.add(new Funcionario("Maria", LocalDate.of(2000, 10, 18), new BigDecimal("2009.44"), "Operador"));
        lista.add(new Funcionario("João", LocalDate.of(1990, 5, 12), new BigDecimal("2284.38"), "Operador"));
        lista.add(new Funcionario("Caio", LocalDate.of(1961, 5, 2), new BigDecimal("9836.14"), "Coordenador"));
        lista.add(new Funcionario("Miguel", LocalDate.of(1988, 10, 14), new BigDecimal("19119.88"), "Diretor"));
        lista.add(new Funcionario("Alice", LocalDate.of(1995, 1, 5), new BigDecimal("2234.68"), "Recepcionista"));
        lista.add(new Funcionario("Heitor", LocalDate.of(1999, 11, 19), new BigDecimal("1582.72"), "Operador"));
        lista.add(new Funcionario("Arthur", LocalDate.of(1993, 3, 31), new BigDecimal("4071.84"), "Contador"));
        lista.add(new Funcionario("Laura", LocalDate.of(1994, 7, 8), new BigDecimal("3017.45"), "Gerente"));
        lista.add(new Funcionario("Heloísa", LocalDate.of(2003, 5, 24), new BigDecimal("1606.85"), "Eletricista"));
        lista.add(new Funcionario("Helena", LocalDate.of(1996, 9, 2), new BigDecimal("2799.93"), "Gerente"));
        return lista;
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.4 – Aumento de salário
    // ═══════════════════════════════════════════════════════════

    /**
     * Aplica aumento percentual no salário de todos os funcionários.
     *
     * @param percentual percentual de aumento (ex: 10 para 10%)
     */
    public void aplicarAumento(BigDecimal percentual) {
        dao.aplicarAumento(percentual);
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.5/3.6 – Agrupamento por função
    // ═══════════════════════════════════════════════════════════

    /**
     * Agrupa funcionários por função em um Map.
     *
     * @return Map com chave = função, valor = lista de funcionários
     */
    public Map<String, List<Funcionario>> agruparPorFuncao() {
        return listarTodos().stream()
                .collect(Collectors.groupingBy(Funcionario::getFuncao));
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.8 – Aniversariantes mês 10 e 12
    // ═══════════════════════════════════════════════════════════

    /**
     * Retorna funcionários que fazem aniversário nos meses especificados.
     */
    public List<Funcionario> buscarAniversariantes(int... meses) {
        Set<Integer> mesesSet = new HashSet<>();
        for (int m : meses) {
            mesesSet.add(m);
        }

        return listarTodos().stream()
                .filter(f -> mesesSet.contains(f.getDataNascimento().getMonthValue()))
                .toList();
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.9 – Funcionário mais velho
    // ═══════════════════════════════════════════════════════════

    /**
     * Retorna o funcionário com a maior idade (data de nascimento mais antiga).
     */
    public Funcionario buscarMaisVelho() {
        return listarTodos().stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento))
                .orElse(null);
    }

    /**
     * Calcula a idade de um funcionário em anos.
     */
    public int calcularIdade(Funcionario funcionario) {
        return Period.between(funcionario.getDataNascimento(), LocalDate.now()).getYears();
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.10 – Ordem alfabética
    // ═══════════════════════════════════════════════════════════

    /**
     * Retorna todos os funcionários ordenados por nome alfabeticamente.
     */
    public List<Funcionario> listarPorOrdemAlfabetica() {
        return listarTodos().stream()
                .sorted(Comparator.comparing(Funcionario::getNome))
                .toList();
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.11 – Total dos salários
    // ═══════════════════════════════════════════════════════════

    /**
     * Calcula o total dos salários de todos os funcionários.
     */
    public BigDecimal calcularTotalSalarios() {
        return listarTodos().stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ═══════════════════════════════════════════════════════════
    //  REQUISITO 3.12 – Salários mínimos
    // ═══════════════════════════════════════════════════════════

    /**
     * Calcula quantos salários mínimos um funcionário ganha.
     */
    public BigDecimal calcularSalariosMinimos(Funcionario funcionario) {
        return funcionario.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
    }

    /**
     * Retorna o valor do salário mínimo utilizado.
     */
    public BigDecimal getSalarioMinimo() {
        return SALARIO_MINIMO;
    }

    /**
     * Verifica se existem funcionários cadastrados.
     */
    public boolean existemRegistros() {
        return dao.existemRegistros();
    }
}
