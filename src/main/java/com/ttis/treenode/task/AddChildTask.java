package com.ttis.treenode.task;

import com.ttis.treenode.domain.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Created by tap on 12/7/16.
 */
public class AddChildTask implements Callable<TreeNode> {

    private static final Logger logger = LoggerFactory.getLogger(AddChildTask.class);

    private TreeNode root;
    private TreeNode node;
    private String nodeId;

    public AddChildTask(TreeNode root, TreeNode node, String nodeId) {
        this.root = root;
        this.node = node;
        this.nodeId = nodeId;
    }

    @Override
    public TreeNode call() throws Exception {
        TreeNode findNode = TreeNode.findNode(root,nodeId);
        TreeNode added = null;
        if (findNode == null){
            added = TreeNode.ROOT.addChild(node);
        } else {
            added = findNode.addChild(node);
        }
        /*
        logger.debug("TreeNode {}",TreeNode.ROOT.toString());
        logger.debug(" We want to add node {} as a child to node {} with findNode => {}", new Object[]{node.getId(), nodeId,findNode});
        logger.debug(" We actually added node {} as a child to node {} with findNode => {}", new Object[]{added.getId(), added.getParent().getId(),findNode});
        //logger.info("AddChildTask node => {} findNode => {}",nodeId, findNode);
        //logger.info("add child node => {}",added);
        logger.debug("AllIdsMap => {}",TreeNode.nodeIdsMap());
        */
        return added;
    }
}
