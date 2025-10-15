package com.gallery.tree.metadata;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PhotoMetadata {
    private final String fileName;
    private final String filePath;
    private final long fileSize;
    private final LocalDateTime creationDate;
    private final String fileExtension;

    public PhotoMetadata(File file) {
        this.fileName = file.getName();
        this.filePath = file.getAbsolutePath();
        this.fileSize = file.length();
        this.fileExtension = getFileExtension(fileName);

        LocalDateTime tempDate = LocalDateTime.now();
        try {
            Path path = file.toPath();
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            tempDate = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
        } catch (Exception ignored) {}
        this.creationDate = tempDate;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getHash() {
        return fileName + "_" + fileSize + "_" +
                (creationDate != null ? creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
    }

    public String getDateFolder() {
        if (creationDate != null) {
            return creationDate.format(DateTimeFormatter.ofPattern("yyyy/MMM"));
        }
        return "дата неизвестна";
    }

    public String getFormattedSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        }
        if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        }
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1).toLowerCase();
    }

    public static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif");
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s",
                fileName, getFormattedSize(),
                creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                filePath);
    }
}
