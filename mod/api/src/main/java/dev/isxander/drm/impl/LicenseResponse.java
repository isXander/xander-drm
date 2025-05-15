package dev.isxander.drm.impl;

public record LicenseResponse(
        boolean permit,
        String reason
) {}
