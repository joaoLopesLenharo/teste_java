package com.teste.model;

import com.teste.util.FormatUtil;

import java.time.LocalDate;

/**
 * Classe Pessoa com os atributos nome e data de nascimento.
 * Requisito 1 do teste prático.
 *
 * Faz parte da camada Model na arquitetura MVC.
 */
public class Pessoa {

    private String nome;
    private LocalDate dataNascimento;

    public Pessoa() {
    }

    public Pessoa(String nome, LocalDate dataNascimento) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    // ── Getters e Setters ────────────────────────────────────

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Retorna a data de nascimento formatada no padrão dd/MM/yyyy.
     * Delega a formatação ao FormatUtil.
     */
    public String getDataNascimentoFormatada() {
        return FormatUtil.formatarData(dataNascimento);
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "nome='" + nome + '\'' +
                ", dataNascimento=" + getDataNascimentoFormatada() +
                '}';
    }
}
