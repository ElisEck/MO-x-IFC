package de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Quelle: https://github.com/gt4dev/yet-another-tree-structure/blob/master/java/src/com/tree/TreeNode.java
 * @param <T>
 */
public class TreeNode<T> implements Iterable<TreeNode<T>>{

    T data;
    TreeNode<T> parent;
    List<TreeNode<T>> children;
    private final Map<String, TreeNode<T>> index;

    public Map<String, TreeNode<T>> getIndex() {
        return index;
    }

    public TreeNode(T data, Map<String, TreeNode<T>> index) {
        this.data = data;
        this.index = index;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public TreeNode<T> addChild(T child) {
        return children.stream()
                .filter(c -> c.data.equals(child))
                .findAny()
                .orElseGet( () -> {
                            TreeNode<T> childNode = new TreeNode<T>(child, index);
                            childNode.parent = this;
                            this.children.add(childNode);
                            index.put(getFullPath(childNode), childNode);
                            return childNode;
                        }
                );
    }

    public String getFullPath() {
        return getFullPath(this);
    }

    public String getFullPath(TreeNode<T> childNode) {
        String path = "";
        if (childNode.parent != null) {
            path += getFullPath(childNode.parent) + ".";
        }
        path += childNode.data;
        return path;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }


    public TreeNode<T> findTreeNode(String fullPath) {
        return index.get(fullPath);
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        TreeNodeIter<T> iter = new TreeNodeIter<T>(this);
        return iter;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getData() {
        return data;
    }

    public List<TreeNode<T>> getSiblings() {
        if(parent == null) {
            return List.of();
        }
        return parent.getChildren().stream()
                .filter(c -> !c.equals(this))
                .collect(Collectors.toList());
    }
}
