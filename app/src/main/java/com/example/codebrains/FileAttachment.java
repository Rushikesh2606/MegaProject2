package com.example.codebrains;

public class FileAttachment {
    private String filename;
    private String size;

    public FileAttachment(String filename, String size) {
        this.filename = filename;
        this.size = size;
    }

    public String getFilename() { return filename; }
    public String getSize() { return size; }
}