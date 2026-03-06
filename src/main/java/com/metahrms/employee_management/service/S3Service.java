package com.metahrms.employee_management.service;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import com.metahrms.employee_management.dto.response.PresignedUrlResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class S3Service {
  @Value("${aws.accessKeyId}")
  private String accessKeyId;

  @Value("${aws.secretAccessKey}")
  private String secretAccessKey;

  @Value("${aws.region}")
  private String region;

  @Value("${aws.bucketName}")
  private String bucketName;

  public S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
        .build();
  }

  private S3Presigner getPresigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
        .build();
  }

  /**
   * Create a presigned URL for uploading files with automatic filename generation
   * @param fileName The original file name
   * @param user_id The user ID
   * @param folderType The folder type (e.g., "documents", "contracts", "payrolls")
   * @return PresignedUrlResponse containing the presigned URL and object key
   */
  public PresignedUrlResponse createPresignedUrlWithFolder(String fileName, Integer user_id, String folderType) {
    try (S3Presigner presigner = getPresigner()) {
      String generatedFileName = generateFileName(fileName);
      String objectKey = user_id + "/" + folderType + "/" + generatedFileName;
      String contentType = getContentType(fileName);

      PutObjectRequest objectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(objectKey)
          .contentType(contentType)
          .build();

      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .putObjectRequest(objectRequest)
          .build();

      PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

      return PresignedUrlResponse.builder()
          .presignedUrl(presignedRequest.url().toString())
          .objectKey(objectKey)
          .contentType(contentType)
          .build();
    }
  }

  // Convenience methods for backward compatibility
  public PresignedUrlResponse createPresignedUrlForDocument(String fileName, Integer user_id) {
    return createPresignedUrlWithFolder(fileName, user_id, "documents");
  }

  public PresignedUrlResponse createPresignedUrlForContract(String fileName, Integer user_id) {
    return createPresignedUrlWithFolder(fileName, user_id, "contracts");
  }

  public String getS3Url(String fileName) {
    try (S3Presigner presigner = getPresigner()) {
      GetObjectRequest objectRequest = GetObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofHours(12))
          .getObjectRequest(objectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    }
  }

  public String createPresignedUrlForPayroll(byte[] fileContent, String fileName, Integer userId) {
    try (S3Client s3Client = getS3Client()) {
      String objectKey = userId + "/payrolls/" + generateFileName(fileName);

      PutObjectRequest putRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(objectKey)
          .contentType("application/pdf")
          .build();

      InputStream fileInputStream = new ByteArrayInputStream(fileContent);
      s3Client.putObject(putRequest, RequestBody.fromInputStream(fileInputStream, fileContent.length));
      return objectKey;
    }
  }

  public String generateFileName(String name) {
    name = name.replaceAll("\\s+", "_").trim();

    Matcher matcher = Pattern.compile("(\\.[^.]+)$").matcher(name);
    String extension = matcher.find() ? matcher.group(1) : "";

    String baseName = extension.isEmpty() ? name : name.replace(extension, "");

    String timeStamp = String.valueOf(System.currentTimeMillis());
    String uniqueID = UUID.randomUUID().toString();

    return baseName + "-" + timeStamp + "-" + uniqueID + extension;
  }

  private String getContentType(String fileName) {
    String lowerFileName = fileName.toLowerCase();

    // Image types
    if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
      return "image/jpeg";
    } else if (lowerFileName.endsWith(".png")) {
      return "image/png";
    } else if (lowerFileName.endsWith(".gif")) {
      return "image/gif";
    } else if (lowerFileName.endsWith(".webp")) {
      return "image/webp";
    } else if (lowerFileName.endsWith(".svg")) {
      return "image/svg+xml";
    } else if (lowerFileName.endsWith(".bmp")) {
      return "image/bmp";
    } else if (lowerFileName.endsWith(".ico")) {
      return "image/x-icon";
    }

    // Document types
    else if (lowerFileName.endsWith(".pdf")) {
      return "application/pdf";
    } else if (lowerFileName.endsWith(".doc")) {
      return "application/msword";
    } else if (lowerFileName.endsWith(".docx")) {
      return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    } else if (lowerFileName.endsWith(".xls")) {
      return "application/vnd.ms-excel";
    } else if (lowerFileName.endsWith(".xlsx")) {
      return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    } else if (lowerFileName.endsWith(".ppt")) {
      return "application/vnd.ms-powerpoint";
    } else if (lowerFileName.endsWith(".pptx")) {
      return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    } else if (lowerFileName.endsWith(".txt")) {
      return "text/plain";
    } else if (lowerFileName.endsWith(".csv")) {
      return "text/csv";
    } else if (lowerFileName.endsWith(".rtf")) {
      return "application/rtf";
    } else if (lowerFileName.endsWith(".odt")) {
      return "application/vnd.oasis.opendocument.text";
    } else if (lowerFileName.endsWith(".ods")) {
      return "application/vnd.oasis.opendocument.spreadsheet";
    } else if (lowerFileName.endsWith(".odp")) {
      return "application/vnd.oasis.opendocument.presentation";
    }

    // Archive types
    else if (lowerFileName.endsWith(".zip")) {
      return "application/zip";
    } else if (lowerFileName.endsWith(".rar")) {
      return "application/x-rar-compressed";
    } else if (lowerFileName.endsWith(".7z")) {
      return "application/x-7z-compressed";
    } else if (lowerFileName.endsWith(".tar")) {
      return "application/x-tar";
    } else if (lowerFileName.endsWith(".gz")) {
      return "application/gzip";
    }

    // Other common types
    else if (lowerFileName.endsWith(".json")) {
      return "application/json";
    } else if (lowerFileName.endsWith(".xml")) {
      return "application/xml";
    } else if (lowerFileName.endsWith(".html") || lowerFileName.endsWith(".htm")) {
      return "text/html";
    } else if (lowerFileName.endsWith(".css")) {
      return "text/css";
    } else if (lowerFileName.endsWith(".js")) {
      return "application/javascript";
    }

    return "application/octet-stream";
  }
}
