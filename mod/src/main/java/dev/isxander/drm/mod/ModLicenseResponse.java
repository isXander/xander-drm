package dev.isxander.drm.mod;

import dev.isxander.drm.impl.LicenseResponse;

public record ModLicenseResponse(LicenseResponse license, String modid, ErrorAction action) {
    public enum ErrorAction {
        NONE,
        LOG,
        CRASH
    }
}
