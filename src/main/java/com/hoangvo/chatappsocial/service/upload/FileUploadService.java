package com.hoangvo.chatappsocial.service.upload;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final Bucket bucket;
    private final Storage storage;

    String getImageUrl(String name) {
        return String.format(StorageClient.getInstance().bucket().getSelfLink(), name);
    }


    public UploadInfo save(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        return UploadInfo.builder()
                .fileName(fileName)
                .url(generateDownLoadableLink(blob.asBlobInfo()))
                .fileSize(file.getSize())
                .build();
    }

    public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {
        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));


        String name = generateFileName(originalFileName);

        bucket.create(name, bytes);


        return name;
    }

    public void delete(String name) throws IOException {

        if (name.isEmpty()) {
            throw new IOException("invalid file name");
        }

        Blob blob = bucket.get(name);

        if (blob == null) {
            throw new IOException("file not found");
        }

        blob.delete();
    }

    private String generateDownLoadableLink(BlobInfo blobInfo) {
        return storage.signUrl(blobInfo, 365, TimeUnit.DAYS, Storage.SignUrlOption.withVirtualHostedStyle()).toString();
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID() + "." + getExtension(originalFileName);
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

    private byte[] getByteArrays(BufferedImage bufferedImage, String format) throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ImageIO.write(bufferedImage, format, baos);

            baos.flush();

            return baos.toByteArray();

        }
    }

    @Builder
    public static class UploadInfo {
        public String fileName;
        public String url;
        public Long fileSize;
    }

}
