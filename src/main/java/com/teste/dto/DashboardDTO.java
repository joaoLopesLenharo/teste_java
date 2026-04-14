package com.teste.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Indicadores agregados para dashboard (GUI e terminal).
 */
public final class DashboardDTO {

    private final int totalFuncionarios;
    private final String totalFolha;
    private final String mediaSalarial;
    private final FuncionarioDTO maisVelho;
    private final List<FuncionarioDTO> aniversariantesMes;
    private final int mesAniversariantesReferencia;

    public DashboardDTO(int totalFuncionarios, String totalFolha, String mediaSalarial,
                        FuncionarioDTO maisVelho, List<FuncionarioDTO> aniversariantesMes,
                        int mesAniversariantesReferencia) {
        this.totalFuncionarios = totalFuncionarios;
        this.totalFolha = totalFolha;
        this.mediaSalarial = mediaSalarial;
        this.maisVelho = maisVelho;
        this.aniversariantesMes = aniversariantesMes != null
                ? Collections.unmodifiableList(aniversariantesMes)
                : List.of();
        this.mesAniversariantesReferencia = mesAniversariantesReferencia;
    }

    public int getTotalFuncionarios() {
        return totalFuncionarios;
    }

    public String getTotalFolha() {
        return totalFolha;
    }

    public String getMediaSalarial() {
        return mediaSalarial;
    }

    public FuncionarioDTO getMaisVelho() {
        return maisVelho;
    }

    public List<FuncionarioDTO> getAniversariantesMes() {
        return aniversariantesMes;
    }

    public int getMesAniversariantesReferencia() {
        return mesAniversariantesReferencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardDTO that = (DashboardDTO) o;
        return totalFuncionarios == that.totalFuncionarios
                && mesAniversariantesReferencia == that.mesAniversariantesReferencia
                && Objects.equals(totalFolha, that.totalFolha)
                && Objects.equals(mediaSalarial, that.mediaSalarial)
                && Objects.equals(maisVelho, that.maisVelho)
                && Objects.equals(aniversariantesMes, that.aniversariantesMes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalFuncionarios, totalFolha, mediaSalarial, maisVelho, aniversariantesMes, mesAniversariantesReferencia);
    }
}
