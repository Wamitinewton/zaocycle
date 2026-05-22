package com.newton.zaocycle.collection.infrastructure.storage;

import com.newton.zaocycle.certification.infrastructure.storage.StorageProperties;
import com.newton.zaocycle.collection.domain.port.PickupPhotoStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Component
@ConditionalOnProperty(name = "zaocycle.storage.provider", havingValue = "SPACES", matchIfMissing = true)
public class SpacesPickupPhotoStore implements PickupPhotoStore {

    private static final Logger log = LoggerFactory.getLogger(SpacesPickupPhotoStore.class);

    private final S3Client s3;
    private final String bucket;
    private final String region;

    public SpacesPickupPhotoStore(StorageProperties props) {
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
        log.info("SpacesPickupPhotoStore initialized: bucket={}, region={}", bucket, region);
    }

    @Override
    public String upload(String filename, byte[] data, String contentType) {
        log.debug("Uploading to Spaces: bucket={}, key={}, contentType={}, bytes={}", bucket, filename, contentType, data.length);
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            s3.putObject(request, RequestBody.fromBytes(data));
            String url = "https://" + bucket + "." + region + ".digitaloceanspaces.com/" + filename;
            log.debug("Upload successful: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Failed to upload pickup photo to Spaces: bucket={}, key={}, error={}", bucket, filename, e.getMessage(), e);
            throw e;
        }
    }
}
