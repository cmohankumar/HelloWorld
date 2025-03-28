```
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.FileUpload;

import java.nio.file.Paths;

public class S3MultipartUploadExample {
    public static void main(String[] args) {
        // Define your bucket name, key (file name in S3), and local file path
        String bucketName = "your-bucket-name";
        String key = "your-object-key";
        String filePath = "path/to/your/local/file";

        // Create an instance of the S3 Transfer Manager
        S3TransferManager transferManager = S3TransferManager.create();

        // Create an UploadFileRequest
        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
            .putObjectRequest(b -> b.bucket(bucketName).key(key))
            .source(Paths.get(filePath))
            .build();

        // Perform the file upload
        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        // Wait for the upload to complete
        fileUpload.completionFuture().join();

        // Close the Transfer Manager
        transferManager.close();

        System.out.println("File uploaded successfully!");
    }
}

```

```
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3-transfer-manager</artifactId>
    <version>2.20.26</version>
</dependency>

<dependency>
    <groupId>software.amazon.awssdk.crt</groupId>
    <artifactId>aws-crt</artifactId>
    <version>0.21.0</version>
</dependency>

```

```
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public class AsyncS3Uploader {

    private final S3TransferManager transferManager;
    private final String bucketName;

    public AsyncS3Uploader(S3TransferManager transferManager, String bucketName) {
        this.transferManager = transferManager;
        this.bucketName = bucketName;
    }

    public void asyncUpload(MultipartFile file, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        Upload upload = transferManager.upload(u -> u
                .putObjectRequest(putObjectRequest)
                .requestBody(AsyncRequestBody.fromInputStream(
                        file.getInputStream(), 
                        file.getSize()
                ))
        );

        // For async completion handling
        upload.completionFuture().whenComplete((result, exception) -> {
            if (exception != null) {
                handleUploadFailure(exception, key);
            } else {
                handleUploadSuccess(key, result.response().eTag());
            }
        });
    }

    private void handleUploadSuccess(String key, String eTag) {
        // Implement success logic
    }

    private void handleUploadFailure(Throwable exception, String key) {
        // Implement error handling
    }
}

```
