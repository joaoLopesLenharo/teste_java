package com.teste.service;

import com.teste.dao.FuncionarioDAO;
import com.teste.dto.DashboardDTO;
import com.teste.dto.FuncionarioDTO;
import com.teste.model.Funcionario;
import com.teste.support.DatabaseTestSupport;
import com.teste.util.RelatorioExportUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    private FuncionarioService service;

    @BeforeEach
    void setup() throws Exception {
        DatabaseTestSupport.resetWithTempFile();
        service = new FuncionarioService(new FuncionarioDAO());
    }

    @Test
    void inserirPadrao_removerJoao_listaSemJoao() {
        service.removerTodos();
        service.inserirFuncionariosPadrao();
        assertTrue(service.listarTodos().stream().anyMatch(f -> "João".equals(f.getNome())));
        service.removerPorNome("João");
        assertTrue(service.listarTodos().stream().noneMatch(f -> "João".equals(f.getNome())));
    }

    @Test
    void aumentoDezPorcento() {
        service.removerTodos();
        service.inserir("Teste", LocalDate.of(1990, 1, 1), new BigDecimal("1000.00"), "Auxiliar");
        Funcionario f = service.listarTodos().get(0);
        service.aplicarAumento(new BigDecimal("10"));
        Funcionario depois = service.buscarPorId(f.getId());
        assertEquals(0, new BigDecimal("1100.00").compareTo(depois.getSalario()));
    }

    @Test
    void agrupamentoPorFuncao() {
        service.removerTodos();
        service.inserirFuncionariosPadrao();
        service.removerPorNome("João");
        Map<String, List<Funcionario>> g = service.agruparPorFuncao();
        assertTrue(g.containsKey("Operador"));
        assertTrue(g.get("Operador").size() >= 1);
    }

    @Test
    void funcionarioMaisVelho() {
        service.removerTodos();
        service.inserirFuncionariosPadrao();
        Funcionario m = service.buscarMaisVelho();
        assertNotNull(m);
        assertEquals("Caio", m.getNome());
    }

    @Test
    void totalFolhaESalariosMinimosDTO() {
        service.removerTodos();
        service.inserir("X", LocalDate.of(1995, 5, 5), new BigDecimal("2424.00"), "Cargo");
        BigDecimal total = service.calcularTotalSalarios();
        assertEquals(0, new BigDecimal("2424.00").compareTo(total));
        FuncionarioDTO dto = service.toDTO(service.listarTodos().get(0));
        assertEquals("2,00", dto.getSalariosMinimos());
    }

    @Test
    void filtrosCombinados() {
        service.removerTodos();
        service.inserirFuncionariosPadrao();
        List<FuncionarioDTO> op = service.filtrarCombinado("mar", "Operador", null);
        assertFalse(op.isEmpty());
        List<FuncionarioDTO> mes10 = service.filtrarCombinado("", "Todos", 10);
        assertFalse(mes10.isEmpty());
        assertTrue(mes10.stream().allMatch(d -> d.getDataNascimentoFormatada().contains("/10/")));
    }

    @Test
    void dashboardDTO() {
        service.removerTodos();
        service.inserirFuncionariosPadrao();
        DashboardDTO d = service.construirDashboard(10);
        assertTrue(d.getTotalFuncionarios() > 0);
        assertNotNull(d.getTotalFolha());
        assertNotNull(d.getMediaSalarial());
        assertNotNull(d.getMaisVelho());
    }

    @Test
    void toDTO_formatacao() {
        service.removerTodos();
        service.inserir("Ana", LocalDate.of(2000, 3, 15), new BigDecimal("1212.00"), "Dev");
        FuncionarioDTO dto = service.toDTO(service.listarTodos().get(0));
        assertEquals("Ana", dto.getNome());
        assertTrue(dto.getDataNascimentoFormatada().contains("03"));
        assertTrue(dto.getSalarioFormatado().contains(","));
        assertEquals("Dev", dto.getFuncao());
        assertTrue(dto.getIdade() > 0);
    }

    @Test
    void exportacaoCsvPdf() throws Exception {
        service.removerTodos();
        service.inserirFuncionariosPadrao();
        Path csv = Files.createTempFile("rep-", ".csv");
        Path pdf = Files.createTempFile("rep-", ".pdf");
        csv.toFile().deleteOnExit();
        pdf.toFile().deleteOnExit();
        RelatorioExportUtil.exportarCsv(csv, service, 10);
        RelatorioExportUtil.exportarPdf(pdf, service, 10);
        String c = Files.readString(csv);
        assertTrue(c.contains("RELATÓRIO"));
        assertTrue(Files.size(pdf) > 100);
    }
}
