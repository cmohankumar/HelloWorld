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
    try {
        ExecutorService threadPool = createFixedThreadPool(5);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            futures.add(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return objectService.putObject(file.getOriginalFilename(), inputStream);
                    } catch (Exception e) {
                        // Log the error
                        System.err.println("Error processing file " + file.getOriginalFilename() + ": " + e.getMessage());
                        // Rethrow as a runtime exception to be handled by the CompletableFuture
                        throw new RuntimeException(e);
                    }
                }, threadPool)
                .thenAccept(objectServiceResponse -> System.out.println("File processed: " + file.getOriginalFilename()))
                .exceptionally(e -> {
                    System.err.println("Failed to process file " + file.getOriginalFilename() + ": " + e.getMessage());
                    return null;
                })
            );
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    } catch (SafePathCheckException | IOException e) {
        throw new DataExtractionException(e.getMessage());
    }
}

