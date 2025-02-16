package cn.ximuli.jframex.common.constants;

public enum ContentTypeConstants {
    URLENCODED("x-www-form-urlencoded", "application/x-www-form-urlencoded"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    TEXT("text", "text/plain"),
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("javascript", "application/javascript"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpg"),
    GIF("gif", "image/gif"),
    PDF("pdf", "application/pdf"),
    WORD("doc", "application/msword"),
    EXCEL("xls", "application/vnd.ms-excel"),
    POWERPOINT("ppt", "application/vnd.ms-powerpoint"),
    MP3("mp3", "audio/mpeg"),
    MP4("mp4", "video/mp4"),
    MKV("mkv", "video/x-matroska"),
    AVI("avi", "video/x-msvideo"),
    FLV("flv", "video/x-flv");
    
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

    ContentTypeConstants(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }
}

