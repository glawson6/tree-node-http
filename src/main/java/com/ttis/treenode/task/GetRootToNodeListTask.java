package com.ttis.treenode.task;

import com.ttis.treenode.domain.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by tap on 12/7/16.
 */
public class GetRootToNodeListTask implements Callable<List<TreeNode>> {

    private static final Logger logger = LoggerFactory.getLogger(GetRootToNodeListTask.class);

    private TreeNode node;
    private String nodeId;

    public GetRootToNodeListTask(TreeNode node, String nodeId) {
        this.node = node;
        this.nodeId = nodeId;
    }

    @Override
    public List<TreeNode> call() throws Exception {
        return TreeNode.findPathNodesList(node, nodeId);
    }
}
