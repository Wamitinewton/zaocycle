package com.newton.zaocycle.profile.infrastructure.storage;

import com.newton.zaocycle.certification.infrastructure.storage.StorageProperties;
import com.newton.zaocycle.profile.domain.port.ProfileImageStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(name = "zaocycle.storage.provider", havingValue = "LOCAL_FS")
public class LocalFsProfileImageStore implements ProfileImageStore {

    private final Path basePath;
    private final String publicUrlPrefix;

    public LocalFsProfileImageStore(StorageProperties props) {
        this.basePath = Paths.get(props.localFs().basePath());
        this.publicUrlPrefix = props.localFs().publicUrlPrefix();
        try {
            Files.createDirectories(this.basePath);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create storage directory: " + this.basePath, e);
        }
    }

    @Override
    public String upload(String filename, byte[] data, String contentType) {
        try {
            Files.write(basePath.resolve(filename), data);
            return publicUrlPrefix + "/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write profile image: " + filename, e);
        }
    }
}
