package com.teste.repository;

import com.teste.model.Funcionario;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrato de persistência de funcionários (camada Repository).
 */
public interface FuncionarioRepository {

    void inserir(Funcionario funcionario);

    void atualizar(Funcionario funcionario);

    void remover(int id);

    void removerPorNome(String nome);

    Funcionario buscarPorId(int id);

    List<Funcionario> listarTodos();

    boolean existemRegistros();

    void removerTodos();

    void aplicarAumento(BigDecimal percentual);
}
