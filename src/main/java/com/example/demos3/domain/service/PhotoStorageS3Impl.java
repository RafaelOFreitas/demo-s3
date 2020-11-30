package com.example.demos3.domain.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.demos3.core.storage.StorageProperties;
import com.example.demos3.domain.exception.StorageException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
@AllArgsConstructor
public class PhotoStorageS3Impl implements PhotoStorageService {
    private final AmazonS3 amazonS3;
    private final StorageProperties storageProperties;

    @Override
    public void store(NewPicture newPicture) {
        try {
            StorageProperties.S3 s3 = this.storageProperties.getS3();

            var metaData = new ObjectMetadata();
            metaData.setContentType(newPicture.getContentType());

            var putObjectRequest = new PutObjectRequest(
                    s3.getBucketName(),
                    this.getFilePath(newPicture.getFileName()),
                    newPicture.getInputStream(),
                    metaData
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            this.amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new StorageException("Could not send file to Amazon S3", e.getCause());
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            StorageProperties.S3 s3 = this.storageProperties.getS3();

            var deleteObjectRequest = new DeleteObjectRequest(s3.getBucketName(), this.getFilePath(fileName));

            this.amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new StorageException("Unable to delete file on Amazon S3", e.getCause());
        }
    }

    @Override
    public PhotoRecover toRecover(String fileName) {
        try {
            StorageProperties.S3 s3 = this.storageProperties.getS3();
            String keyName = this.getFilePath(fileName);

            ListObjectsV2Result result = this.amazonS3.listObjectsV2(s3.getBucketName());
            List<S3ObjectSummary> objects = result.getObjectSummaries();

            boolean exists = objects.stream().map(S3ObjectSummary::getKey).anyMatch(key -> key.equals(keyName));

            if (!exists) {
                return PhotoRecover.builder()
                        .url(null)
                        .build();
            }

            URL url = this.amazonS3.getUrl(s3.getBucketName(), keyName);

            return PhotoRecover.builder()
                    .url(url.toString())
                    .build();
        } catch (Exception e) {
            throw new StorageException("Couldn't retrieve the file on Amazon S3", e.getCause());
        }
    }

    private String getFilePath(String fileName) {
        return String.format("%s/%s", this.storageProperties.getS3().getPhotosDirectory(), fileName);
    }
}
