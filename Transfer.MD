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
