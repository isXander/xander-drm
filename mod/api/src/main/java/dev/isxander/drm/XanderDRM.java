package dev.isxander.drm;

import com.google.gson.Gson;
import dev.isxander.drm.impl.LicenseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class XanderDRM {
    private static final String ENDPOINT = "https://drm.isxander.dev/v1/license/%s";
    private static final Logger LOGGER = LoggerFactory.getLogger(XanderDRM.class);

    public static CompletableFuture<LicenseResponse> checkLicenseAsync(String license) {
        var req = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT.formatted(license)))
                .GET()
                .build();

        return HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        LOGGER.error("Error: {}", response.statusCode());
                        LOGGER.error("Response: {}", response.body());
                        return new LicenseResponse(true, "An error occurred when checking license.");
                    }

                    return new Gson().fromJson(response.body(), LicenseResponse.class);
                });
    }

    public static LicenseResponse checkLicense(String license) {
        return checkLicenseAsync(license).join();
    }

    // method for dependent mods to call to trigger NoClassDefError if the user has attempted to remove the JiJ.
    public static void hello() {}
}