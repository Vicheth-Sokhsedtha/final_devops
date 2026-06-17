package finalexam.vicheth_sokhsedtha.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;

/**
 * Generates QR codes using the ZXing library.
 */
@Service
public class QrCodeService {

    private static final int SIZE = 200;

    /**
     * Generate a QR code image (PNG) and return it as a base64 data URI
     * suitable for embedding directly in HTML {@code <img>} tags.
     *
     * @param content the data to encode (e.g. a verification URL)
     * @return data URI string, e.g. {@code data:image/png;base64,...}
     */
    public String generateDataUri(String content) {
        try {
            byte[] pngBytes = generatePngBytes(content);
            String base64 = Base64.getEncoder().encodeToString(pngBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generate a QR code image (PNG) as a raw byte array.
     */
    public byte[] generatePngBytes(String content) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Build a verification URL for the given profile UUID.
     * In production, replace the base URL with a configurable property.
     */
    public String buildVerificationUrl(String profileUuid) {
        return "http://localhost:8080/profiles/uuid/" + profileUuid;
    }
}