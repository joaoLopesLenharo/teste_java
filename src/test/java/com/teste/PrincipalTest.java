package com.teste;

import com.teste.controller.FuncionarioController;
import com.teste.support.DatabaseTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Garante que a sequência original do enunciado executa sem exceção (saída via DTO).
 */
class PrincipalTest {

    @Test
    void executarRequisitos_sequenciaOriginal() throws Exception {
        DatabaseTestSupport.resetWithTempFile();
        FuncionarioController controller = new FuncionarioController();
        Principal.executarRequisitos(controller);
        assertTrue(controller.existemRegistros());
    }

    @Test
    void executarRequisitosTexto_naoVazio() throws Exception {
        DatabaseTestSupport.resetWithTempFile();
        FuncionarioController controller = new FuncionarioController();
        String t = Principal.executarRequisitosTexto(controller);
        assertTrue(t.contains("3.1"));
        assertTrue(t.contains("3.12"));
    }
}
