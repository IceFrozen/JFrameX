package cn.ximuli.jframex.ui.component.themes;

import cn.ximuli.jframex.ui.Application;
import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IJThemesManager {
    public final List<IJThemeInfo> bundledThemes = new ArrayList<>();
    public final List<IJThemeInfo> moreThemes = new ArrayList<>();
    private final Map<File, Long> lastModifiedMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    void loadBundledThemes() {
        bundledThemes.clear();

        // load themes.json
        Map<String, Object> json;
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(Application.APP_THEMES_PACKAGE), StandardCharsets.UTF_8)) {
            json = (Map<String, Object>) Json.parse(reader);
        } catch (IOException ex) {
			log.error("Error loading themes.json", ex);
            return;
        }

        // add info about bundled themes
        for (Map.Entry<String, Object> e : json.entrySet()) {
            String resourceName = e.getKey();
            Map<String, String> value = (Map<String, String>) e.getValue();
            String name = value.get("name");
            boolean discontinued = Boolean.parseBoolean(value.get("discontinued"));
            boolean dark = Boolean.parseBoolean(value.get("dark"));
            String lafClassName = value.get("lafClassName");
            String license = value.get("license");
            String licenseFile = value.get("licenseFile");
            String pluginUrl = value.get("pluginUrl");
            String sourceCodeUrl = value.get("sourceCodeUrl");
            String sourceCodePath = value.get("sourceCodePath");

            bundledThemes.add(new IJThemeInfo(name, resourceName, discontinued, dark,
                    license, licenseFile, pluginUrl, sourceCodeUrl, sourceCodePath, null, lafClassName));
        }
    }

    void loadThemesFromDirectory() {
        // get current working directory
        File directory = new File("").getAbsoluteFile();

        File[] themeFiles = directory.listFiles((dir, name) -> name.endsWith(".theme.json") || name.endsWith(".properties"));
        if (themeFiles == null)
            return;

        lastModifiedMap.clear();
        lastModifiedMap.put(directory, directory.lastModified());

        moreThemes.clear();
        for (File f : themeFiles) {
            String fName = f.getName();
            String name = fName.endsWith(".properties")
                    ? StringUtils.removeTrailing(fName, ".properties")
                    : StringUtils.removeTrailing(fName, ".theme.json");
            moreThemes.add(new IJThemeInfo(name, null, false, false, null, null, null, null, null, f, null));
            lastModifiedMap.put(f, f.lastModified());
        }
    }

    public boolean hasThemesFromDirectoryChanged() {
        for (Map.Entry<File, Long> e : lastModifiedMap.entrySet()) {
            if (e.getKey().lastModified() != e.getValue().longValue())
                return true;
        }
        return false;
    }
}
