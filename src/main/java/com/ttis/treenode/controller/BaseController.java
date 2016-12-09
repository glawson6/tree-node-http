package com.ttis.treenode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ttis.treenode.dto.SimpleResponse;
import com.ttis.treenode.exception.NotFoundException;
import com.ttis.treenode.service.TreeNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by tap on 7/30/16.
 */
public class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

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
}
