package com.fintrack.FinTrackV2.service;

import com.fintrack.FinTrackV2.model.Expense;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.List;

@Service

public class PdfService {

    public ByteArrayInputStream generatePdf(

            List<Expense> expenses,

            Double income,

            Double expense,

            Double balance){

        Document document =
                new Document();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        try{

            PdfWriter.getInstance(
                    document,
                    out);

            document.open();

            Font font =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18);

            Paragraph title =
                    new Paragraph(
                            "FinTrack Financial Report",
                            font);

            title.setAlignment(
                    Element.ALIGN_CENTER);

            document.add(title);

            document.add(
                    Chunk.NEWLINE);

            document.add(
                    new Paragraph(
                            "Total Income: ₹ " + income));

            document.add(
                    new Paragraph(
                            "Total Expense: ₹ " + expense));

            document.add(
                    new Paragraph(
                            "Balance: ₹ " + balance));

            document.add(
                    Chunk.NEWLINE);

            PdfPTable table =
                    new PdfPTable(4);

            table.addCell("Title");
            table.addCell("Category");
            table.addCell("Amount");
            table.addCell("Date");

            for(Expense e : expenses){

                table.addCell(
                        e.getTitle());

                table.addCell(
                        e.getCategory());

                table.addCell(
                        String.valueOf(
                                e.getAmount()));

                table.addCell(
                        String.valueOf(
                                e.getDate()));
            }

            document.add(table);

            document.close();

        }catch(Exception e){

            e.printStackTrace();
        }

        return new ByteArrayInputStream(
                out.toByteArray());
    }
}