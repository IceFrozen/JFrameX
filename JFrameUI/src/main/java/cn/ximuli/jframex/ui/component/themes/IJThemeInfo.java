package cn.ximuli.jframex.ui.component.themes;

import lombok.Getter;

import java.io.File;

@Getter
public class IJThemeInfo {
    final String name;
    final String resourceName;
    final boolean discontinued;
    final boolean dark;
    final String license;
    final String licenseFile;
    final String pluginUrl;
    final String sourceCodeUrl;
    final String sourceCodePath;
    final File themeFile;
    final String lafClassName;

    public IJThemeInfo(String name, boolean dark, String lafClassName) {
        this(name, null, false, dark, null, null, null, null, null, null, lafClassName);
    }

    public IJThemeInfo(String name, String resourceName, boolean discontinued, boolean dark,
                       String license, String licenseFile,
                       String pluginUrl, String sourceCodeUrl, String sourceCodePath,
                       File themeFile, String lafClassName) {
        this.name = name;
        this.resourceName = resourceName;
        this.discontinued = discontinued;
        this.dark = dark;
        this.license = license;
        this.licenseFile = licenseFile;
        this.pluginUrl = pluginUrl;
        this.sourceCodeUrl = sourceCodeUrl;
        this.sourceCodePath = sourceCodePath;
        this.themeFile = themeFile;
        this.lafClassName = lafClassName;
    }
}
