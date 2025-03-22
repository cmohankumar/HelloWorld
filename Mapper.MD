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

    @Mapping(target = "docId", source = "transactionId")
    @Mapping(target = "sourceFileType", expression = "java(file.getContentType())")
    @Mapping(target = "sourceFileName", expression = "java(file.getOriginalFilename())")
    @Mapping(target = "sourceFileSize", expression = "java(file.getSize())")
    @Mapping(target = "sourceTotalPages", ignore = true)
    DocStore fileToDocStore(String transactionId, MultipartFile file);

    default List<DocStore> digitalRequestToDocStoreList(DigitalRequest digitalRequest) {
        return digitalRequest.getFiles().stream()
                .map(file -> fileToDocStore(digitalRequest.getTransactionId(), file))
                .collect(Collectors.toList());
    }
}
```
