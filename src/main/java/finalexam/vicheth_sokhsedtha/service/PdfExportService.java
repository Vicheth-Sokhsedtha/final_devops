package finalexam.vicheth_sokhsedtha.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.Template;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

/**
 * Generates ID cards in PDF format using iText 7.
 * Embeds a QR code image (if available) and renders profile details
 * in a card-like layout using the colour scheme from the chosen Template.
 */
@Service
public class PdfExportService {

    private static final float CARD_WIDTH = 340f;
    private static final float CARD_HEIGHT = 540f;

    private final QrCodeService qrCodeService;
    private final BarcodeService barcodeService;

    public PdfExportService(QrCodeService qrCodeService, BarcodeService barcodeService) {
        this.qrCodeService = qrCodeService;
        this.barcodeService = barcodeService;
    }

    /**
     * Generate a single PDF page containing the ID card for the given profile.
     *
     * @param profile  the profile to render
     * @param template the template/theme (may be null)
     * @return PDF bytes
     */
    public byte[] generateIdCard(Profile profile, Template template) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Determine colours from template
        String primaryHex = template != null ? template.getPrimaryColor() : "#1d4ed8";
        String secondaryHex = template != null ? template.getSecondaryColor() : "#e0e7ff";
        String textHex = template != null ? template.getTextColor() : "#111827";

        Color primary = hexToColor(primaryHex);
        Color secondary = hexToColor(secondaryHex);
        Color textColor = hexToColor(textHex);

        // Header bar
        PdfFont boldFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

        // ── Header ──
        Cell headerCell = new Cell().setBackgroundColor(primary).setPadding(12).setBorder(Border.NO_BORDER);
        Paragraph orgName = new Paragraph(
                template != null ? template.getOrganizationName() : "Organization")
                .setFont(boldFont).setFontSize(16).setFontColor(DeviceRgb.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        headerCell.add(orgName);
        if (template != null && template.getTagline() != null && !template.getTagline().isBlank()) {
            headerCell.add(new Paragraph(template.getTagline())
                    .setFont(normalFont).setFontSize(10).setFontColor(new DeviceRgb(220, 220, 255))
                    .setTextAlignment(TextAlignment.CENTER));
        }
        table.addCell(headerCell);

        // ── Body ──
        Cell bodyCell = new Cell().setPadding(16).setBorder(Border.NO_BORDER);
        bodyCell.setBackgroundColor(DeviceRgb.WHITE);

        // Name
        bodyCell.add(new Paragraph(profile.getFullName())
                .setFont(boldFont).setFontSize(16).setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER));

        if (profile.getTitle() != null && !profile.getTitle().isBlank()) {
            bodyCell.add(new Paragraph(profile.getTitle())
                    .setFont(normalFont).setFontSize(11).setFontColor(new DeviceRgb(107, 114, 128))
                    .setTextAlignment(TextAlignment.CENTER));
        }

        // Details
        addDetail(bodyCell, "Reg No.", profile.getRegistrationNumber(), normalFont, textColor);
        if (profile.getDepartment() != null) addDetail(bodyCell, "Dept.", profile.getDepartment(), normalFont, textColor);
        if (profile.getEmail() != null) addDetail(bodyCell, "Email", profile.getEmail(), normalFont, textColor);
        if (profile.getPhone() != null) addDetail(bodyCell, "Phone", profile.getPhone(), normalFont, textColor);
        if (profile.getBloodGroup() != null) addDetail(bodyCell, "Blood", profile.getBloodGroup(), normalFont, textColor);
        addDetail(bodyCell, "Type", profile.getType().name(), normalFont, textColor);
        addDetail(bodyCell, "Issue",
                profile.getIssueDate() != null ? profile.getIssueDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "",
                normalFont, textColor);
        addDetail(bodyCell, "Expiry",
                profile.getExpiryDate() != null ? profile.getExpiryDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "",
                normalFont, textColor);

        // QR Code
        if (profile.getUuid() != null) {
            try {
                String qrDataUri = qrCodeService.generateDataUri(
                        qrCodeService.buildVerificationUrl(profile.getUuid()));
                if (qrDataUri != null) {
                    // iText can load from a URL data-uri using ImageDataFactory
                    Image qrImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(
                            new URL(qrDataUri)));
                    qrImage.setWidth(80).setHeight(80).setTextAlignment(TextAlignment.CENTER);
                    bodyCell.add(new Paragraph().add(qrImage).setTextAlignment(TextAlignment.CENTER));
                }
            } catch (Exception ignored) { /* skip QR if generation fails */ }
        }

        // Barcode
        if (profile.getRegistrationNumber() != null) {
            try {
                String barcodeDataUri = barcodeService.generateDataUri(
                        profile.getRegistrationNumber(), profile.getBarcodeType());
                if (barcodeDataUri != null) {
                    Image barcodeImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(
                            new URL(barcodeDataUri)));
                    barcodeImage.setWidth(200).setHeight(40).setTextAlignment(TextAlignment.CENTER);
                    bodyCell.add(new Paragraph().add(barcodeImage).setTextAlignment(TextAlignment.CENTER));
                }
            } catch (Exception ignored) { /* skip barcode if generation fails */ }
        }

        table.addCell(bodyCell);

        // ── Footer ──
        Cell footerCell = new Cell().setBackgroundColor(secondary).setPadding(8).setBorder(Border.NO_BORDER);
        footerCell.add(new Paragraph("ID: " + (profile.getUuid() != null ? profile.getUuid().substring(0, 8) : ""))
                .setFont(normalFont).setFontSize(9).setFontColor(new DeviceRgb(107, 114, 128))
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(footerCell);

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addDetail( Cell cell, String label, String value, PdfFont font, Color textColor) {
        Paragraph p = new Paragraph()
                .add(new Text(label + ": ").setFont(font).setFontSize(10).setFontColor(new DeviceRgb(107, 114, 128)))
                .add(new Text(value != null ? value : "").setFont(font).setFontSize(10).setFontColor(textColor));
        cell.add(p);
    }

    private Color hexToColor(String hex) {
        if (hex == null || hex.length() < 6) return new DeviceRgb(29, 78, 216);
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            int r = Integer.parseInt(h.substring(0, 2), 16);
            int g = Integer.parseInt(h.substring(2, 4), 16);
            int b = Integer.parseInt(h.substring(4, 6), 16);
            return new DeviceRgb(r, g, b);
        } catch (Exception e) {
            return new DeviceRgb(29, 78, 216);
        }
    }
}