package com.ttis.treenode.dto;

import com.ttis.treenode.domain.TreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tap on 12/9/16.
 */
public class FamilyListDTO {

    Map<String, List<TreeNodeDTO>> family = new HashMap<>();

    public Map<String, List<TreeNodeDTO>> getFamily() {
        return family;
    }

    public void setFamily(Map<String, List<TreeNodeDTO>> family) {
        this.family = family;
    }
}
