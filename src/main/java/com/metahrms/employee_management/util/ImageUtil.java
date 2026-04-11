package com.metahrms.employee_management.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

/**
 * Utility class để xử lý ảnh
 */
@Slf4j
public class ImageUtil {
    
    private static final int DEFAULT_TARGET_WIDTH = 800;
    private static final float DEFAULT_QUALITY = 0.9f;
    
    /**
     * Convert MultipartFile to Base64 string
     */
    public static String convertToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Convert byte array to Base64
     */
    public static String convertToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Decode Base64 to byte array
     */
    public static byte[] decodeBase64(String base64String) {
        // Remove data URI prefix if exists
        if (base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }
        return Base64.getDecoder().decode(base64String);
    }
    
    /**
     * Resize và compress ảnh trước khi gửi lên AI service
     * 
     * @param base64Image Base64 string của ảnh gốc
     * @param targetWidth Width mục tiêu (height tự scale theo tỷ lệ)
     * @param quality Chất lượng JPEG (0.0 - 1.0)
     * @return Base64 string của ảnh đã xử lý
     */
    public static String resizeAndCompress(String base64Image, int targetWidth, float quality) 
            throws IOException {
        
        log.debug("Resizing image to width={}, quality={}", targetWidth, quality);
        
        // Decode base64
        byte[] imageBytes = decodeBase64(base64Image);
        
        // Read image
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        
        if (originalImage == null) {
            throw new IOException("Failed to read image from base64");
        }
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        log.debug("Original image size: {}x{}", originalWidth, originalHeight);
        
        // Resize nếu cần
        BufferedImage resizedImage = originalImage;
        if (originalWidth > targetWidth) {
            resizedImage = Scalr.resize(
                originalImage, 
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_TO_WIDTH,
                targetWidth,
                Scalr.OP_ANTIALIAS
            );
            log.debug("Resized to: {}x{}", resizedImage.getWidth(), resizedImage.getHeight());
        }
        
        // Compress to JPEG
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPEG writers found");
        }
        
        ImageWriter writer = writers.next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        
        if (writeParam.canWriteCompressed()) {
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(quality);
        }
        
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(resizedImage, null, null), writeParam);
        } finally {
            writer.dispose();
        }
        
        byte[] compressedBytes = outputStream.toByteArray();
        
        log.debug("Original size: {} bytes, Compressed size: {} bytes, Ratio: {}", 
            imageBytes.length, 
            compressedBytes.length,
            String.format("%.2f%%", (compressedBytes.length * 100.0 / imageBytes.length))
        );
        
        return convertToBase64(compressedBytes);
    }
    
    /**
     * Resize và compress với default values
     */
    public static String resizeAndCompress(String base64Image) throws IOException {
        return resizeAndCompress(base64Image, DEFAULT_TARGET_WIDTH, DEFAULT_QUALITY);
    }
    
    /**
     * Validate image size
     */
    public static boolean validateImageSize(MultipartFile file, long maxSizeBytes) {
        return file.getSize() <= maxSizeBytes;
    }
    
    /**
     * Validate image format
     */
    public static boolean validateImageFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/jpg") || 
                contentType.equals("image/png"));
    }
    
    /**
     * Extract format from base64 data URI
     * 
     * @param base64String Base64 string (có thể có data URI prefix)
     * @return Format (jpeg, png) hoặc null
     */
    public static String extractFormat(String base64String) {
        if (base64String.startsWith("data:image/")) {
            String format = base64String.substring(11, base64String.indexOf(";"));
            return format;
        }
        return null;
    }
}