package com.teste.util;

import com.teste.dto.DashboardDTO;
import com.teste.dto.FuncionarioDTO;
import com.teste.service.FuncionarioService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exportação de relatórios consolidados (CSV e PDF).
 */
public final class RelatorioExportUtil {

    private static final float PDF_MARGIN = 40;
    private static final float PDF_LINE_HEIGHT = 11;
    private static final float PDF_FONT_SIZE = 8;

    private RelatorioExportUtil() {
    }

    public static void exportarCsv(Path destino, FuncionarioService service, int mesDashboard) throws IOException {
        String texto = montarTextoRelatorio(service, mesDashboard);
        try (Writer w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            w.write('\ufeff');
            w.write(texto);
        }
    }

    public static void exportarPdf(Path destino, FuncionarioService service, int mesDashboard) throws IOException {
        List<String> linhas = quebrarLinhas(montarTextoRelatorio(service, mesDashboard));
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            float maxTextWidth = pageWidth - 2 * PDF_MARGIN;

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            cs.setFont(font, PDF_FONT_SIZE);
            float y = pageHeight - PDF_MARGIN;

            for (String linha : linhas) {
                if (y < PDF_MARGIN) {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    cs.setFont(font, PDF_FONT_SIZE);
                    y = pageHeight - PDF_MARGIN;
                }
                String chunk = truncarParaLarguraPdf(linha, maxTextWidth);
                cs.beginText();
                cs.newLineAtOffset(PDF_MARGIN, y);
                cs.showText(chunk);
                cs.endText();
                y -= PDF_LINE_HEIGHT;
            }
            cs.close();
            doc.save(destino.toFile());
        }
    }

    private static String truncarParaLarguraPdf(String linha, float maxWidth) {
        String s = sanitizarPdf(linha == null ? "" : linha);
        float approxChar = maxWidth / 5.5f;
        int maxChars = Math.max(10, (int) approxChar);
        if (s.length() <= maxChars) {
            return s;
        }
        return s.substring(0, maxChars - 3) + "...";
    }

    private static String sanitizarPdf(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c >= 32 && c <= 126) {
                b.append(c);
            } else {
                switch (c) {
                    case 'á', 'à', 'ã', 'â', 'ä' -> b.append('a');
                    case 'Á', 'À', 'Ã', 'Â', 'Ä' -> b.append('A');
                    case 'é', 'ê', 'ë' -> b.append('e');
                    case 'É', 'Ê', 'Ë' -> b.append('E');
                    case 'í', 'î', 'ï' -> b.append('i');
                    case 'Í', 'Î', 'Ï' -> b.append('I');
                    case 'ó', 'ô', 'õ', 'ö' -> b.append('o');
                    case 'Ó', 'Ô', 'Õ', 'Ö' -> b.append('O');
                    case 'ú', 'ü' -> b.append('u');
                    case 'Ú', 'Ü' -> b.append('U');
                    case 'ç' -> b.append('c');
                    case 'Ç' -> b.append('C');
                    case 'ñ' -> b.append('n');
                    case 'Ñ' -> b.append('N');
                    case 'º', '°' -> b.append('o');
                    case 'ª' -> b.append('a');
                    default -> b.append(' ');
                }
            }
        }
        return b.toString();
    }

    public static String montarTextoRelatorio(FuncionarioService service, int mesDashboard) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO RH — FUNCIONÁRIOS\n");
        sb.append("========================\n\n");

        sb.append("LISTA FORMATADA\n");
        sb.append("---------------\n");
        for (FuncionarioDTO f : service.listarTodosDTO()) {
            sb.append(String.format("%s | %s | R$ %s | %s | %d anos | %s SM%n",
                    f.getNome(), f.getDataNascimentoFormatada(), f.getSalarioFormatado(),
                    f.getFuncao(), f.getIdade(), f.getSalariosMinimos()));
        }
        sb.append("\n");

        sb.append("FOLHA SALARIAL\n");
        sb.append("--------------\n");
        sb.append("Total: R$ ").append(FormatUtil.formatarValor(service.calcularTotalSalarios())).append("\n\n");

        sb.append("AGRUPAMENTO POR FUNÇÃO\n");
        sb.append("----------------------\n");
        for (Map.Entry<String, List<FuncionarioDTO>> e : service.agruparPorFuncaoDTO().entrySet()) {
            sb.append(e.getKey()).append(":\n");
            for (FuncionarioDTO f : e.getValue()) {
                sb.append("  - ").append(f.getNome()).append(" (R$ ").append(f.getSalarioFormatado()).append(")\n");
            }
        }
        sb.append("\n");

        sb.append("ANIVERSARIANTES (MESES 10 E 12)\n");
        sb.append("--------------------------------\n");
        List<FuncionarioDTO> aniv1012 = service.buscarAniversariantesDTO(10, 12);
        for (FuncionarioDTO f : aniv1012) {
            sb.append("  - ").append(f.getNome()).append(" — ").append(f.getDataNascimentoFormatada()).append("\n");
        }
        if (aniv1012.isEmpty()) {
            sb.append("  (nenhum)\n");
        }
        sb.append("\n");

        DashboardDTO dash = service.construirDashboard(mesDashboard);
        sb.append("INDICADORES (DASHBOARD — ANIVERSARIANTES MÊS ").append(mesDashboard).append(")\n");
        sb.append("---------------------------------------------------\n");
        sb.append("Total de funcionários: ").append(dash.getTotalFuncionarios()).append("\n");
        sb.append("Folha salarial: R$ ").append(dash.getTotalFolha()).append("\n");
        sb.append("Média salarial: R$ ").append(dash.getMediaSalarial()).append("\n");
        if (dash.getMaisVelho() != null) {
            FuncionarioDTO mv = dash.getMaisVelho();
            sb.append("Mais velho: ").append(mv.getNome()).append(" (").append(mv.getIdade()).append(" anos)\n");
        } else {
            sb.append("Mais velho: —\n");
        }
        sb.append("Aniversariantes no mês ").append(mesDashboard).append(":\n");
        for (FuncionarioDTO f : dash.getAniversariantesMes()) {
            sb.append("  - ").append(f.getNome()).append(" — ").append(f.getDataNascimentoFormatada()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static List<String> quebrarLinhas(String texto) {
        List<String> out = new ArrayList<>();
        for (String linha : texto.split("\r?\n")) {
            out.add(linha.isEmpty() ? " " : linha);
        }
        return out;
    }
}
