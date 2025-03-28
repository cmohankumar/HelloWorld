```
S3TransferManager transferManager = S3TransferManager.create();

UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
    .putObjectRequest(b -> b.bucket(bucketName).key(key))
    .source(Paths.get(filePath))
    .build();

FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
fileUpload.completionFuture().join();

transferManager.close();
```
