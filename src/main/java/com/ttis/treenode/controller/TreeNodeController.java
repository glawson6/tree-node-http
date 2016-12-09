package com.ttis.treenode.controller;

import com.ttis.treenode.domain.TreeNode;
import com.ttis.treenode.dto.FamilyDTO;
import com.ttis.treenode.dto.NewNodeDTO;
import com.ttis.treenode.dto.SimpleResponse;
import com.ttis.treenode.dto.TreeNodeDTO;
import com.ttis.treenode.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by tap on 12/8/16.
 */
@RestController
@RequestMapping("/treenode")
public class TreeNodeController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @RequestMapping(value = "/children/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getChildren(@PathVariable String nodeId){
        logger.debug("getChildren(nodeId => {})", new Object[] {nodeId});
        ResponseEntity<?> responseEntity = null;

        FamilyDTO descendantsDTO = null;
        try {
            Future<Map<String, TreeNode>> mapFuture = treeNodeService.getChildren(nodeId);
            Map<String,TreeNode> treeNodeMap = mapFuture.get();
            descendantsDTO = convertDescendantMapToDTO(treeNodeMap, FamilyDTO.CHILDREN);
            if (null == descendantsDTO || null == descendantsDTO.getFamily() || descendantsDTO.getFamily().size() <= 0
                    || descendantsDTO.getFamily().get(FamilyDTO.CHILDREN).size() <= 0){
                StringBuilder message = new StringBuilder("No children found for ");
                message.append(nodeId);
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.NOT_FOUND);
            } else {
                responseEntity = new ResponseEntity<>(descendantsDTO , HttpStatus.OK);
            }
        } catch (ExecutionException ie){
            responseEntity = createSystemErrorResponse((Exception)ie.getCause());
        } catch (Exception ie){
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/descendants/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDescendants(@PathVariable String nodeId){
        logger.debug("getDescendants(nodeId => {})", new Object[] {nodeId});
        ResponseEntity<?> responseEntity = null;

        FamilyDTO descendantsDTO = null;
        try {
            Future<Map<String, TreeNode>> mapFuture = treeNodeService.getDescendants(nodeId);
            Map<String,TreeNode> treeNodeMap = mapFuture.get();
            descendantsDTO = convertDescendantMapToDTO(treeNodeMap,FamilyDTO.DESCENDANTS);
            if (null == descendantsDTO || null == descendantsDTO.getFamily() || descendantsDTO.getFamily().size() <= 0
                    || descendantsDTO.getFamily().get(FamilyDTO.DESCENDANTS).size() <= 0){
                StringBuilder message = new StringBuilder("No descendants found for ");
                message.append(nodeId);
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.NOT_FOUND);
            } else {
                responseEntity = new ResponseEntity<>(descendantsDTO , HttpStatus.OK);
            }
        } catch (ExecutionException ie){
            responseEntity = createSystemErrorResponse((Exception)ie.getCause());
        } catch (Exception ie){
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/ancestors/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAncestors(@PathVariable String nodeId){
        logger.debug("getAncestors(nodeId => {})", new Object[] {nodeId});
        ResponseEntity<?> responseEntity = null;

        FamilyDTO descendantsDTO = null;
        try {
            Future<Map<String, TreeNode>> mapFuture = treeNodeService.getRootToNode(nodeId);
            Map<String,TreeNode> treeNodeMap = mapFuture.get();
            descendantsDTO = convertDescendantMapToDTO(treeNodeMap,FamilyDTO.ANCESTORS);
            if (null == descendantsDTO || null == descendantsDTO.getFamily() || descendantsDTO.getFamily().size() <= 0
                    || descendantsDTO.getFamily().get(FamilyDTO.ANCESTORS).size() <= 0){
                StringBuilder message = new StringBuilder("No descendants found for ");
                message.append(nodeId);
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.NOT_FOUND);
            } else {
                responseEntity = new ResponseEntity<>(descendantsDTO , HttpStatus.OK);
            }
        } catch (ExecutionException ie){
            responseEntity = createSystemErrorResponse((Exception)ie.getCause());
        } catch (Exception ie){
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/paths", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAllPaths(){
        ResponseEntity<?> responseEntity = null;

        try {
            Map<String, String> treeNodeMap = treeNodeService.getAllNodeIdMap();
            if (null == treeNodeMap || treeNodeMap.size() <= 0){
                StringBuilder message = new StringBuilder("No nodes found");
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.NOT_FOUND);
            } else {
                responseEntity = new ResponseEntity<>(treeNodeMap , HttpStatus.OK);
            }
        }catch (Exception ie){
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addNode(@RequestBody @Valid NewNodeDTO newNodeDTO){
        logger.debug("addNode(newNodeDTO => {})", new Object[] {createPrettyJSON(newNodeDTO)});
        ResponseEntity<?> responseEntity = null;

        try {
            TreeNode newTreeNode = convertToTreeNode(newNodeDTO);
            Future<TreeNode> mapFuture = treeNodeService.addChildNode(newNodeDTO.getParentNodeId(), newTreeNode);
            TreeNode treeNode = mapFuture.get();
            if (null == treeNode ){
                StringBuilder message = new StringBuilder("Could not create node. ");
                message.append(newNodeDTO);
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setLocation(ServletUriComponentsBuilder
                        .fromCurrentRequest().path("/{id}")
                        .buildAndExpand(treeNode.getId()).toUri());
                responseEntity = new ResponseEntity<>(newNodeDTO, httpHeaders , HttpStatus.CREATED);
            }
        } catch (ExecutionException ie){
            responseEntity = createSystemErrorResponse((Exception)ie.getCause());
        } catch (Exception ie){
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getNode(@PathVariable String nodeId){
        logger.info("getNode(nodeId => {})", new Object[] {nodeId});
        ResponseEntity<?> responseEntity = null;

        try {
            Future<TreeNode> mapFuture = treeNodeService.getNode(nodeId);
            TreeNode treeNode = mapFuture.get();
            if (null == treeNode){
                StringBuilder message = new StringBuilder("No node found for ");
                message.append(nodeId);
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.NOT_FOUND);
            } else {
                TreeNodeDTO nodeDTO = convertToTreeNodeDTO(treeNode);
                responseEntity = new ResponseEntity<>(nodeDTO , HttpStatus.OK);
            }
        } catch (ExecutionException ie){
            responseEntity = createSystemErrorResponse((Exception)ie.getCause());
        } catch (Exception ie){
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{nodeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deletetNode(@PathVariable String nodeId){
        logger.debug("getNode(nodeId => {})", new Object[] {nodeId});
        ResponseEntity<?> responseEntity = null;

        try {
            Future<TreeNode> mapFuture = treeNodeService.removeNode(nodeId);
            TreeNode treeNode = mapFuture.get();
            if (null == treeNode){
                StringBuilder message = new StringBuilder("Node not found for ");
                message.append(nodeId);
                responseEntity = new ResponseEntity<>(new SimpleResponse(false, message.toString()), HttpStatus.NOT_FOUND);
            } else {
                StringBuilder message = new StringBuilder("Node at ");
                message.append(nodeId).append(" deleted.");
                responseEntity = new ResponseEntity<>(new SimpleResponse(true,message.toString()), HttpStatus.OK);
            }
        } catch (ExecutionException ie){
            ie.printStackTrace();
            responseEntity = createSystemErrorResponse((Exception)ie.getCause());
        } catch (Exception ie){
            ie.printStackTrace();
            responseEntity = createSystemErrorResponse(ie);
        }
        return responseEntity;
    }

    private TreeNodeDTO convertToTreeNodeDTO(TreeNode treeNode) {
        return new TreeNodeDTO(treeNode.getId(), treeNode.getDescription());
    }

    private TreeNode convertToTreeNode(NewNodeDTO newNodeDTO) {
        return TreeNode.createNode(newNodeDTO.getTreeNode().getId(), newNodeDTO.getTreeNode().getDescription());
    }

    private FamilyDTO convertDescendantMapToDTO(Map<String, TreeNode> treeNodeMap, String family) {
        FamilyDTO descendantsDTO = new FamilyDTO();
        Map<String, TreeNodeDTO> nodeDTOMap = treeNodeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> new TreeNodeDTO(entry.getValue().getId(), entry.getValue().getDescription())));
        Map<String, Map<String, TreeNodeDTO>> familyMap = new HashMap<>();
        familyMap.put(family, nodeDTOMap);
        descendantsDTO.setFamily(familyMap);
        return descendantsDTO;
    }
}
