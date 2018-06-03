package com.hierarchy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hierarchy.exception.TreeOperationException;
import com.hierarchy.io.data.AddNodeDataRequest;
import com.hierarchy.io.data.DeleteNodeDataRequest;
import com.hierarchy.io.data.MoveNodeDataRequest;
import com.hierarchy.io.data.QueryDataRequest;
import com.hierarchy.io.data.QueryDataResponse;
import com.hierarchy.io.data.SimpleDataResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class HierarchyService {
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static Tree tree = new Tree();

	public static void main(String[] args) {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			String inputMessage;
			while ((inputMessage = in.readLine()) != null && inputMessage.length() != 0) {
				try {
					Map<String, Map> inputValue = objectMapper.readValue(inputMessage, new TypeReference<Map<String, Map>>() {});
					String type = inputValue.keySet().iterator().next();

					switch (type) {
						case "add_node" :
							AddNodeDataRequest add = objectMapper.convertValue(inputValue.get(type), AddNodeDataRequest.class);
							tree.addNode(add.getName(), add.getId(), add.getParentId());
							successResponse();
							break;
						case "delete_node":
							DeleteNodeDataRequest delete = objectMapper.convertValue(inputValue.get(type), DeleteNodeDataRequest.class);
							tree.deleteNode(delete.getId());
							successResponse();
							break;
						case "move_node":
							MoveNodeDataRequest move = objectMapper.convertValue(inputValue.get(type), MoveNodeDataRequest.class);
							tree.moveNode(move.getId(), move.getNewParentId());
							successResponse();
							break;
						case "query":
							QueryDataRequest query = objectMapper.convertValue(inputValue.get(type), QueryDataRequest.class);
							List<Tree.TreeNode> nodes = tree.searchNodes(query.getMinDepth(), query.getMaxDepth(), query.getRootIds(), query.getNames(), query.getIds());
							System.out.println(objectMapper.writeValueAsString(new QueryDataResponse(nodes)));
							break;
						default:
							throw new IllegalArgumentException("Undefined type operation " + type);
					}
				} catch (NullPointerException | TreeOperationException | JsonParseException | IllegalArgumentException e ) {
					e.printStackTrace();
					failureResponse();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void successResponse() throws JsonProcessingException {
		System.out.println(objectMapper.writeValueAsString(SimpleDataResponse.SUCCESS));
	}

	private static void failureResponse() throws JsonProcessingException {
		System.out.println(objectMapper.writeValueAsString(SimpleDataResponse.FAILURE));
	}
}
