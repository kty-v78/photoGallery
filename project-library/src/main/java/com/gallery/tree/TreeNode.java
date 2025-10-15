package com.gallery.tree;

import com.gallery.tree.metadata.PhotoMetadata;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private String name;
    private String path;
    private final List<TreeNode> childrenFolder;
    private final List<PhotoMetadata> photos;
    private TreeNode parentFolder;

    public TreeNode(String name) {
        this.name = name;
        this.path = name;
        this.childrenFolder = new ArrayList<>();
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public List<TreeNode> getChildrenFolder() {
        return childrenFolder;
    }

    public List<PhotoMetadata> getPhotos() {
        return photos;
    }

    public TreeNode getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(TreeNode parentFolder) {
        this.parentFolder = parentFolder;
    }

    public void addChild(TreeNode child) {
        child.setParentFolder(this);
        child.path = this.path + "/" + child.name;
        this.childrenFolder.add(child);
    }

    public void addPhoto(PhotoMetadata photo) {
        this.photos.add(photo);
    }

    public int getTotalPhotos() {
        int count = photos.size();
        for (TreeNode child : childrenFolder) {
            count += child.getTotalPhotos();
        }
        return count;
    }
}
