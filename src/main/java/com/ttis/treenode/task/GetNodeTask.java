package com.ttis.treenode.task;

import com.ttis.treenode.domain.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Created by tap on 12/7/16.
 */
public class GetNodeTask implements Callable<TreeNode> {

    private static final Logger logger = LoggerFactory.getLogger(GetNodeTask.class);

    private TreeNode node;
    private String nodeId;

    public GetNodeTask(TreeNode node, String nodeId) {
        this.node = node;
        this.nodeId = nodeId;
    }

    @Override
    public TreeNode call() throws Exception {
        TreeNode findNode = TreeNode.findParentNode(node,nodeId);
        return findNode.getChildren().get(nodeId);
    }
}
