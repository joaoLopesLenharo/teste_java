package com.teste.dao;

import com.teste.model.Funcionario;
import com.teste.support.DatabaseTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integração JDBC com SQLite (arquivo temporário).
 */
class FuncionarioDAOIntegrationTest {

    private FuncionarioDAO dao;

    @BeforeEach
    void setup() throws Exception {
        DatabaseTestSupport.resetWithTempFile();
        dao = new FuncionarioDAO();
    }

    @Test
    void crudBasico() {
        Funcionario f = new Funcionario("Zé", LocalDate.of(1980, 1, 2), new BigDecimal("3000.00"), "Mecânico");
        dao.inserir(f);
        assertTrue(f.getId() > 0);
        List<Funcionario> todos = dao.listarTodos();
        assertEquals(1, todos.size());
        assertEquals("Zé", todos.get(0).getNome());

        f.setSalario(new BigDecimal("3300.00"));
        dao.atualizar(f);
        assertEquals(0, new BigDecimal("3300.00").compareTo(dao.buscarPorId(f.getId()).getSalario()));

        dao.remover(f.getId());
        assertTrue(dao.listarTodos().isEmpty());
    }

    @Test
    void aplicarAumentoSql() {
        Funcionario f = new Funcionario("A", LocalDate.of(1990, 1, 1), new BigDecimal("1000.00"), "B");
        dao.inserir(f);
        dao.aplicarAumento(new BigDecimal("10"));
        Funcionario d = dao.buscarPorId(f.getId());
        assertEquals(0, new BigDecimal("1100.00").compareTo(d.getSalario()));
    }
}
