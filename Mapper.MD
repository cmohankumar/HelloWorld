```
@Mapper
public interface BarcodeDataToDocumentsMapper {
    BarcodeDataToDocumentsMapper INSTANCE = Mappers.getMapper(BarcodeDataToDocumentsMapper.class);

    // Single-object mapping (for reference)
    @Mapping(source = "docId", target = "documentIdentifier")
    @Mapping(source = "formData", target = "formCode.formData")
    @Mapping(target = "fileType", ignore = true)
    @Mapping(target = "documentDetailText", ignore = true)
    Documents barcodeDataToDocuments(BarcodeData barcodeData);

    // List-to-single-object mapping
    default Documents barcodeDataListToDocuments(List<BarcodeData> barcodeDataList) {
        if (barcodeDataList == null || barcodeDataList.isEmpty()) {
            return null;
        }

        Documents documents = new Documents();
        FormCode formCode = new FormCode();
        documents.setFormCode(formCode);
        List<FormData> combinedFormData = new ArrayList<>();

        // Take docId from first element
        documents.setDocumentIdentifier(barcodeDataList.get(0).getDocId());

        // Aggregate all formData from all list items
        barcodeDataList.stream()
            .filter(bd -> bd.getFormData() != null)
            .forEach(bd -> combinedFormData.addAll(bd.getFormData()));

        formCode.setFormData(combinedFormData);
        return documents;
    }
}
```

````
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface BarcodeDataToDocumentsMapper {
    BarcodeDataToDocumentsMapper INSTANCE = Mappers.getMapper(BarcodeDataToDocumentsMapper.class);

    default List<Documents> barcodeDataListToDocumentsList(List<BarcodeData> barcodeDataList) {
        if (barcodeDataList == null) {
            return null;
        }
        
        return barcodeDataList.stream()
            .map(this::mapBarcodeDataToDocuments)
            .collect(Collectors.toList());
    }

    @Mapping(source = "docId", target = "documentIdentifier")
    @Mapping(source = "formData", target = "formCode.formData")
    @Mapping(target = "fileType", ignore = true)
    @Mapping(target = "documentDetailText", ignore = true)
    Documents mapBarcodeDataToDocuments(BarcodeData barcodeData);
}
````

```
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DigitalRequestToDocStoreMapper {

    @Mapping(target = "docId", expression = "java(getDocIdFromMap(digitalRequest.getDocIdMap(), file.getOriginalFilename()))")
    @Mapping(target = "sourceFileType", expression = "java(file.getContentType())")
    @Mapping(target = "sourceFileName", expression = "java(file.getOriginalFilename())")
    @Mapping(target = "sourceFileSize", expression = "java(file.getSize())")
    @Mapping(target = "sourceTotalPages", ignore = true)
    DocStore mapFileToDocStore(DigitalRequest digitalRequest, MultipartFile file);

    default List<DocStore> mapDigitalRequestToDocStores(DigitalRequest digitalRequest) {
        return digitalRequest.getFiles().stream()
                .map(file -> mapFileToDocStore(digitalRequest, file))
                .collect(Collectors.toList());
    }

    default String getDocIdFromMap(Map<String, String> docIdMap, String originalFilename) {
        return docIdMap != null ? docIdMap.get(originalFilename) : null;
    }
}

```

```
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocStoreService {

    private final DigitalRequestToDocStoreMapper mapper;
    private final DocStoreRepository docStoreRepository;

    @Autowired
    public DocStoreService(DigitalRequestToDocStoreMapper mapper, DocStoreRepository docStoreRepository) {
        this.mapper = mapper;
        this.docStoreRepository = docStoreRepository;
    }

    @Transactional
    public List<DocStore> processAndSaveDigitalRequest(DigitalRequest digitalRequest) {
        return digitalRequest.getFiles().stream()
            .filter(file -> !"application/json".equals(file.getContentType()))
            .map(file -> mapper.mapFileToDocStore(digitalRequest, file))
            .collect(Collectors.collectingAndThen(Collectors.toList(), docStoreRepository::saveAll));
    }
}

```

```
@Configuration
@OpenAPIDefinition(info = @Info(title = "File Upload API", version = "1.0"))
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .path("/upload", new PathItem()
                .post(new Operation()
                    .summary("Upload files with JSON data")
                    .requestBody(new RequestBody()
                        .content(new Content()
                            .addMediaType(MediaType.MULTIPART_FORM_DATA_VALUE, 
                                new MediaType()
                                    .schema(new Schema<>()
                                        .type("object")
                                        .addProperties("json", new Schema<>().type("string").format("binary"))
                                        .addProperties("files", new ArraySchema().items(new Schema<>().type("string").format("binary")))
                                    )
                            )
                        )
                    )
                )
            );
    }
}
```
```
@Mapper
public interface FileMapper {
    
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    @Mapping(source = "file", target = "contentType", qualifiedByName = "detectContentType")
    FileDTO toDto(FileEntity file);

    @Named("detectContentType") // Names the method for reference
    default String detectContentType(FileEntity file) {
        try {
            return new Tika().detect(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to detect MIME type", e);
        }
    }
}

```
```
public class FileValidator {

    private static final Tika TIKA = new Tika();

    // Single file validation
    public static boolean isValidFile(MultipartFile file) {
        try {
            String detectedType = TIKA.detect(
                file.getInputStream(), 
                file.getOriginalFilename()
            );
            return !"application/octet-stream".equals(detectedType);
        } catch (IOException e) {
            return false;  // IOException → invalid file
        }
    }

    // Multiple files validation
    public static boolean areValidFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return false;  // Handle empty/null input
        }
        
        return files.stream()
            .allMatch(FileValidator::isValidFile);
    }
}
```
```
@PostMapping("/upload")
public ResponseEntity<?> handleUpload(
    @RequestParam("files") List<MultipartFile> files
) {
    boolean isValid = FileValidator.areValidFiles(files);
    return isValid 
        ? ResponseEntity.ok("All files valid") 
        : ResponseEntity.badRequest().body("Invalid file(s) detected");
}
```
```
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
        @RequestParam("files") List<MultipartFile> files,
        @RequestHeader(value = "trace-id", required = false) String traceId
    ) {
        // Validate trace-id header
        return (traceId == null || traceId.isBlank())
            ? ResponseEntity.badRequest().body("No trace-id found")
            : !FileValidator.areValidFiles(files)
                ? ResponseEntity.badRequest().body("Invalid file(s) detected")
                : processValidFiles(files, traceId);
    }

    private ResponseEntity<?> processValidFiles(List<MultipartFile> files, String traceId) {
        try {
            return ResponseEntity.ok(fileProcessingService.processFiles(files, traceId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Processing failed");
        }
    }
}
```
