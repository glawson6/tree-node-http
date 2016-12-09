package com.ttis.treenode.service;

import com.ttis.treenode.domain.TreeNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by tap on 12/7/16.
 * Design and implement a RESTful web service which manages an arbitrarily structured tree or hierarchy of nodes.
 A node in the tree should have the following mandatory properties:
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
public interface TreeNodeService {


    Future<Map<String, TreeNode>> getChildren(String nodeId);
    Future<Map<String, TreeNode>> getDescendants(String nodeId);
    Future<Map<String, TreeNode>> getRootToNode(String nodeId);
    Future<List<TreeNode>> getRootToNodeList(String nodeId);
    Future<TreeNode> getNode(String nodeId);
    Future<TreeNode> removeNode(String nodeId);
    Future<TreeNode> addChildNode(String parentNodeId, TreeNode node);


    /*
    Map<String, TreeNode> getChildren(String nodeId);
    Map<String, TreeNode> getDescendants(String nodeId);
    Map<String, TreeNode> getRootToNode(String nodeId);
    TreeNode getNode(String nodeId);
    TreeNode removeNode(String nodeId);
    TreeNode addChildNode(String parentNodeId, TreeNode node);
    */

    Collection<String> getAllNodeIds();
    Map<String, String> getAllNodeIdMap();

}
