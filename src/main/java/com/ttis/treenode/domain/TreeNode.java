package com.ttis.treenode.domain;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.ttis.treenode.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by tap on 12/7/16.
 */
public class TreeNode {

    private static final Logger logger = LoggerFactory.getLogger(TreeNode.class);

    private String id;
    private String description;

    private TreeNode parent = null;
    private Map<String, TreeNode> children = new HashMap<>();
    private static final Map<String, String> ids = new ConcurrentHashMap<>();


    public static final String ROOT_ID = "ROOT";
    public static final String EMPTY_STRING = "";


    private static TreeNode createRootNode() {
        ids.put(ROOT_ID, EMPTY_STRING);
        TreeNode treeNode = new TreeNode(ROOT_ID, "This is the root node");
        return treeNode;
    }

    public static final TreeNode ROOT = createRootNode();

    private TreeNode(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public static TreeNode createNode(String nodeId, String description) {
        return new TreeNode(nodeId, description);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TreeNode getParent() {
        return parent;
    }

    public static final String getPath(String nodeId) {
        return ids.get(nodeId);
    }

    static final Object lock = new Object();

    public TreeNode addChild(TreeNode child) {
        logger.debug("Entering addChild....");
        if (null == child) {
            throw new IllegalArgumentException("Cannot add null child!");
        }
        if (ids.keySet().contains(child.getId())) {
            StringBuilder sb = new StringBuilder("Cannot add child ");
            sb.append(child.toString()).append(" id is already taken!");
            throw new NotFoundException(sb.toString());
        }
        if (children.size() >= 15) {
            throw new UnsupportedOperationException("Cannot Add child node! Size at 15");
        } else {


            synchronized (lock) {
                String path = calculatePath();
                logger.debug("We calculated path => {} from node => {}", path, this.getId());
                ids.put(child.getId(), path);
                child.parent = this;
                children.put(child.getId(), child);

                logger.info("########### id {} parent {} ", new Object[]{id, parent});
            }
        }

        logger.debug("Leaving addChild....");
        return child;
    }

    private TreeNode removeChild(String nodeId) {
        if (null == nodeId) {
            throw new IllegalArgumentException("Cannot remove null nodeId!");
        }
        if (ROOT_ID.equals(nodeId)){
            throw new UnsupportedOperationException("Cannot Remove ROOT!");
        }
        if (!ids.keySet().contains(nodeId)) {
            StringBuilder sb = new StringBuilder("Cannot remove node with id ");
            sb.append(nodeId).append(". Node not found!");
            throw new NotFoundException(sb.toString());
        }
        TreeNode holder = null;
        synchronized (lock) {
            ids.remove(nodeId);
            holder = children.remove(nodeId);
        }
        // Remove holder's kids
        // TODO Revist this. not quite right.
        if (null != holder) {
            holder.children.entrySet().stream()
                    .map(entry -> entry.getValue().children)//Stream<Map<String, TreeNode>>
                    .forEach(map -> map.clear());
            holder = null;
        }
        return this;
    }

    public static TreeNode removeNode(TreeNode root, String nodeId) {
        if (null == nodeId) {
            throw new IllegalArgumentException("Cannot remove null nodeId!");
        }
        if (ROOT_ID.equals(nodeId)){
            throw new UnsupportedOperationException("Cannot Remove ROOT!");
        }
        if (!ids.keySet().contains(nodeId)) {
            StringBuilder sb = new StringBuilder("Cannot remove node with id ");
            sb.append(nodeId).append(". Node not found!");
            throw new NotFoundException(sb.toString());
        }
        TreeNode holder = findParentNode(root, nodeId);
        TreeNode parent = null;
        if (null != holder){
            parent = holder.getParent();
        }
        if (null == parent) {
            //We are at ROOT
            parent = ROOT;

        }

        return parent.removeChild(nodeId);
    }

    public static TreeNode findParentNode(TreeNode root, String nodeId) {
        logger.debug("Entering findParentNode....");
        if (null == nodeId) {
            throw new IllegalArgumentException("Cannot find null nodeId!");
        }
        if (!ids.keySet().contains(nodeId)) {
            StringBuilder sb = new StringBuilder("Node ");
            sb.append(nodeId).append(" does not exist!.");
            throw new NotFoundException(sb.toString());
        }
        String path = ids.get(nodeId);
        String[] pathArr = path.split("\\.");
        //logger.debug("path => {} pathArr size => {}", new Object[]{path, pathArr.length});
        TreeNode child = root;
        TreeNode parent = null;
        if (pathArr.length > 1) {
            int i = 1;
            while (null != child && i < pathArr.length) {
                String childId = pathArr[i];
                //logger.debug("Getting childId {}",childId);
                child = child.children.get(childId);
                //logger.debug("Got child {}",child);
                i++;

            }
            parent = child;
        } else {
            parent = root;
        }
        logger.debug("child => {}", child);
        logger.debug("parent => {}", parent);
        logger.debug("root => {}", root);
        logger.debug("Leaving findParentNode....");
        return parent;
    }

    public static TreeNode findNode(TreeNode root, String nodeId) {
        TreeNode parentNode = findParentNode(root, nodeId);
        return parentNode.getChildren().get(nodeId);
    }

    public static Map<String, TreeNode> findPathNodes(TreeNode root, String nodeId) {
        if (null == nodeId) {
            throw new IllegalArgumentException("Cannot find null nodeId!");
        }
        if (!ids.keySet().contains(nodeId)) {
            StringBuilder sb = new StringBuilder("Node ");
            sb.append(nodeId).append(" does not exist!.");
            throw new NotFoundException(sb.toString());
        }
        Map<String, TreeNode> pathNodes = new LinkedHashMap<>();
        String path = ids.get(nodeId);
        TreeNode parentNode = findParentNode(root, nodeId);
        TreeNode oneNode = parentNode.getChildren().get(nodeId);

        TreeNode node = oneNode;
        TreeNode parent = node.getParent();
        pathNodes.put(node.getId(), node);
        while (node != null) {
            node = node.parent;
            if (node != null) {
                pathNodes.put(node.getId(), node);
            }
        }
        return pathNodes;
    }



    public static List<TreeNode> findPathNodesList(TreeNode root, String nodeId) {
        if (null == nodeId) {
            throw new IllegalArgumentException("Cannot find null nodeId!");
        }
        if (!ids.keySet().contains(nodeId)) {
            StringBuilder sb = new StringBuilder("Node ");
            sb.append(nodeId).append(" does not exist!.");
            throw new NotFoundException(sb.toString());
        }
        List<TreeNode> pathNodes = new ArrayList<>();
        String path = ids.get(nodeId);
        TreeNode parentNode = findParentNode(root, nodeId);
        TreeNode oneNode = parentNode.getChildren().get(nodeId);

        TreeNode node = oneNode;
        TreeNode parent = node.getParent();
        pathNodes.add(node);
        while (node != null) {
            node = node.parent;
            if (node != null) {
                pathNodes.add(node);
            }
        }
        return pathNodes;
    }

    public Map<String, TreeNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    private String calculatePath() {
        Stack<String> pathStack = new Stack<>();
        LinkedList<String> someIds = new LinkedList<>();
        TreeNode node = this;
        pathStack.push(this.getId());
        someIds.offerFirst(this.getId());
        while (node.parent != null) {
            node = node.parent;
            pathStack.push(node.getId());
            someIds.offerFirst(node.getId());
        }
        String results2 = String.join(".", someIds);
        String results3 = String.join(".", pathStack);
        logger.info("results2 => {}, results3 => {}", new Object[]{results2, results3});
        String result = someIds.stream()
                .map(str -> str)
                .collect(Collectors.joining("."));
        return result;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TreeNode{");
            sb.append("id='").append(id).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append(", parent=").append(parent);
            sb.append(", lock=").append(lock);
            sb.append('}');
        return sb.toString();
    }

    public static Collection<String> allNodeIds() {
        return ids.keySet();
    }

    public static Map<String, String> nodeIdsMap() {
        return Collections.unmodifiableMap(ids);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeNode treeNode = (TreeNode) o;

        return id.equals(treeNode.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
