package com.ttis.treenode.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tap on 12/8/16.
 */
public class FamilyDTO {

    public final static String DESCENDANTS = "descendants";
    public final static String CHILDREN = "children";
    public final static String ANCESTORS = "ancestors";

    private Map<String,Map<String, TreeNodeDTO>> family = new HashMap<>();

    public Map<String, Map<String, TreeNodeDTO>> getFamily() {
        return family;
    }

    public void setFamily(Map<String, Map<String, TreeNodeDTO>> family) {
        this.family = family;
    }
}
