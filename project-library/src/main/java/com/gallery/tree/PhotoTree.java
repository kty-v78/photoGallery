package com.gallery.tree;

import com.gallery.tree.metadata.PhotoMetadata;

import java.io.File;
import java.util.*;

public class PhotoTree {
    private TreeNode root;
    private int countPhoto;

    public PhotoTree(String rootName) {
        this.root = new TreeNode(rootName);
        this.countPhoto = 0;
    }

    public TreeNode getRoot() {
        return root;
    }

    public int getCountPhoto() {
        return countPhoto;
    }

    public void addPhoto(String folderPath, PhotoMetadata photo) {
        TreeNode folder = findOrCreateFolder(folderPath);
        folder.addPhoto(photo);
        countPhoto++;
    }

    private TreeNode findOrCreateFolder(String folderPath) {
        if (folderPath == null || folderPath.isEmpty() || folderPath.equals(root.getName())) {
            return root;
        }

        String[] pathParts = folderPath.split("/");
        TreeNode current = root;

        for (String part : pathParts) {
            if (part.isEmpty()) continue;

            TreeNode found = null;
            for (TreeNode child : current.getChildrenFolder()) {
                if (child.getName().equals(part)) {
                    found = child;
                    break;
                }
            }

            if (found == null) {
                found = new TreeNode(part);
                current.addChild(found);
            }
            current = found;
        }

        return current;
    }

    public Map<String, List<PhotoMetadata>> findDuplicates() {
        Map<String, List<PhotoMetadata>> duplicates = new HashMap<>();
        Map<String, List<PhotoMetadata>> fileMap = new HashMap<>();

        List<PhotoMetadata> allPhotos = getAllPhotos();

        for (PhotoMetadata photo : allPhotos) {
            String hash = photo.getHash();
            fileMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(photo);
        }

        for (Map.Entry<String, List<PhotoMetadata>> entry : fileMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }

        return duplicates;
    }

    public void deleteDuplicates() {
        Map<String, List<PhotoMetadata>> duplicates = findDuplicates();
        if (duplicates.isEmpty()) {
            System.out.println("Дубликатов не найдено.");
            return;
        }

        int deletedCount = 0;

        for (List<PhotoMetadata> photoList : duplicates.values()) {
            for (int i = 1; i < photoList.size(); i++) {
                PhotoMetadata photo = photoList.get(i);
                File file = new File(photo.getFilePath());
                if (file.exists() && file.delete()) {
                    System.out.println("Удалено: " + photo.getFilePath());
                    deletedCount++;
                } else {
                    System.out.println("Не удалось удалить: " + photo.getFilePath());
                }
            }
        }

        System.out.println("Удалено файлов-дубликатов: " + deletedCount);
    }

    public List<PhotoMetadata> getAllPhotos() {
        List<PhotoMetadata> allPhotos = new ArrayList<>();
        collectPhotos(root, allPhotos);
        return allPhotos;
    }

    private void collectPhotos(TreeNode node, List<PhotoMetadata> photos) {
        photos.addAll(node.getPhotos());
        for (TreeNode child : node.getChildrenFolder()) {
            collectPhotos(child, photos);
        }
    }

    public void organizeByDate() {
        List<PhotoMetadata> allPhotos = getAllPhotos();

        root.getChildrenFolder().clear();
        root.getPhotos().clear();
        countPhoto = 0;

        for (PhotoMetadata photo : allPhotos) {
            String dateFolder = photo.getDateFolder();
            addPhoto(dateFolder, photo);
        }
    }

    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(TreeNode node, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }

        if (depth == 0) {
            System.out.println(indent + node.getName() + " (" + root.getTotalPhotos() + " фото)");
        } else {
            System.out.println(indent + node.getName() + " (" + node.getTotalPhotos() + " фото)");
        }

        for (TreeNode child : node.getChildrenFolder()) {
            printTree(child, depth + 1);
        }

        for (PhotoMetadata photo : node.getPhotos()) {
            System.out.println(indent + "  " + photo.getFileName() +
                    " (" + photo.getFormattedSize() + ")");
        }
    }


    public List<PhotoMetadata> findPhotosByName(String name) {
        List<PhotoMetadata> result = new ArrayList<>();
        searchPhotosByName(root, name.toLowerCase(), result);
        return result;
    }

    private void searchPhotosByName(TreeNode node, String searchName, List<PhotoMetadata> result) {
        for (PhotoMetadata photo : node.getPhotos()) {
            if (photo.getFileName().toLowerCase().contains(searchName)) {
                result.add(photo);
            }
        }
        for (TreeNode child : node.getChildrenFolder()) {
            searchPhotosByName(child, searchName, result);
        }
    }
}