package com.hierarchy.io.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MoveNodeDataRequest {
	private String id;
	@JsonProperty(value = "new_parent_id")
	private String newParentId;
}
