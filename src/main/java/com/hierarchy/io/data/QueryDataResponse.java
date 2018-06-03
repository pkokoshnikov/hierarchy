package com.hierarchy.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hierarchy.Tree;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class QueryDataResponse {
	private List<NodeResponse> nodes = new ArrayList<>();

	public QueryDataResponse(List<Tree.TreeNode> treeNodes) {
		this.nodes = treeNodes.stream().map(treeNode -> new NodeResponse(treeNode.getName(), treeNode.getNodeId(), treeNode.getParentId())).collect(Collectors.toList());
	}

	@Getter
	private static class NodeResponse {
		private String name;
		private String id;
		private String parentId;

		public NodeResponse(String name, String id, String parentId) {
			this.name = name;
			this.id = id;
			this.parentId = parentId;
		}

		@JsonProperty(value = "parent_id")
		public String getParentId() {
			return parentId;
		}
	}
}
