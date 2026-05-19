package com.newton.zaocycle.certification.infrastructure.storage;

import com.newton.zaocycle.certification.domain.port.CertificateImageStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Component
@ConditionalOnProperty(name = "zaocycle.storage.provider", havingValue = "SPACES", matchIfMissing = true)
public class SpacesCertificateImageStore implements CertificateImageStore {

    private final S3Client s3;
    private final String bucket;
    private final String region;

    public SpacesCertificateImageStore(StorageProperties props) {
        StorageProperties.Spaces spaces = props.spaces();
        this.bucket = spaces.bucket();
        this.region = spaces.region();
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(spaces.endpoint()))
                .region(Region.of(spaces.region()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(spaces.accessKey(), spaces.secretKey())
                ))
                .build();
    }

    @Override
    public String upload(String filename, byte[] data, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3.putObject(request, RequestBody.fromBytes(data));
        return "https://" + bucket + "." + region + ".digitaloceanspaces.com/" + filename;
    }
}
