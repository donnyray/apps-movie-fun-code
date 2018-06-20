package org.superbiz.moviefun.blob;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.tika.io.IOUtils;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client amazonS3Client;
    private String bucketName;

    public S3Store(AmazonS3Client amazonS3Client, String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {

        amazonS3Client.putObject(bucketName,
                blob.getName(),
                blob.getInputStream(),
                new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        S3Object s3Object = amazonS3Client.getObject(bucketName, name);
        return Optional.of(
                new Blob(
                        name,
                        s3Object.getObjectContent(),
                        s3Object.getObjectMetadata().getContentType()));
    }

    @Override
    public void deleteAll() {

    }
}
