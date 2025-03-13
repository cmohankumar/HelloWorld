# HelloWorld
CI-Specific Configuration
For CI builds specifically:
Set the environment variable SPOTLESS_DISABLE_LINE_ENDINGS=true in your CI configuration.
Or use a CI-specific Spotless profile in your build tool configuration that disables line ending checks.

Client → Controller → Sync Middle Service → External API (sync)
                              │
                              └→ Async File Service → Fileserver + DB
@RestController
public class FileController {

    @Autowired
    private SyncMiddleService middleService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResult> uploadFile(@RequestParam("file") MultipartFile file) {
        ApiResult apiResponse = middleService.processFile(file);
        return ResponseEntity.ok(apiResponse);
    }
}
@Service
public class SyncMiddleService {

    @Autowired
    private ExternalApiClient externalApi;
    
    @Autowired
    private AsyncFileService asyncFileService;

    public ApiResult processFile(MultipartFile file) {
        // 1. Make synchronous API call
        ApiResult apiResponse = externalApi.callExternalService(file);
        
        // 2. Initiate async processing
        String transactionId = UUID.randomUUID().toString();
        asyncFileService.handleFileUpload(file, transactionId);
        
        // 3. Combine results for immediate response
        return new ApiResult(
            apiResponse.getData(), 
            transactionId,
            "PROCESSING"
        );
    }
}
@Service
public class AsyncFileService {

    @Autowired
    private FileRepository fileRepo;

    @Async("fileUploadTaskExecutor")
    public void handleFileUpload(MultipartFile file, String transactionId) {
        try {
            // 1. Save file to fileserver
            Path filePath = Paths.get("/storage/" + file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            // 2. Update database
            fileRepo.save(new FileRecord(
                transactionId,
                file.getOriginalFilename(),
                "COMPLETED",
                Instant.now()
            ));
        } catch (IOException e) {
            fileRepo.updateStatus(transactionId, "FAILED");
        }
    }
}
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "fileUploadTaskExecutor")
    public Executor fileUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("FileUpload-");
        executor.initialize();
        return executor;
    }
}
@Entity
public class FileRecord {
    @Id
    private String transactionId;
    private String filename;
    private String status;
    private Instant processedAt;
    // getters/setters
}



    public static int countPages(InputStream pdfStream) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(pdfStream)) {
            PDFParser parser = new PDFParser(bis);
            parser.parse();
            try (PDDocument document = parser.getPDDocument()) {
                return document.getNumberOfPages();
            }
        } catch (ClassCastException e) {
            throw new IOException("Corrupted PDF structure: " + e.getMessage());
        }
    }


   This is a common issue when working with multipart files in asynchronous methods. The problem likely occurs because Spring's MultipartFile objects are temporary and are cleaned up after the request completes, but your @Async method might be executing after this cleanup happens.

Let's see how we can fix this. Here's a sample implementation that should help resolve your issue:

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Service
public class FileProcessingService {

    @Async
    public CompletableFuture<String> processFiles(MultipartFile file1, MultipartFile file2) throws IOException {
        // Create temporary files that will persist beyond the request
        Path tempFile1Path = Files.createTempFile("file1-", "-temp");
        Path tempFile2Path = Files.createTempFile("file2-", "-temp");
        
        try {
            // Transfer the multipart content to these temp files
            file1.transferTo(tempFile1Path.toFile());
            file2.transferTo(tempFile2Path.toFile());
            
            // Now we can safely access the files as streams, even in async context
            try (InputStream stream1 = new FileInputStream(tempFile1Path.toFile());
                 InputStream stream2 = new FileInputStream(tempFile2Path.toFile())) {
                
                // Process your streams here
                System.out.println("Processing file 1: " + stream1.available() + " bytes");
                System.out.println("Processing file 2: " + stream2.available() + " bytes");
                
                // Your processing logic here
                // ...
                
                return CompletableFuture.completedFuture("Files processed successfully");
            }
        } finally {
            // Clean up temporary files when done
            try {
                Files.deleteIfExists(tempFile1Path);
                Files.deleteIfExists(tempFile2Path);
            } catch (IOException e) {
                // Log but don't throw to avoid masking the original exception
                System.err.println("Warning: Could not delete temporary files: " + e.getMessage());
            }
        }
    }
}
```

And here's how you might call this from your controller:

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
public class FileUploadController {
    
    private final FileProcessingService fileProcessingService;
    
    public FileUploadController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }
    
    @PostMapping("/upload-process")
    public ResponseEntity<String> uploadAndProcess(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {
        try {
            // Start async processing
            fileProcessingService.processFiles(file1, file2);
            return ResponseEntity.ok("Files submitted for processing");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
```

Key points to understand:

1. The `NoSuchFileException` occurs because Spring's `MultipartFile` objects are backed by temporary files that get cleaned up after the request completes, but your @Async method runs independently.

2. By transferring the content to our own temporary files first, we ensure the data persists until our async method finishes processing.

3. Make sure your @Async is configured correctly in your application. You need `@EnableAsync` in your configuration class.

4. The try-with-resources construct ensures streams are properly closed.

5. The finally block ensures cleanup happens regardless of success or failure.

This approach should resolve the issue with the second file failing with NoSuchFileException in your @Async method.​​​​​​​​​​​​​​​​ 
