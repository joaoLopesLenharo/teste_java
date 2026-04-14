package com.teste.service;

import com.teste.dto.DashboardDTO;
import com.teste.dto.FuncionarioDTO;
import com.teste.model.Funcionario;
import com.teste.repository.FuncionarioRepository;
import com.teste.util.FormatUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Regras de negócio e montagem de DTOs para a camada de apresentação.
 */
public class FuncionarioService {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public Funcionario inserir(String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        Funcionario f = new Funcionario(nome, dataNascimento, salario, funcao);
        repository.inserir(f);
        return f;
    }

    public void atualizar(int id, String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        Funcionario f = new Funcionario(id, nome, dataNascimento, salario, funcao);
        repository.atualizar(f);
    }

    public void remover(int id) {
        repository.remover(id);
    }

    public void removerPorNome(String nome) {
        repository.removerPorNome(nome);
    }

    public Funcionario buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public List<Funcionario> listarTodos() {
        return repository.listarTodos();
    }

    public void removerTodos() {
        repository.removerTodos();
    }

    public List<Funcionario> inserirFuncionariosPadrao() {
        List<Funcionario> padrao = criarFuncionariosPadrao();
        for (Funcionario f : padrao) {
            repository.inserir(f);
        }
        return padrao;
    }

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

    public void aplicarAumento(BigDecimal percentual) {
        repository.aplicarAumento(percentual);
    }

    public Map<String, List<Funcionario>> agruparPorFuncao() {
        return listarTodos().stream().collect(Collectors.groupingBy(Funcionario::getFuncao));
    }

    public Map<String, List<FuncionarioDTO>> agruparPorFuncaoDTO() {
        Map<String, List<Funcionario>> map = agruparPorFuncao();
        Map<String, List<FuncionarioDTO>> out = new LinkedHashMap<>();
        for (Map.Entry<String, List<Funcionario>> e : map.entrySet()) {
            out.put(e.getKey(), toDTOs(e.getValue()));
        }
        return out;
    }

    public List<Funcionario> buscarAniversariantes(int... meses) {
        Set<Integer> mesesSet = new HashSet<>();
        for (int m : meses) {
            mesesSet.add(m);
        }
        return listarTodos().stream()
                .filter(f -> mesesSet.contains(f.getDataNascimento().getMonthValue()))
                .toList();
    }

    public List<FuncionarioDTO> buscarAniversariantesDTO(int... meses) {
        return toDTOs(buscarAniversariantes(meses));
    }

