package com.teste.model;

import com.teste.util.FormatUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Classe Funcionário que estende a classe Pessoa.
 * Atributos adicionais: salário (BigDecimal) e função (String).
 * Requisito 2 do teste prático.
 *
 * Faz parte da camada Model na arquitetura MVC.
 */
public class Funcionario extends Pessoa {

    private int id; // ID para persistência no banco de dados
    private BigDecimal salario;
    private String funcao;

    public Funcionario() {
        super();
    }

    public Funcionario(String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        super(nome, dataNascimento);
        this.salario = salario;
        this.funcao = funcao;
    }

    public Funcionario(int id, String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        super(nome, dataNascimento);
        this.id = id;
        this.salario = salario;
        this.funcao = funcao;
    }

    // ── Getters e Setters ────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    /**
     * Retorna o salário formatado no padrão brasileiro.
     * Delega a formatação ao FormatUtil.
     */
    public String getSalarioFormatado() {
        return FormatUtil.formatarValor(salario);
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-12s | R$ %-12s | %s",
                getNome(),
                getDataNascimentoFormatada(),
                getSalarioFormatado(),
                funcao);
    }
}
