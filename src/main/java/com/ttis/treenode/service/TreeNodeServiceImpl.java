package com.ttis.treenode.service;

import com.ttis.treenode.domain.TreeNode;
import com.ttis.treenode.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by tap on 12/7/16.
 * A node in the tree should have the following mandatory properties:
 A globally unique name
 Brief description
 Any additional properties on a node are up to the developer.
 A node in the tree can have a parent node, and can have 0 to 15 children (an attempt to add more than 15 child nodes to a parent will result in an error).  The tree managed by the service will have a single, parent node, named ‘root’, that will always exist and can never be modified or deleted.
 Functional Requirements:
 The service will provide resources to support the following:
 Add a node to the tree at a specific location (for instance, add a new node to a leaf node’s children)
 Retrieve a single node
 Retrieve the immediate children of a node
 Retrieve all descendants of a node (immediate children and nested children)
 For an arbitrary node, retrieve all ancestors/parents of the node (the path from the root node to the specific node).
 Remove a node from the tree (also removes all of its children)
 */
@Service(value = "treeNodeservice")
public class TreeNodeServiceImpl implements TreeNodeService {

    private static final Logger logger = LoggerFactory.getLogger(TreeNodeServiceImpl.class);

    @Autowired
    private ThreadPoolTaskExecutor executor;

    private static final TreeNode root = TreeNode.ROOT;



    @Override
    public Future<Map<String, TreeNode>> getChildren(String nodeId) {
        return executor.submit(new GetChildrenTask(root, nodeId));
    }

    @Override
    public Future<Map<String, TreeNode>> getDescendants(String nodeId) {
        return executor.submit(new GetDescendentsTask(root, nodeId));
    }

    @Override
    public Future<Map<String, TreeNode>> getRootToNode(String nodeId) {
        return executor.submit(new GetRootToNodeTask(root, nodeId));
    }

    @Override
    public Future<TreeNode> getNode(String nodeId) {
        return executor.submit(new GetNodeTask(root, nodeId));
    }

    @Override
    public Future<TreeNode> removeNode(String nodeId) {
        return executor.submit(new RemoveNodeTask(root, nodeId));
    }

    @Override
    public Future<TreeNode> addChildNode(String parentNodeId, TreeNode node) {
        return executor.submit(new AddChildTask(root, node, parentNodeId));
    }

    @Override
    public Collection<String> getAllNodeIds() {
        return TreeNode.allNodeIds();
    }

    @Override
    public Map<String, String> getAllNodeIdMap() {
        return TreeNode.nodeIdsMap();
    }


    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    /*
    @Override
    public Map<String, TreeNode> getChildren(String nodeId){
        TreeNode findNode = TreeNode.findParentNode(root,nodeId);
        return findNode.getChildren();
    }

    @Override
    public Map<String, TreeNode> getDescendants(String nodeId) {
        Map<String,TreeNode> descendents = TreeNode.nodeIdsMap().entrySet().stream()
                .filter(entry -> entry.getValue().contains(nodeId))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> TreeNode.findNode(root, entry.getKey())));
        return descendents;
    }

    @Override
    public Map<String, TreeNode> getRootToNode(String nodeId) {
        return TreeNode.findPathNodes(root, nodeId);
    }

    @Override
    public TreeNode getNode(String nodeId) {
        TreeNode findNode = TreeNode.findParentNode(root,nodeId);
        return findNode.getChildren().get(nodeId);
    }

    @Override
    public TreeNode removeNode(String nodeId) {
        return TreeNode.removeNode(root,nodeId);
    }

    @Override
    public TreeNode addChildNode(String parentNodeId, TreeNode node) {
        TreeNode findNode = TreeNode.findNode(root,parentNodeId);
        TreeNode added = null;
        if (findNode == null){
            added = TreeNode.ROOT.addChild(node);
        } else {
            added = findNode.addChild(node);
        }
        logger.info("TreeNode {}",TreeNode.ROOT.toString());
        logger.info(" We want to add node {} as a child to node {} with findNode => {}", new Object[]{node.getId(), parentNodeId,findNode});
        logger.info(" We actually added node {} as a child to node {} with findNode => {}", new Object[]{added.getId(), added.getParent().getId(),findNode});
        //logger.info("AddChildTask node => {} findNode => {}",nodeId, findNode);
        //logger.info("add child node => {}",added);
        logger.info("AllIdsMap => {}",TreeNode.nodeIdsMap());
        return added;
    }

    @Override
    public Collection<String> getAllNodeIds() {
        return TreeNode.allNodeIds();
    }

    @Override
    public Map<String, String> getAllNodeIdMap() {
        return TreeNode.nodeIdsMap();
    }
    */
}