    public Funcionario buscarMaisVelho() {
        return listarTodos().stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento))
                .orElse(null);
    }

    public int calcularIdade(Funcionario funcionario) {
        return Period.between(funcionario.getDataNascimento(), LocalDate.now()).getYears();
    }

    public List<Funcionario> listarPorOrdemAlfabetica() {
        return listarTodos().stream()
                .sorted(Comparator.comparing(Funcionario::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Funcionario> listarPorOrdemAlfabeticaDesc() {
        return listarTodos().stream()
                .sorted(Comparator.comparing(Funcionario::getNome, String.CASE_INSENSITIVE_ORDER).reversed())
                .toList();
    }

    public BigDecimal calcularTotalSalarios() {
        return listarTodos().stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularSalariosMinimos(Funcionario funcionario) {
        return funcionario.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSalarioMinimo() {
        return SALARIO_MINIMO;
    }

    public boolean existemRegistros() {
        return repository.existemRegistros();
    }

    /**
     * Formatação e cálculos derivados para exibição (único ponto de montagem de {@link FuncionarioDTO}).
     */
    public FuncionarioDTO toDTO(Funcionario funcionario) {
        int idade = calcularIdade(funcionario);
        BigDecimal qtdSm = calcularSalariosMinimos(funcionario);
        return new FuncionarioDTO(
                funcionario.getId(),
                funcionario.getNome(),
                FormatUtil.formatarData(funcionario.getDataNascimento()),
                FormatUtil.formatarValor(funcionario.getSalario()),
                funcionario.getFuncao(),
                idade,
                FormatUtil.formatarValor(qtdSm)
        );
    }

    public List<FuncionarioDTO> toDTOs(List<Funcionario> funcionarios) {
        return funcionarios.stream().map(this::toDTO).toList();
    }

    public List<FuncionarioDTO> listarTodosDTO() {
        return toDTOs(listarTodos());
    }

    public List<FuncionarioDTO> filtrarPorNomeContendo(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            return listarTodosDTO();
        }
        String t = trecho.trim().toLowerCase(Locale.ROOT);
        return listarTodos().stream()
                .filter(f -> f.getNome().toLowerCase(Locale.ROOT).contains(t))
                .map(this::toDTO)
                .toList();
    }

    public List<FuncionarioDTO> filtrarPorFuncao(String funcao) {
        if (funcao == null || funcao.isBlank() || "Todos".equalsIgnoreCase(funcao)) {
            return listarTodosDTO();
        }
        return listarTodos().stream()
                .filter(f -> f.getFuncao().equalsIgnoreCase(funcao.trim()))
                .map(this::toDTO)
                .toList();
    }

    public List<FuncionarioDTO> aniversariantesPorMes(int mes) {
        return toDTOs(buscarAniversariantes(mes));
    }

    public List<FuncionarioDTO> listarPorOrdemAlfabeticaDTO() {
        return toDTOs(listarPorOrdemAlfabetica());
    }

    public List<FuncionarioDTO> listarPorOrdemAlfabeticaDescDTO() {
        return toDTOs(listarPorOrdemAlfabeticaDesc());
    }

    public FuncionarioDTO buscarMaisVelhoDTO() {
        Funcionario m = buscarMaisVelho();
        return m != null ? toDTO(m) : null;
    }

    /**
     * @param mesAniversariantes mês (1-12) para a lista de aniversariantes do dashboard
     */
    public DashboardDTO construirDashboard(int mesAniversariantes) {
        List<Funcionario> todos = listarTodos();
        int n = todos.size();
        BigDecimal total = calcularTotalSalarios();
        String totalFmt = FormatUtil.formatarValor(total);
        String mediaFmt;
        if (n == 0) {
            mediaFmt = FormatUtil.formatarValor(BigDecimal.ZERO);
        } else {
            BigDecimal media = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
            mediaFmt = FormatUtil.formatarValor(media);
        }
        FuncionarioDTO maisVelho = buscarMaisVelhoDTO();
        List<FuncionarioDTO> aniv = toDTOs(buscarAniversariantes(mesAniversariantes));
        return new DashboardDTO(n, totalFmt, mediaFmt, maisVelho, aniv, mesAniversariantes);
    }

    public Set<String> listarFuncoesDistintas() {
        return listarTodos().stream().map(Funcionario::getFuncao).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Filtros combinados para a grade (nome, função e mês de nascimento).
     *
     * @param mesNascimento {@code null} ignora filtro de mês
     */
    public List<FuncionarioDTO> filtrarCombinado(String nomeTrecho, String funcao, Integer mesNascimento) {
        return listarTodos().stream()
                .filter(f -> filtroNome(f, nomeTrecho))
                .filter(f -> filtroFuncao(f, funcao))
                .filter(f -> mesNascimento == null || f.getDataNascimento().getMonthValue() == mesNascimento)
                .map(this::toDTO)
                .toList();
    }

    private boolean filtroNome(Funcionario f, String trecho) {
        if (trecho == null || trecho.isBlank()) return true;
        return f.getNome().toLowerCase(Locale.ROOT).contains(trecho.trim().toLowerCase(Locale.ROOT));
    }

    private boolean filtroFuncao(Funcionario f, String funcao) {
        if (funcao == null || funcao.isBlank() || "Todos".equalsIgnoreCase(funcao)) return true;
        return f.getFuncao().equalsIgnoreCase(funcao.trim());
    }
}
