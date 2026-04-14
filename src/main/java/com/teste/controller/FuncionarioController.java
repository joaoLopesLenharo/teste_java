package com.teste.controller;

import com.teste.dao.FuncionarioDAO;
import com.teste.dto.DashboardDTO;
import com.teste.dto.FuncionarioDTO;
import com.teste.model.Funcionario;
import com.teste.service.FuncionarioService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fachada MVC: delega regras ao {@link FuncionarioService} e exportações aos utilitários.
 * Usada de forma idêntica pela GUI e pelo terminal.
 */
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController() {
        this(new FuncionarioService(new FuncionarioDAO()));
    }

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    public FuncionarioService getService() {
        return service;
    }

    public Funcionario inserir(String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        return service.inserir(nome, dataNascimento, salario, funcao);
    }

    public void atualizar(int id, String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        service.atualizar(id, nome, dataNascimento, salario, funcao);
    }

    public void remover(int id) {
        service.remover(id);
    }

    public void removerPorNome(String nome) {
        service.removerPorNome(nome);
    }

    public Funcionario buscarPorId(int id) {
        return service.buscarPorId(id);
    }

    public List<Funcionario> listarTodos() {
        return service.listarTodos();
    }

    public void removerTodos() {
        service.removerTodos();
    }

    public List<Funcionario> inserirFuncionariosPadrao() {
        return service.inserirFuncionariosPadrao();
    }

    public List<Funcionario> criarFuncionariosPadrao() {
        return service.criarFuncionariosPadrao();
    }

    public void aplicarAumento(BigDecimal percentual) {
        service.aplicarAumento(percentual);
    }

    public Map<String, List<Funcionario>> agruparPorFuncao() {
        return service.agruparPorFuncao();
    }

    public Map<String, List<FuncionarioDTO>> agruparPorFuncaoDTO() {
        return service.agruparPorFuncaoDTO();
    }

    public List<Funcionario> buscarAniversariantes(int... meses) {
        return service.buscarAniversariantes(meses);
    }

    public List<FuncionarioDTO> buscarAniversariantesDTO(int... meses) {
        return service.buscarAniversariantesDTO(meses);
    }

    public Funcionario buscarMaisVelho() {
        return service.buscarMaisVelho();
    }

    public int calcularIdade(Funcionario funcionario) {
        return service.calcularIdade(funcionario);
    }

    public List<Funcionario> listarPorOrdemAlfabetica() {
        return service.listarPorOrdemAlfabetica();
    }

    public BigDecimal calcularTotalSalarios() {
        return service.calcularTotalSalarios();
    }

    public BigDecimal calcularSalariosMinimos(Funcionario funcionario) {
        return service.calcularSalariosMinimos(funcionario);
    }

    public BigDecimal getSalarioMinimo() {
        return service.getSalarioMinimo();
    }

    public boolean existemRegistros() {
        return service.existemRegistros();
    }

    // ── DTO e consultas de apresentação ─────────────────────────────

    public FuncionarioDTO toDTO(Funcionario f) {
        return service.toDTO(f);
    }

    public List<FuncionarioDTO> listarTodosDTO() {
        return service.listarTodosDTO();
    }

    public List<FuncionarioDTO> filtrarPorNomeContendo(String trecho) {
        return service.filtrarPorNomeContendo(trecho);
    }

    public List<FuncionarioDTO> filtrarPorFuncao(String funcao) {
        return service.filtrarPorFuncao(funcao);
    }

    public List<FuncionarioDTO> aniversariantesPorMes(int mes) {
        return service.aniversariantesPorMes(mes);
    }

    public List<FuncionarioDTO> listarPorOrdemAlfabeticaDTO() {
        return service.listarPorOrdemAlfabeticaDTO();
    }

    public List<FuncionarioDTO> listarPorOrdemAlfabeticaDescDTO() {
        return service.listarPorOrdemAlfabeticaDescDTO();
    }

    public FuncionarioDTO buscarMaisVelhoDTO() {
        return service.buscarMaisVelhoDTO();
    }

    public DashboardDTO construirDashboard(int mesAniversariantes) {
        return service.construirDashboard(mesAniversariantes);
    }

    public Set<String> listarFuncoesDistintas() {
        return service.listarFuncoesDistintas();
    }

    public List<FuncionarioDTO> filtrarCombinado(String nomeTrecho, String funcao, Integer mesNascimento) {
        return service.filtrarCombinado(nomeTrecho, funcao, mesNascimento);
    }

    public void exportarCsv(Path destino, int mesDashboard) throws IOException {
        com.teste.util.RelatorioExportUtil.exportarCsv(destino, service, mesDashboard);
    }

    public void exportarPdf(Path destino, int mesDashboard) throws IOException {
        com.teste.util.RelatorioExportUtil.exportarPdf(destino, service, mesDashboard);
    }
}
