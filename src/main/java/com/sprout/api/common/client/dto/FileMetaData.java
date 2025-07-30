package com.sprout.api.common.client.dto;

import java.io.InputStream;
import java.util.function.Supplier;

public record FileMetaData(
    Supplier<InputStream> inputStreamSupplier,
    String originalFilename,
    String contentType,
    long contentSize
) {

    public static FileMetaData of(
        Supplier<InputStream> inputStreamSupplier,
        String originalFilename,
        String contentType,
        long contentSize
    ) {
        return new FileMetaData(
            inputStreamSupplier,
            originalFilename,
            contentType,
            contentSize
        );
    }

    public InputStream getInputStream() {
        return inputStreamSupplier.get();
    }
}
