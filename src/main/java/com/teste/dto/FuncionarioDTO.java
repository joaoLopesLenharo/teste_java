package com.teste.dto;

import java.util.Objects;

/**
 * DTO de apresentação para funcionário.
 * Toda formatação é responsabilidade do {@link com.teste.service.FuncionarioService#toDTO}.
 */
public final class FuncionarioDTO {

    /** Identificador no banco (necessário para telas de edição; -1 se ausente). */
    private final int id;
    private final String nome;
    private final String dataNascimentoFormatada;
    private final String salarioFormatado;
    private final String funcao;
    private final int idade;
    private final String salariosMinimos;

    public FuncionarioDTO(int id, String nome, String dataNascimentoFormatada, String salarioFormatado,
                          String funcao, int idade, String salariosMinimos) {
        this.id = id;
        this.nome = nome;
        this.dataNascimentoFormatada = dataNascimentoFormatada;
        this.salarioFormatado = salarioFormatado;
        this.funcao = funcao;
        this.idade = idade;
        this.salariosMinimos = salariosMinimos;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDataNascimentoFormatada() {
        return dataNascimentoFormatada;
    }

    public String getSalarioFormatado() {
        return salarioFormatado;
    }

    public String getFuncao() {
        return funcao;
    }

    public int getIdade() {
        return idade;
    }

    public String getSalariosMinimos() {
        return salariosMinimos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuncionarioDTO that = (FuncionarioDTO) o;
        return id == that.id
                && idade == that.idade
                && Objects.equals(nome, that.nome)
                && Objects.equals(dataNascimentoFormatada, that.dataNascimentoFormatada)
                && Objects.equals(salarioFormatado, that.salarioFormatado)
                && Objects.equals(funcao, that.funcao)
                && Objects.equals(salariosMinimos, that.salariosMinimos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, dataNascimentoFormatada, salarioFormatado, funcao, idade, salariosMinimos);
    }
}
