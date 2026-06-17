package finalexam.vicheth_sokhsedtha.controller;

import finalexam.vicheth_sokhsedtha.model.Profile;
import finalexam.vicheth_sokhsedtha.model.Template;
import finalexam.vicheth_sokhsedtha.service.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles ID card live preview (HTML), PDF export, and batch generation.
 */
@Controller
@RequestMapping("/id-cards")
public class IdCardController {

    private final ProfileService profileService;
    private final TemplateService templateService;
    private final QrCodeService qrCodeService;
    private final BarcodeService barcodeService;
    private final PdfExportService pdfExportService;

    public IdCardController(ProfileService profileService,
                            TemplateService templateService,
                            QrCodeService qrCodeService,
                            BarcodeService barcodeService,
                            PdfExportService pdfExportService) {
        this.profileService = profileService;
        this.templateService = templateService;
        this.qrCodeService = qrCodeService;
        this.barcodeService = barcodeService;
        this.pdfExportService = pdfExportService;
    }

    // ─────────────────────────────────────────────
    //  Live Preview (HTML / Thymeleaf)
    // ─────────────────────────────────────────────

    /**
     * Live preview of a single ID card in the browser.
     * GET /id-cards/preview/{id}
     */
    @GetMapping("/preview/{id}")
    public String preview(@PathVariable Long id, Model model) {
        Optional<Profile> opt = profileService.findById(id);
        if (opt.isEmpty()) {
            return "error/404";
        }
        Profile profile = opt.get();

        // Load the associated template (if any)
        Template template = profile.getTemplate();
        if (template == null) {
            template = templateService.findDefault().orElse(null);
        }

        // Generate QR code data URI for embedding directly in HTML
        String qrDataUri = null;
        if (profile.getUuid() != null) {
            qrDataUri = qrCodeService.generateDataUri(
                    qrCodeService.buildVerificationUrl(profile.getUuid()));
        }

        // Generate barcode data URI
        String barcodeDataUri = null;
        if (profile.getRegistrationNumber() != null) {
            barcodeDataUri = barcodeService.generateDataUri(
                    profile.getRegistrationNumber(), profile.getBarcodeType());
        }

        model.addAttribute("profile", profile);
        model.addAttribute("template", template);
        model.addAttribute("qrCodeDataUri", qrDataUri);
        model.addAttribute("barcodeDataUri", barcodeDataUri);

        return "id-card";
    }

    // ─────────────────────────────────────────────
    //  PDF Export
    // ─────────────────────────────────────────────

    /**
     * Download a single ID card as a PDF.
     * GET /id-cards/pdf/{id}
     */
    @GetMapping("/pdf/{id}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) throws IOException {
        Optional<Profile> opt = profileService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Profile profile = opt.get();

        Template template = profile.getTemplate();
        if (template == null) {
            template = templateService.findDefault().orElse(null);
        }

        byte[] pdfBytes = pdfExportService.generateIdCard(profile, template);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        String filename = "id-card-" + profile.getRegistrationNumber() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    // ─────────────────────────────────────────────
    //  Batch PDF Generation
    // ─────────────────────────────────────────────

    /**
     * Download a ZIP containing multiple ID cards as PDFs.
     * POST /id-cards/pdf/batch
     *
     * Request body: { "ids": [1, 2, 3] }
     * For simplicity, returns a single combined PDF (multi-page).
     */
    @PostMapping("/pdf/batch")
    public ResponseEntity<Resource> batchPdf(@RequestBody List<Long> ids) throws IOException {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // We use a simple approach: combine all PDFs into one by concatenating bytes.
        // A production implementation would use iText PdfWriter with PdfMerger.
        List<byte[]> pdfs = new ArrayList<>();
        for (Long id : ids) {
            Optional<Profile> opt = profileService.findById(id);
            if (opt.isPresent()) {
                Profile profile = opt.get();
                Template template = profile.getTemplate();
                if (template == null) template = templateService.findDefault().orElse(null);
                pdfs.add(pdfExportService.generateIdCard(profile, template));
            }
        }

        if (pdfs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Merge PDFs using iText
        ByteArrayOutputStream mergedBaos = new ByteArrayOutputStream();
        com.itextpdf.kernel.pdf.PdfDocument mergedDoc =
                new com.itextpdf.kernel.pdf.PdfDocument(
                        new com.itextpdf.kernel.pdf.PdfWriter(mergedBaos));
        mergedDoc.initializeOutlines();

        for (byte[] pdfBytes : pdfs) {
            com.itextpdf.kernel.pdf.PdfDocument srcDoc =
                    new com.itextpdf.kernel.pdf.PdfDocument(
                            new com.itextpdf.kernel.pdf.PdfReader(
                                    new java.io.ByteArrayInputStream(pdfBytes)));
            srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), mergedDoc, null);
            srcDoc.close();
        }

        mergedDoc.close();
        ByteArrayResource resource = new ByteArrayResource(mergedBaos.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"id-cards-batch.pdf\"")
                .body(resource);
    }

    // ─────────────────────────────────────────────
    //  QR Code Image Endpoint
    // ─────────────────────────────────────────────

    /**
     * Return the QR code for a profile as a PNG image.
     * GET /id-cards/qr/{profileId}
     */
    @GetMapping(value = "/qr/{profileId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long profileId) {
        try {
            Optional<Profile> opt = profileService.findById(profileId);
            if (opt.isEmpty() || opt.get().getUuid() == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] qrBytes = qrCodeService.generatePngBytes(
                    qrCodeService.buildVerificationUrl(opt.get().getUuid()));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ─────────────────────────────────────────────
    //  Barcode Image Endpoint
    // ─────────────────────────────────────────────

    /**
     * Return the barcode for a profile as a PNG image.
     * GET /id-cards/barcode/{profileId}
     */
    @GetMapping(value = "/barcode/{profileId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBarcode(@PathVariable Long profileId) {
        try {
            Optional<Profile> opt = profileService.findById(profileId);
            if (opt.isEmpty() || opt.get().getRegistrationNumber() == null) {
                return ResponseEntity.notFound().build();
            }
            Profile profile = opt.get();
            byte[] barcodeBytes = barcodeService.generatePngBytes(
                    profile.getRegistrationNumber(), profile.getBarcodeType());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(barcodeBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}