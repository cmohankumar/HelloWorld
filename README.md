# HelloWorld
My first hello world Repo...
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String text = "Hello world:then is not enough:welcome my:to my space";

        String result = text.replaceAll("(?<=:[^:]*+), (?=[^:]++*:[^:]*+$)", 
                System.lineSeparator());

        System.out.println(result);
    }
}

CI-Specific Configuration
For CI builds specifically:
Set the environment variable SPOTLESS_DISABLE_LINE_ENDINGS=true in your CI configuration.
Or use a CI-specific Spotless profile in your build tool configuration that disables line ending checks.

public void extractBarCode(List<MultipartFile> files) throws DataExtractionException {
    ExecutorService threadPool = createFixedThreadPool(5); // Create a thread pool with 5 threads
    try {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            futures.add(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return objectService.putObject(file.getOriginalFilename(), inputStream);
                    } catch (Exception e) {
                        // Log the error and rethrow as RuntimeException
                        System.err.println("Error processing file " + file.getOriginalFilename() + ": " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }, threadPool)
                .thenAccept(objectServiceResponse -> 
                    System.out.println("File processed: " + file.getOriginalFilename())
                )
                .exceptionally(e -> {
                    System.err.println("Failed to process file " + file.getOriginalFilename() + ": " + e.getMessage());
                    return null;
                })
            );
        }

        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    } catch (SafePathCheckException | IOException e) {
        throw new DataExtractionException(e.getMessage());
    } finally {
        // Properly shutdown the thread pool
        threadPool.shutdown(); // Initiates an orderly shutdown
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) { // Wait for tasks to finish
                System.err.println("Thread pool did not terminate within the timeout. Forcing shutdown...");
                threadPool.shutdownNow(); // Force shutdown if tasks don't complete in time
            }
        } catch (InterruptedException ex) {
            System.err.println("Thread pool shutdown interrupted. Forcing shutdown...");
            threadPool.shutdownNow(); // Force shutdown on interruption
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}


Create a MultiValueMap to hold the parts of the request:

MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

Add the FileUploadRequest as a JSON part:

FileUploadRequest request = new FileUploadRequest();
// Set properties of request
HttpHeaders jsonHeaders = new HttpHeaders();
jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
HttpEntity<FileUploadRequest> requestEntity = new HttpEntity<>(request, jsonHeaders);
body.add("json", requestEntity);

Add the MultipartFile objects:

for (MultipartFile file : files) {
    HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(
        new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        }, fileHeaders);
    body.add("files", fileEntity);
}

Set the headers for the entire request:

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.MULTIPART_FORM_DATA);

Create the final HttpEntity:

HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

Send the request using RestTemplate:

RestTemplate restTemplate = new RestTemplate();
ResponseEntity<String> response = restTemplate.postForEntity(
    "http://your-api-url/upload",
    requestEntity,
    String.class
);

