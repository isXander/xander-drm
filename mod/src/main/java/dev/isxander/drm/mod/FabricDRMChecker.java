package dev.isxander.drm.mod;

import dev.isxander.drm.XanderDRM;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricDRMChecker implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FabricDRMChecker.class);

    @Override
    public void onInitialize() {
        checkMods()
                .thenAccept(responses -> {
                    for (ModLicenseResponse response : responses) {
                        if (response == null) continue;

                        acceptLicenseResponse(response);
                    }
                });
    }

    public static void checkLicense(String license, String modid) {
        checkLicense(license, modid, ModLicenseResponse.ErrorAction.CRASH);
    }

    public static void checkLicense(String license, String modid, ModLicenseResponse.ErrorAction errorAction) {
        XanderDRM.checkLicenseAsync(license)
                .thenApply(response -> new ModLicenseResponse(response, modid, errorAction))
                .thenAccept(FabricDRMChecker::acceptLicenseResponse);
    }

    private static void acceptLicenseResponse(ModLicenseResponse response) {
        if (response.license().permit()) {
            LOGGER.info("XanderDRM: License check passed for mod {}", response.license().reason());
        } else {
            LOGGER.error("XanderDRM: License check failed for mod {}: {}", response.modid(), response.license().reason());
            switch (response.action()) {
                case LOG -> {} // above ^^
                case CRASH -> {
                    // bypass any sort of crash detector mod, just die
                    Runtime.getRuntime().exit(0);
                }
            }
        }
    }

    private static @Nullable CompletableFuture<ModLicenseResponse> checkMod(ModContainer mod) {
        CustomValue cv = mod.getMetadata().getCustomValue("xander-drm");
        if (cv == null) return null;

        ModLicenseResponse.ErrorAction action = ModLicenseResponse.ErrorAction.CRASH;
        String license = switch (cv.getType()) {
            case CustomValue.CvType.STRING -> cv.getAsString();
            case CustomValue.CvType.OBJECT -> {
                CustomValue.CvObject object = cv.getAsObject();
                if (object.get("license") == null) {
                    LOGGER.error("XanderDRM: No license found for mod {}", mod.getMetadata().getId());
                    yield null;
                }

                if (object.get("action") != null) {
                    String actionString = object.get("action").getAsString();
                    action = switch (actionString) {
                        case "none" -> ModLicenseResponse.ErrorAction.NONE;
                        case "log" -> ModLicenseResponse.ErrorAction.LOG;
                        case "crash" -> ModLicenseResponse.ErrorAction.CRASH;
                        default -> {
                            LOGGER.error("XanderDRM: Unknown action {} for mod {}", actionString, mod.getMetadata().getId());
                            yield null;
                        }
                    };
                }

                yield object.get("license").getAsString();
            }
            default -> null;
        };

        if (license == null) {
            LOGGER.error("XanderDRM: No license found for mod {}", mod.getMetadata().getId());
            return null;
        }

        ModLicenseResponse.ErrorAction finalAction = action;
        return XanderDRM.checkLicenseAsync(license)
                .thenApply(response -> new ModLicenseResponse(response, mod.getMetadata().getId(), finalAction));
    }

    private static CompletableFuture<List<ModLicenseResponse>> checkMods() {
        List<CompletableFuture<ModLicenseResponse>> futures = new ArrayList<>();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            CompletableFuture<ModLicenseResponse> future = checkMod(mod);
            if (future != null) {
                futures.add(future);
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<ModLicenseResponse> responses = new ArrayList<>();
                    for (CompletableFuture<ModLicenseResponse> future : futures) {
                        try {
                            responses.add(future.get());
                        } catch (Exception e) {
                            LOGGER.error("Error getting license response", e);
                        }
                    }
                    return responses;
                });
    }
}
