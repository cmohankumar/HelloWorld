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
