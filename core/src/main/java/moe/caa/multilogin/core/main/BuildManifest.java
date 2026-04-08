package moe.caa.multilogin.core.main;

import lombok.Getter;
import moe.caa.multilogin.api.internal.logger.LoggerProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * 非稳定版本输出Banner
 */

@Getter
public class BuildManifest {
    private final MultiCore core;
    private String buildType;
    private Date buildDate;
    private String version;

    public BuildManifest(MultiCore core) {
        this.core = core;
    }

    public void read() throws IOException {
        Properties properties = new Properties();
        try (InputStream resourceStream = getClass().getResourceAsStream("/build.properties")) {
            if (resourceStream == null) {
                throw new IOException("build.properties resource not found");
            }
            properties.load(resourceStream);
        }

        buildType = properties.getProperty("build_type", "unknown");
        String timestampStr = properties.getProperty("build_timestamp", "0");
        buildDate = new Date(Long.parseLong(timestampStr));
        version = properties.getProperty("version", "unknown");
    }

    public void checkStable() {
        if (!buildType.equalsIgnoreCase("final")) {
            LoggerProvider.getLogger().warn("Please exercise caution and care when using the current version of the multilogin, as it may be unstable.");
        }
    }
}
