package com.teste.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Classe utilitária para formatação de datas e valores numéricos
 * no padrão brasileiro conforme requisitos do teste.
 *
 * - Data: dd/MM/yyyy
 * - Valor numérico: separador de milhar (.) e decimal (,)
 */
public final class FormatUtil {

    /** Formato de data brasileiro: dd/MM/yyyy */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Formato monetário brasileiro: #.##0,00 */
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    private FormatUtil() {
        // Classe utilitária - não instanciar
    }

    /**
     * Formata uma data LocalDate para o padrão dd/MM/yyyy.
     */
    public static String formatarData(LocalDate data) {
        if (data == null) return "";
        return data.format(DATE_FORMAT);
    }

    /**
     * Formata um BigDecimal com separador de milhar (.) e decimal (,).
     */
    public static String formatarValor(BigDecimal valor) {
        if (valor == null) return "0,00";
        return DECIMAL_FORMAT.format(valor);
    }

    /**
     * Parseia uma string de data no formato dd/MM/yyyy para LocalDate.
     */
    public static LocalDate parsearData(String dataStr) {
        return LocalDate.parse(dataStr, DATE_FORMAT);
    }

    /**
     * Parseia uma string de valor numérico para BigDecimal.
     * Aceita formatos: "2500.00" ou "2.500,00"
     */
    public static BigDecimal parsearValor(String valorStr) {
        String limpo = valorStr.replace(".", "").replace(",", ".");
        return new BigDecimal(limpo);
    }
}
