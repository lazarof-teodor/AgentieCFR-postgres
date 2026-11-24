package agentie.cfr.util;

import agentie.cfr.model.Bilet;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ExportPDF {

    private static final DateTimeFormatter FORMAT_DATA =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter FORMAT_DATA_ORA =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final int DIMENSIUNE_QR = 300;

    public static void exportaBilet(Bilet bilet, File fisier) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A5);
            document.addPage(pagina);

            PDType1Font fontTitlu = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontEticheta = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontText = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            BufferedImage imagineQR = genereazaCodQR(construiesteContinutQR(bilet));
            PDImageXObject obiectQR = LosslessFactory.createFromImage(document, imagineQR);

            try (PDPageContentStream continut = new PDPageContentStream(document, pagina)) {
                float x = 50;
                float y = pagina.getMediaBox().getHeight() - 60;
                float pasRand = 22;

                float dimensiuneAfisataQR = 100;
                float xQR = pagina.getMediaBox().getWidth() - dimensiuneAfisataQR - 50;
                float yQR = pagina.getMediaBox().getHeight() - dimensiuneAfisataQR - 50;
                continut.drawImage(obiectQR, xQR, yQR, dimensiuneAfisataQR, dimensiuneAfisataQR);

                continut.setFont(fontTitlu, 18);
                scrieRand(continut, x, y, "BILET AGENTIE CFR");
                y -= pasRand * 1.5f;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Numar bilet:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y, "#" + bilet.getId());
                y -= pasRand;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Status:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y, bilet.getStatus().toUpperCase());
                y -= pasRand * 1.5f;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Client:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y,
                        bilet.getClient().getNume() + " " + bilet.getClient().getPrenume()
                        + " (" + bilet.getClient().getCategorie() + ")");
                y -= pasRand;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Ruta:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y,
                        bilet.getCursa().getRuta().getStatiePlecare().getNume() + " -> "
                        + bilet.getCursa().getRuta().getStatieSosire().getNume());
                y -= pasRand;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Tren:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y,
                        bilet.getCursa().getTren().getNumar() + " (" + bilet.getCursa().getTren().getTip() + ")");
                y -= pasRand;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Data calatoriei:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y, bilet.getCursa().getData().format(FORMAT_DATA));
                y -= pasRand;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Ora plecare / sosire:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y,
                        bilet.getCursa().getOraPlecare() + " / " + bilet.getCursa().getOraSosire());
                y -= pasRand;

                continut.setFont(fontEticheta, 11);
                scrieRand(continut, x, y, "Vagon / Loc:");
                continut.setFont(fontText, 11);
                scrieRand(continut, x + 140, y,
                        "Vagon " + bilet.getLoc().getVagon().getNumarVagon()
                        + " (clasa " + bilet.getLoc().getVagon().getClasa() + "), loc "
                        + bilet.getLoc().getNumarLoc());
                y -= pasRand * 1.5f;

                continut.setFont(fontEticheta, 13);
                scrieRand(continut, x, y, "Pret platit:");
                continut.setFont(fontTitlu, 13);
                scrieRand(continut, x + 140, y, bilet.getPret().toString() + " lei");
                y -= pasRand * 1.5f;

                continut.setFont(fontEticheta, 9);
                scrieRand(continut, x, y, "Emis de:");
                continut.setFont(fontText, 9);
                scrieRand(continut, x + 140, y,
                        bilet.getUtilizator().getNume() + " " + bilet.getUtilizator().getPrenume());
                y -= pasRand * 0.8f;

                continut.setFont(fontEticheta, 9);
                scrieRand(continut, x, y, "Data emiterii:");
                continut.setFont(fontText, 9);
                scrieRand(continut, x + 140, y, bilet.getDataEmitere().format(FORMAT_DATA_ORA));
            }

            document.save(fisier);
        }
    }

    private static void scrieRand(PDPageContentStream continut, float x, float y, String text) throws IOException {
        continut.beginText();
        continut.newLineAtOffset(x, y);
        continut.showText(text);
        continut.endText();
    }

    private static String construiesteContinutQR(Bilet bilet) {
        return "AGENTIE CFR"
                + "\nBilet: #" + bilet.getId()
                + "\nClient: " + bilet.getClient().getNume() + " " + bilet.getClient().getPrenume()
                + "\nRuta: " + bilet.getCursa().getRuta().getStatiePlecare().getNume()
                + " -> " + bilet.getCursa().getRuta().getStatieSosire().getNume()
                + "\nTren: " + bilet.getCursa().getTren().getNumar()
                + "\nData: " + bilet.getCursa().getData().format(FORMAT_DATA)
                + "\nOra: " + bilet.getCursa().getOraPlecare()
                + "\nVagon: " + bilet.getLoc().getVagon().getNumarVagon()
                + " Loc: " + bilet.getLoc().getNumarLoc()
                + "\nStatus: " + bilet.getStatus();
    }

    private static BufferedImage genereazaCodQR(String continut) throws IOException {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrice = writer.encode(continut, BarcodeFormat.QR_CODE, DIMENSIUNE_QR, DIMENSIUNE_QR);
            return MatrixToImageWriter.toBufferedImage(matrice);
        } catch (WriterException e) {
            throw new IOException("Eroare la generarea codului QR", e);
        }
    }
}