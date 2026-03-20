import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.util.Calendar;
import java.util.List;

public class PDF_demo {

    // ČÍTANIE – extrakcia textu (Vitalii)
    public static void readPDF(String path) throws IOException {

        try (PDDocument doc = PDDocument.load(new File(path))) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // text v správnom poradí
            String text = stripper.getText(doc);

            System.out.println(text);
        }
    }

    // POČET STRÁN A ROZMERY (Vitalii)
    public static void infoPDF(String cesta) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(cesta))) {

            System.out.println("Počet strán: " + doc.getNumberOfPages());

            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                PDPage strana = doc.getPage(i);
                PDRectangle rozmer = strana.getMediaBox();

                System.out.printf(
                        "Strana %d: %.0f x %.0f bodov  /  %.1f x %.1f cm%n",
                        i + 1,
                        rozmer.getWidth(),
                        rozmer.getHeight(),
                        rozmer.getWidth() / 72 * 2.54,  // body -> cm
                        rozmer.getHeight() / 72 * 2.54
                );
            }
        }
    }

    // METADÁTA (Vitalii)
    public static void metadataPDF(String vstup, String vystup) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(vstup))) {

            PDDocumentInformation info = doc.getDocumentInformation();
            info.setTitle("Výročná správa 2024");
            info.setAuthor("Ján Novák");
            info.setSubject("Finančné výsledky");
            info.setKeywords("správa, 2024, financie");
            info.setCreationDate(Calendar.getInstance());

            doc.save(vystup);
            System.out.println("Metadáta uložené.");
        }
    }

    // ČÍTANIE METADÁT (Vitalii)
    public static void readMetadataPDF(String cesta) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(cesta))) {

            PDDocumentInformation info = doc.getDocumentInformation();
            System.out.println("Názov:   " + info.getTitle());
            System.out.println("Autor:   " + info.getAuthor());
            System.out.println("Predmet: " + info.getSubject());
            System.out.println("Kľúčové slová: " + info.getKeywords());
            System.out.println("Vytvorené: " + info.getCreationDate().getTime());
        }
    }

    // Vytvorenie PDF s rôznymi efektmi (Arseniy)
    public static void makePDF(String path) throws IOException {
        try (PDDocument doc = new PDDocument()) {

            // Prvá stránka
            PDPage strana1 = new PDPage(PDRectangle.A4);
            doc.addPage(strana1);

            try (PDPageContentStream cs = new PDPageContentStream(doc, strana1)) {

                // Červený text
                cs.setNonStrokingColor(Color.RED);
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(50, 750);
                cs.showText("Ahoj zo Java PDF!");
                cs.endText();

                // Viacriadkový text
                cs.setNonStrokingColor(Color.BLACK);
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, 720);
                String[] lines = {"Prvý riadok textu", "Druhý riadok textu", "Tretí riadok textu"};
                for (String line : lines) {
                    cs.showText(line);
                    cs.newLineAtOffset(0, -15);
                }
                cs.endText();

                // Modrý rámik okolo textu
                cs.setStrokingColor(Color.BLUE);
                cs.addRect(45, 685, 200, 50);
                cs.stroke();

                // Obrázok
                PDImageXObject image = PDImageXObject.createFromFile("image.png", doc);
                cs.drawImage(image, 50, 500, 200, 150);

                // Bold + italic text
                cs.setNonStrokingColor(Color.MAGENTA);
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD_OBLIQUE, 14);
                cs.newLineAtOffset(50, 650);
                cs.showText("Tu je text bold+italic");
                cs.endText();
            }

            // Druhá stránka
            PDPage strana2 = new PDPage(PDRectangle.A4);
            doc.addPage(strana2);

            try (PDPageContentStream cs2 = new PDPageContentStream(doc, strana2)) {
                cs2.setNonStrokingColor(Color.DARK_GRAY);
                cs2.beginText();
                cs2.setFont(PDType1Font.COURIER, 12);
                cs2.newLineAtOffset(50, 750);
                cs2.showText("Toto je text na druhej strane");
                cs2.endText();
            }

            // Uloženie dokumentu
            doc.save(path);
        }
    }

    // Úprava existujúceho PDF – pridanie textu (Arseniy)
    public static void changePDF(String vstup, String vystup) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(vstup))) {
            PDPage strana = doc.getPage(0); // prvá strana

            try (PDPageContentStream cs = new PDPageContentStream(
                    doc, strana, PDPageContentStream.AppendMode.APPEND, true)) {
                cs.setNonStrokingColor(Color.BLUE);
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, 600);
                cs.showText("Tento text bol pridany neskor.");
                cs.endText();
            }

            doc.save(vystup);
        }
    }

    // OCHRANA PDF HESLOM A NASTAVENIE PRAV (Andrii)
    public static void passwordPDF(String input, String output) throws IOException {

        try (PDDocument doc = PDDocument.load(new File(input))) {

            AccessPermission permission = new AccessPermission(); // valid only for user
            permission.setCanPrint(false);
            permission.setCanModify(false);

            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                    "admin",   // permissions do not apply
                    "user",                 // must follow the permissions
                    permission
            );
            policy.setEncryptionKeyLength(256);

            doc.protect(policy);
            doc.save(output);

            System.out.println("Súbor chránený heslom: " + output);
        }
    }

    // ZLUČENIE PDF SÚBOROV (Andrii)
    public static void mergePdf() throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.addSource("test.pdf");
        merger.addSource("upraveny.pdf");
        merger.setDestinationFileName("merged.pdf");
        merger.mergeDocuments(null);
    }

    // ZLUČENIE PDF SÚBOROV (Andrii)
    public static void splitPdf() throws IOException {
        try (PDDocument doc = PDDocument.load(new File("upraveny.pdf"))) {

            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(doc);
            int i = 1;
            for (PDDocument page : pages) {
                page.save("page_" + i++ + ".pdf");
                page.close();
            }
        }
    }

    // MAIN – spustenie všetkých ukážok
    public static void main(String[] args) throws IOException {

        // ================================== Vitalii ==================================
        PDF_demo.readPDF("output.pdf");

        System.out.println(" Informácie o stranách ");
        infoPDF("test.pdf");

        System.out.println("\n Nastavenie metadát ");
        metadataPDF("test.pdf", "test_metadata.pdf");
        readMetadataPDF("test_metadata.pdf");

        // ================================== Arseniy ==================================
        PDF_demo.makePDF("output.pdf");
        System.out.println("Vytvorené: output.pdf");

        PDF_demo.changePDF("output.pdf", "upraveny.pdf");
        System.out.println("Upravené: upraveny.pdf");

        // ================================== Andrii ==================================
        System.out.println("\n Ochrana heslom ");
        passwordPDF("test.pdf", "chraneny.pdf");

        System.out.println("\n Zlucenie PDF súborov ");
        mergePdf();

        System.out.println("\n Rozdelenie PDF súborov ");
        splitPdf();
    }
}