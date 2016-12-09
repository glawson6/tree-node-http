package com.ttis.treenode.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by tap on 12/8/16.
 */
public class TreeNodeDTO {

    @NotNull
    private String id;

    @NotNull
    private String description;

    public TreeNodeDTO() {
    }

    public TreeNodeDTO(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TreeNodeDTO{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
