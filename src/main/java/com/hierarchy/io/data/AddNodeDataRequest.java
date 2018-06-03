package com.hierarchy.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AddNodeDataRequest {
	private String id;
	private String name;
	@JsonProperty(value = "parent_id")
	private String parentId;
}
