package com.ttis.treenode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ttis.treenode.domain.TreeNode;
import com.ttis.treenode.dto.*;
import com.ttis.treenode.exception.NotFoundException;
import com.ttis.treenode.service.TreeNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by tap on 12/8/16.
 */
@RestController
@RequestMapping("/treenode")
public class TreeNodeController {
    private static final Logger logger = LoggerFactory.getLogger(TreeNodeController.class);

    @Value("${treenode.show-json-pretty:false}")
    boolean showJSONPretty;

    private static final ObjectMapper prettyPrintMapper = createObjectMapper();

    @Autowired
    protected TreeNodeService treeNodeService;

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    /**
     * We will try to convert to pretty print json
     *
     * @param object
     * @return
     */
    protected String createPrettyJSON(Object object) {
        String output = "";
        if ((null != object) & showJSONPretty) {
            StringWriter sw = new StringWriter();
            try {
                prettyPrintMapper.writeValue(sw, object);
                output = sw.toString();
            } catch (IOException e) {
                logger.warn("Could not convert object to JSON", e);
                output = object.toString();
            }
        } else if (null != object) {
            output = object.toString();
        }
        return output;
    }

    protected ResponseEntity<?> createSystemErrorResponse(Exception e) {

        ResponseEntity<?> responseEntity = null;
        if (e instanceof IllegalArgumentException) {
            IllegalArgumentException ie = (IllegalArgumentException) e;
            SimpleResponse response = new SimpleResponse(false);
            response.setMsg(ie.getMessage());
            responseEntity = new ResponseEntity<SimpleResponse>(response, HttpStatus.BAD_REQUEST);
        } else if (e instanceof NotFoundException) {
            NotFoundException ie = (NotFoundException) e;
            SimpleResponse response = new SimpleResponse(false);
            response.setMsg(ie.getMessage());
            responseEntity = new ResponseEntity<SimpleResponse>(response, HttpStatus.NOT_FOUND);
        } else if (e instanceof UnsupportedOperationException) {
            UnsupportedOperationException ie = (UnsupportedOperationException) e;
            SimpleResponse response = new SimpleResponse(false);
            response.setMsg(ie.getMessage());
            responseEntity = new ResponseEntity<SimpleResponse>(response, HttpStatus.NOT_ACCEPTABLE);
        } else {
            SimpleResponse response = new SimpleResponse(false);
            response.setMsg(e.getMessage());
            responseEntity = new ResponseEntity<SimpleResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
    @RequestMapping(value = "/{nodeId}/children", method = RequestMethod.GET)
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

    @RequestMapping(value = "/{nodeId}/descendants", method = RequestMethod.GET)
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

    /*
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
    */

    @RequestMapping(value = "/{nodeId}/ancestors", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAncestors(@PathVariable String nodeId){
        logger.debug("getAncestors(nodeId => {})", new Object[] {nodeId});
        ResponseEntity<?> responseEntity = null;

        FamilyListDTO descendantsDTO = null;
        try {
            Future<List<TreeNode>> mapFuture = treeNodeService.getRootToNodeList(nodeId);
            List<TreeNode> treeNodeMap = mapFuture.get();
            descendantsDTO = convertDescendantListToDTO(treeNodeMap,FamilyDTO.ANCESTORS);
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

    private FamilyListDTO convertDescendantListToDTO(List<TreeNode> treeNodeList, String family) {
        FamilyListDTO descendantsDTO = new FamilyListDTO();
        List<TreeNodeDTO> nodeDTOList = treeNodeList.stream()
                .map(node -> convertToTreeNodeDTO(node))
                .collect(Collectors.toList());
        Map<String, List<TreeNodeDTO>> familyMap = new HashMap<>();
        familyMap.put(family, nodeDTOList);
        descendantsDTO.setFamily(familyMap);
        return descendantsDTO;
    }
}
