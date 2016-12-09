package com.ttis.treenode.task;

import com.ttis.treenode.domain.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by tap on 12/7/16.
 */
public class GetChildrenTask implements Callable<Map<String, TreeNode>> {

    private static final Logger logger = LoggerFactory.getLogger(GetChildrenTask.class);

    private TreeNode node;
    private String nodeId;

    public GetChildrenTask(TreeNode node, String nodeId) {
        this.node = node;
        this.nodeId = nodeId;
    }

    @Override
    public Map<String, TreeNode> call() throws Exception {
        TreeNode findNode = null;
        if (TreeNode.ROOT_ID.equals(nodeId)){
            findNode = TreeNode.ROOT;
        } else {
            findNode = TreeNode.findNode(node,nodeId);
        }
        return findNode.getChildren();
    }
}
