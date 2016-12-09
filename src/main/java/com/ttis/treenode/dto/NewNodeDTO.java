package com.ttis.treenode.dto;

/**
 * Created by tap on 12/8/16.
 */
public class NewNodeDTO {

    private String parentNodeId;
    private TreeNodeDTO treeNode;

    public NewNodeDTO(String parentNodeId, TreeNodeDTO treeNode) {
        this.parentNodeId = parentNodeId;
        this.treeNode = treeNode;
    }

    public NewNodeDTO() {
    }

    public String getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public TreeNodeDTO getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNodeDTO treeNode) {
        this.treeNode = treeNode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NewNodeDTO{");
        sb.append("parentNodeId='").append(parentNodeId).append('\'');
        sb.append(", treeNode=").append(treeNode);
        sb.append('}');
        return sb.toString();
    }
}
