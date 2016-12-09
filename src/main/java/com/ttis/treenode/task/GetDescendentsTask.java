package com.ttis.treenode.task;

import com.ttis.treenode.domain.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by tap on 12/7/16.
 */
public class GetDescendentsTask implements Callable<Map<String, TreeNode>> {

    private static final Logger logger = LoggerFactory.getLogger(GetDescendentsTask.class);

    private TreeNode node;
    private String nodeId;

    public GetDescendentsTask(TreeNode node, String nodeId) {
        this.node = node;
        this.nodeId = nodeId;
    }

    @Override
    public Map<String, TreeNode> call() throws Exception {
        //TreeNode oneNode = TreeNode.findNode(node, nodeId);
        // Any path that has the nodeId in should be considered.
        /*
        Map<String,String> paths = TreeNode.nodeIdsMap().entrySet().stream()
                .filter(entry -> entry.getValue().contains(nodeId))
                .collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> entry.getValue()));
                    */


        Map<String,TreeNode> descendents = TreeNode.nodeIdsMap().entrySet().stream()
                .filter(entry -> entry.getValue().contains(nodeId))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> TreeNode.findNode(node, entry.getKey())));

        /*
        Map<String, TreeNode> descendents =
                paths.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue()));
                                */
        return descendents;
    }
}
