package com.ttis.treenode.service;

import com.ttis.treenode.TreeNodeTestConfig;
import com.ttis.treenode.domain.TreeNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
/**
 * Created by tap on 12/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TreeNodeTestConfig.class, loader=AnnotationConfigContextLoader.class)
@ActiveProfiles({"test"})
public class TreeNodeServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TreeNodeServiceTest.class);

    /*
    @InjectMocks
    TreeNodeService treeNodeService;
    */

    @Autowired
    TreeNodeService treeNodeService;

    /*
    @Mock
    MovieRatingService movieRatingService;

*/
    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    private static final List<String> SOME_IDS = new ArrayList<String>(){{
       add(UUID.randomUUID().toString());
        add(UUID.randomUUID().toString());
        add(UUID.randomUUID().toString());
        add(UUID.randomUUID().toString());
        add(UUID.randomUUID().toString());
    }};

    private static List<TreeNode> SOME_NODES = new ArrayList<TreeNode>(){{
            add(TreeNode.createNode(SOME_IDS.get(0), "Some descriptive node"));
            add(TreeNode.createNode(SOME_IDS.get(1), "part two descriptive node"));
        add(TreeNode.createNode(SOME_IDS.get(2), "part three descriptive node"));
    }};

    @Test
    public void test_AddChildNode() throws ExecutionException, InterruptedException {
        // Arrange
        TreeNode testNode = createANode();
        // Act
        Future<TreeNode> nodeFuture = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode);
        TreeNode resultNode = nodeFuture.get();
        // Assert
        Assert.assertEquals("The parent node should be root",TreeNode.ROOT, resultNode.getParent());
        Assert.assertEquals("The testNode id should equal the resultNode id",testNode.getId(), resultNode.getId());
        Assert.assertEquals("The testNode should equal the ROOTnode.get",resultNode,TreeNode.ROOT.getChildren().get(testNode.getId()));
    }

    @Test
    public void test_GetChildren() throws ExecutionException, InterruptedException {
        // Arrange
        String nodeIdGD1 = "GC1";
        TreeNode testNode = TreeNode.createNode(nodeIdGD1, "Some descriptive node");
        Future<TreeNode> nodeFuture1 = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode);
        testNode = nodeFuture1.get();
        final String nodeIdGD2 = "GC2";
        String description = "The get node description";
        TreeNode testNode2 = TreeNode.createNode(nodeIdGD2, description);
        Future<TreeNode> nodeFuture2 = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode2);
        testNode2 = nodeFuture2.get();
        final String nodeIdParent = testNode2.getId();
        logger.info("test_GetDescendants nodeIdParent => {}",nodeIdParent);
        final String nodeIdGD3 = "GC3";
        TreeNode testNode3 = TreeNode.createNode(nodeIdGD3, "Some descriptive node");
        Future<TreeNode> nodeFuture = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode3);
        TreeNode testNode4 = nodeFuture.get();

        Set<String> sampleIds = new HashSet<String>(){{
            add(nodeIdGD1);
            add(nodeIdGD2);
            add(nodeIdGD3);
        }};

        // Act
        Future<Map<String, TreeNode>> nodeFuture5 = treeNodeService.getChildren(TreeNode.ROOT.getId());
        Map<String, TreeNode> resultNode = nodeFuture5.get();
        logger.info("@@@@@@@@ resultNode keyset {}",resultNode.keySet());
        // Assert
        sampleIds.stream()
                .forEach(id -> {
                    Assert.assertTrue("Returned children have node ids.", resultNode.keySet().contains(id));
                });
    }
    @Test
    public void test_RemoveNode() throws ExecutionException, InterruptedException {
        // Arrange
        TreeNode testNode = createANode();
        SOME_NODES.stream()
                .forEach(node -> {
                    Future<TreeNode> nodeFuture = treeNodeService.addChildNode(TreeNode.ROOT.getId(), node);
                });
        Future<TreeNode> nodeFuture1 = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode);

        List<String> allIdsBefore = new ArrayList<>(Collections.unmodifiableCollection(treeNodeService.getAllNodeIds()));
        // Act
        Future<TreeNode> nodeResults = treeNodeService.removeNode(testNode.getId());
        TreeNode resultNode = nodeResults.get();
        List<String> allIdsAfter = new ArrayList<>(Collections.unmodifiableCollection(treeNodeService.getAllNodeIds()));
        logger.info("allIdsBefore => {}, allIdsAfter => {}",new Object[]{allIdsBefore, allIdsAfter});
        // Assert
        Assert.assertTrue("The node should be in the id list",allIdsBefore.contains(testNode.getId()));
        Assert.assertFalse("The node should not be in the id list",allIdsAfter.contains(testNode.getId()));
    }

    @Test
    public void test_GetNodeTask() throws ExecutionException, InterruptedException {
        // Arrange
        String nodeId = "GETNODE";
        String description = "The get node description";
        TreeNode testNode = TreeNode.createNode(nodeId, description);
        Future<TreeNode> nodeFuture = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode);
        TreeNode testNodeResults = nodeFuture.get();

        // Act
        Future<TreeNode> nodeResults = treeNodeService.getNode(testNode.getId());

        TreeNode resultNode = nodeResults.get();
        // Assert
        Assert.assertEquals("The nodes should be equal",testNode, resultNode);
    }


    @Test
    public void test_GetDescendants() throws ExecutionException, InterruptedException {
        // Arrange
        String nodeIdGD1 = "GD1";
        TreeNode testNode = TreeNode.createNode(nodeIdGD1, "Some descriptive node");
        Future<TreeNode> nodeFuture1 = treeNodeService.addChildNode(TreeNode.ROOT.getId(), testNode);
        testNode = nodeFuture1.get();
        final String nodeIdGD2 = "GD2";
        String description = "The get node description";
        TreeNode testNode2 = TreeNode.createNode(nodeIdGD2, description);
        Future<TreeNode> nodeFuture2 = treeNodeService.addChildNode(testNode.getId(), testNode2);
        testNode2 = nodeFuture2.get();
        final String nodeIdParent = testNode2.getId();
        logger.info("test_GetDescendants nodeIdParent => {}",nodeIdParent);
        final String nodeIdGD3 = "GD3";
        TreeNode testNode3 = TreeNode.createNode(nodeIdGD3, "Some descriptive node");
        Future<TreeNode> nodeFuture = treeNodeService.addChildNode( testNode2.getId(), testNode3);
        TreeNode testNode4 = nodeFuture.get();

        Map<String, String> allIdsMap = treeNodeService.getAllNodeIdMap();
        logger.info("AllIdsMap => {}",TreeNode.nodeIdsMap());


        // Act
        Future<Map<String, TreeNode>> nodeFuture5 = treeNodeService.getDescendants(nodeIdGD1);
        Map<String, TreeNode> treeNodes = nodeFuture5.get();
        logger.info("{} descendents {}",nodeIdGD1, treeNodes.keySet());
        // Assert
        Assert.assertTrue("The descendents should have keys",treeNodes.keySet().contains(nodeIdGD2) && treeNodes.keySet().contains(nodeIdGD3));


    }
    private TreeNode createANode() {
        String nodeId = "ONE";
        String description = "The first node and Parent";
        TreeNode parent = TreeNode.createNode(nodeId, description);
        return parent;
    }


}
