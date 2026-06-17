package finalexam.vicheth_sokhsedtha.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import finalexam.vicheth_sokhsedtha.model.BarcodeType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;

/**
 * Generates linear barcodes (Code-128 or EAN-13) using ZXing.
 */
@Service
public class BarcodeService {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 60;

    /**
     * Generate a barcode image (PNG) as a base64 data URI.
     *
     * @param content data to encode (for EAN-13 must be exactly 12 or 13 digits)
     * @param type    the barcode symbology
     * @return data URI string, or {@code null} if generation fails
     */
    public String generateDataUri(String content, BarcodeType type) {
        try {
            byte[] pngBytes = generatePngBytes(content, type);
            String base64 = Base64.getEncoder().encodeToString(pngBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generate a barcode image (PNG) as a raw byte array.
     */
    public byte[] generatePngBytes(String content, BarcodeType type) throws WriterException, IOException {
        BarcodeFormat format;
        com.google.zxing.Writer writer;

        switch (type) {
            case EAN_13:
                format = BarcodeFormat.EAN_13;
                writer = new EAN13Writer();
                // Pad with leading zero if needed
                if (content.length() == 12) {
                    content = "0" + content;
                }
                break;
            case CODE_128:
            default:
                format = BarcodeFormat.CODE_128;
                writer = new Code128Writer();
                break;
        }

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = writer.encode(content, format, WIDTH, HEIGHT, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }
}