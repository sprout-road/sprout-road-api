package com.sprout.api.file.util;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class IdUtil {

	private final Supplier<String> uuidSupplier;

	public IdUtil() {
		this(() -> UUID.randomUUID().toString());
	}

	public IdUtil(Supplier<String> uuidSupplier) {
		this.uuidSupplier = uuidSupplier;
	}

	public String generateUniqueId(int length) {
		return uuidSupplier.get().substring(0, length);
	}
}
