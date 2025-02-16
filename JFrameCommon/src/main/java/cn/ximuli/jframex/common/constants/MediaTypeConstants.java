package cn.ximuli.jframex.common.constants;

public enum MediaTypeConstants {
    STREAM("*", "application/octet-stream"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    WAV("wav", "audio/wav"),
    MP3("mp3", "audio/mp3"),
    MP4("mp4", "video/mpeg4"),
    TXT("txt", "text/plain"),
    STR("str", "text/plain"),
    XLS("xls", "application/x-xls"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    XML("xml", "text/xml"),
    APK("apk", "application/vnd.android.package-archive"),
    DOC("doc", "application/msword"),
    PDF("pdf", "application/pdf"),
    HTML("html", "text/html"),
    ZIP("zip", "application/zip"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    JSON("json", "application/json");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String name;
    private String contentType;

    MediaTypeConstants(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    public String getContentTypeWithCharset(String charsetStr) {
        return contentType + ";" + BaseConstants.CHARSET_STR + "=" + charsetStr;
    }

}

