package com.monthlystairbills.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.monthlystairbills.model.BillConfig;
import com.monthlystairbills.model.Recipient;
import com.monthlystairbills.util.CatalanNumberWords;

import java.io.IOException;
import java.nio.file.Path;

public class PdfGenerator {

    // Receipt dimensions: ~A5 landscape (210mm x 148mm) in points (1mm = 2.835pt)
    private static final float PAGE_WIDTH  = 595f; // ~210mm
    private static final float PAGE_HEIGHT = 210f; // ~74mm  (half A5)
    private static final float MARGIN      = 20f;

    private static final float FONT_SMALL  = 7f;
    private static final float FONT_NORMAL = 8f;
    private static final float FONT_BOLD   = 9f;

    public void generate(Path outputPath, int billNumber, BillConfig config, Recipient recipient) throws IOException {
        PageSize pageSize = new PageSize(PAGE_WIDTH, PAGE_HEIGHT);

        try (PdfWriter writer = new PdfWriter(outputPath.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, pageSize)) {

            doc.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

            // ── Header row: empty left + bill number box right ──────────────
            Table header = new Table(UnitValue.createPercentArray(new float[]{65, 35}))
                    .setWidth(UnitValue.createPercentValue(100));

            header.addCell(emptyCell());
            header.addCell(cell(String.format("REBUT N\u00BA %d / %d", billNumber, config.getYear()), FONT_BOLD, TextAlignment.CENTER)
                    .setBorder(new SolidBorder(1f)));

            doc.add(header);

            // ── Date line ───────────────────────────────────────────────────
            String dateLine = String.format("%s,   %d   %s   %d",
                    config.getCommunityCity(),
                    config.getDay(),
                    CatalanNumberWords.monthName(config.getMonth()),
                    config.getYear());

            doc.add(new Paragraph(dateLine)
                    .setFontSize(FONT_NORMAL)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(4f)
                    .setMarginBottom(2f));

            // ── Main content table ──────────────────────────────────────────
            Table body = new Table(UnitValue.createPercentArray(new float[]{30, 55, 15}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBorder(new SolidBorder(0.5f));

            // Row 1: Name
            String floor = CatalanNumberWords.floorOrdinal(recipient.getFloor()) + " pis";
            body.addCell(labelCell("Rebut de Sr./Sra."));
            body.addCell(valueCell(recipient.getName().toUpperCase()));
            body.addCell(valueCell(floor).setTextAlignment(TextAlignment.RIGHT));

            // Row 2: Amount in words
            body.addCell(labelCell("Quantitat en euros"));
            body.addCell(valueCellSpan2(CatalanNumberWords.toWords(recipient.getQuantityInEuros())));

            // Row 3: Concept
            body.addCell(labelCell("Concepte"));
            body.addCell(valueCellSpan2(recipient.getConcept().toUpperCase()));

            doc.add(body);

            // ── Footer: euros box left + community name right ───────────────
            Table footer = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(4f);

            footer.addCell(cell(String.format("Euros     %s \u20AC", CatalanNumberWords.formatAmount(recipient.getQuantityInEuros())),
                    FONT_NORMAL, TextAlignment.LEFT)
                    .setBorder(new SolidBorder(0.5f))
                    .setPadding(3f));

            footer.addCell(cell(config.getCommunityName(), FONT_SMALL, TextAlignment.RIGHT)
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderLeft(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.BOTTOM));

            doc.add(footer);
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private Cell cell(String text, float fontSize, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(fontSize).setTextAlignment(alignment))
                .setBorder(Border.NO_BORDER)
                .setPadding(2f);
    }

    private Cell labelCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(FONT_SMALL))
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(new SolidBorder(0.5f))
                .setBorderBottom(Border.NO_BORDER)
                .setPadding(2f);
    }

    private Cell valueCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(FONT_NORMAL))
                .setBorder(Border.NO_BORDER)
                .setPadding(2f);
    }

    /** Cell spanning 2 columns (rowspan=1, colspan=2) */
    private Cell valueCellSpan2(String text) {
        return new Cell(1, 2)
                .add(new Paragraph(text).setFontSize(FONT_NORMAL))
                .setBorder(Border.NO_BORDER)
                .setPadding(2f);
    }

    private Cell emptyCell() {
        return new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER);
    }
}
