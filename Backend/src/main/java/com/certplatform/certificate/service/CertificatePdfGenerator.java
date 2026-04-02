package com.certplatform.certificate.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class CertificatePdfGenerator {

    public static String generateCertificatePdf(String studentName, String outputPath) throws IOException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs(); // ensure folder exists

        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        // Title
        Paragraph title = new Paragraph("Certificate of Completion")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Student name
        Paragraph student = new Paragraph("This is to certify that")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(student);

        Paragraph name = new Paragraph(studentName)
                .setFontSize(20)
                .setBold()
                .setFontColor(ColorConstants.BLUE)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(name);

        // Project details
        Paragraph project = new Paragraph("has successfully completed the project:")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(project);

        // Date
        Paragraph date = new Paragraph("Issued on: " + LocalDate.now())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(date);

        document.close();
        return outputPath;
    }
}

