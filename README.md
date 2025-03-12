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



