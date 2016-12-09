package com.ttis.treenode.task;

import com.ttis.treenode.domain.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by tap on 12/7/16.
 */
public class RemoveNodeTask implements Callable<TreeNode> {

    private static final Logger logger = LoggerFactory.getLogger(RemoveNodeTask.class);

    private TreeNode node;
    private String nodeId;

    public RemoveNodeTask(TreeNode node, String nodeId) {
        this.node = node;
        this.nodeId = nodeId;
    }

    @Override
    public TreeNode call() throws Exception {
        TreeNode treeNode = null;
        if (TreeNode.ROOT_ID.equals(nodeId)) {
            throw new UnsupportedOperationException("Cannot Remove ROOT!");
        }
        Map<String, TreeNode> allNodes = new HashMap<>();
        Map<String, TreeNode> descendents = TreeNode.nodeIdsMap().entrySet().stream()
                .filter(entry -> entry.getValue().contains(nodeId))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> TreeNode.findNode(node, entry.getKey())));
        allNodes.putAll(descendents);

        allNodes.entrySet().stream()
                .forEach(entry -> {
                    TreeNode.removeNode(entry.getValue(), entry.getValue().getId());
                });
        treeNode = TreeNode.removeNode(node, nodeId);
        return treeNode;
    }
}
