package com.hierarchy.io.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class QueryDataRequest {
	@JsonProperty(value = "min_depth")
	private Integer minDepth;
	@JsonProperty(value = "max_depth")
	private Integer maxDepth;
	private List<String> names;
	private List<String> ids;
	@JsonProperty(value = "root_ids")
	private List<String> rootIds;
}
