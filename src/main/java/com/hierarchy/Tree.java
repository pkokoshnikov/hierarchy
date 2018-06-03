package com.hierarchy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.hierarchy.exception.TreeOperationException;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import static com.hierarchy.exception.TreeOperationException.Code.*;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@ToString
public class Tree {
	private final Map<String, TreeNode> indexTreeNode = new HashMap<>();
	private TreeNode root;

	public void addNode(@NonNull String name, @NonNull String nodeId, String parentId) throws TreeOperationException {
		if(name.length() == 0 || nodeId.length() == 0) throw new TreeOperationException("Name or nodeId cannot be empty", EMPTY_VALUE);

		if(parentId == null || parentId.length() == 0) {
			if(root != null) throw new TreeOperationException("Root already exists", NODE_ALREADY_EXISTS);

			root = new TreeNode(name, nodeId);
			putNodeToIndex(nodeId, this.root);
			return;
		}

		TreeNode parentNode = getNodeFromIndex(parentId);
		TreeNode newNode = new TreeNode(name, nodeId, parentNode);
		if(nodeExists(nodeId)) throw new TreeOperationException(format("Node with such id %s already exists", nodeId), NODE_ALREADY_EXISTS);
		parentNode.addChild(newNode);
		putNodeToIndex(nodeId, newNode);
	}

	public void deleteNode(@NonNull String nodeId) throws TreeOperationException {
		TreeNode treeNode = getNodeFromIndex(nodeId);
		if(treeNode.getChildren().size() != 0) throw new TreeOperationException(format("Node with id %s contains children and cannot be deleted", nodeId), CHILDREN_ARE_NOT_EMPTY);
		if(treeNode.getParent() == null) {
			root = null;
			removeNodeFromIndex(nodeId);
		} else {
			treeNode.getParent().removeChild(nodeId);
			removeNodeFromIndex(nodeId);
		}
	}

	public void moveNode(@NonNull String nodeId, @NonNull String newParentId) throws TreeOperationException {
		TreeNode treeNode = getNodeFromIndex(nodeId);
		TreeNode newParentNode = getNodeFromIndex(newParentId);

		if(treeNode == newParentNode) throw new TreeOperationException("Circle self is detected", CIRCLE_DETECTED);

		TreeNode tempNode = newParentNode;
		while (tempNode != null) {
			tempNode = tempNode.getParent();
			if (tempNode == treeNode) {
				throw new TreeOperationException(format("Circle is detected id %s with new parent id %s", nodeId, newParentId), CIRCLE_DETECTED);
			}
		}

		treeNode.updateParent(newParentNode);
	}

	public List<TreeNode> searchNodes(Integer minDepth, Integer maxDepth, List<String> rootIds, List<String> names, List<String> ids) throws TreeOperationException {
		//set default non null values
		rootIds = ofNullable(rootIds).orElse(Collections.emptyList());
		minDepth = ofNullable(minDepth).orElse(Integer.MIN_VALUE);
		maxDepth = ofNullable(maxDepth).orElse(Integer.MAX_VALUE);
		ids = ofNullable(ids).orElse(Collections.emptyList());
		names = ofNullable(names).orElse(Collections.emptyList());

		if(rootIds.isEmpty()) {
			return innerSearchNodes(root, minDepth, maxDepth, ids, names);
		}

		List<TreeNode> localRoots = new LinkedList<>();

		for (String rootId : rootIds) {
			if(nodeExists(rootId)) {
				localRoots.add(getNodeFromIndex(rootId));
			}
		}

		List<TreeNode> result = new LinkedList<>();
		for (TreeNode localRoot : localRoots) {
			result.addAll(innerSearchNodes(localRoot, minDepth, maxDepth, ids, names));
		}

		return result;
	}

	private List<TreeNode> innerSearchNodes(TreeNode root, int minDepth, int maxDepth, List<String> ids, List<String> names) {
		List<TreeNode> result = new LinkedList<>();
		Map<String, Integer> depthMap = new HashMap<>();
		Map<String, Queue<TreeNode>> sortedChildren = new HashMap<>();// map with sorted non visited children, to prevent sorting and filtering every iteration
		Set<String> visitedNodeIds = new HashSet<>();

		Stack<TreeNode> stack = new Stack<>();
		stack.add(root);
		depthMap.put(root.getNodeId(), 0);

		while (!stack.isEmpty()) {
			TreeNode currentNode = stack.peek();
			int currentDepth = depthMap.get(currentNode.getNodeId());

			if(!visitedNodeIds.contains(currentNode.getNodeId())) {
				boolean addToResult = true;

				if(currentDepth < minDepth) addToResult = false;
				if(!ids.isEmpty() && !ids.contains(currentNode.getNodeId())) addToResult = false;
				if(!names.isEmpty() && !names.contains(currentNode.getName())) addToResult = false;

				if(addToResult) {
					result.add(currentNode);
				}
				visitedNodeIds.add(currentNode.getNodeId());
			}

			if(currentDepth + 1 > maxDepth) {
				stack.pop();
				continue;
			}

			Queue<TreeNode> nonVisitedChildren = sortedChildren.computeIfAbsent(currentNode.getNodeId(), key -> currentNode.getChildren().stream()
					.sorted(Comparator.comparing(TreeNode::getName)).collect(Collectors.toCollection(LinkedList::new))); // computes first time only for every node

			if(nonVisitedChildren.isEmpty()) {
				stack.pop();
				continue;
			}

			TreeNode nonVisitedNode = nonVisitedChildren.poll();
			depthMap.put(nonVisitedNode.getNodeId(), currentDepth + 1);
			stack.push(nonVisitedNode);
		}

		return result;
	}

	private boolean nodeExists(String nodeId) {
		return indexTreeNode.containsKey(nodeId);
	}

	private TreeNode getNodeFromIndex(String nodeId) throws TreeOperationException {
		return ofNullable(indexTreeNode.get(nodeId)).orElseThrow(() -> new TreeOperationException(format("Node with id %s is not found", nodeId), NODE_IS_NOT_FOUND));
	}

	private void removeNodeFromIndex(String nodeId) throws TreeOperationException {
		ofNullable(indexTreeNode.remove(nodeId)).orElseThrow(() -> new TreeOperationException(format("Node with id %s is not found", nodeId), NODE_IS_NOT_FOUND));
	}

	private void putNodeToIndex(String id, TreeNode node) {
		indexTreeNode.put(id, node);
	}

	@Getter
	@ToString
	public class TreeNode {
		private final Set<TreeNode> children = new HashSet<>();
		private TreeNode parent;
		private String name;
		private String nodeId;

		public TreeNode(@NonNull String name, @NonNull String nodeId) {
			this(name, nodeId, null);
		}

		public TreeNode(@NonNull String name, @NonNull String nodeId, TreeNode parent) {
			this.parent = parent;
			this.name = name;
			this.nodeId = nodeId;
		}

		public void addChild(@NonNull TreeNode node) throws TreeOperationException {
			if(children.stream().anyMatch(child -> child.getName().equals(node.getName()))) {
				throw new TreeOperationException(format("Sibling name %s already exists", node.getName()), SIBLING_NAME_EXISTS);
			} else {
				children.add(node);
			}
		}

		public void removeChild(@NonNull String id) throws TreeOperationException {
			if(!children.removeIf(child -> child.getNodeId().contains(id))) throw new TreeOperationException(format("Child with specified Id %s is not found", id), CHILD_IS_NOT_FOUND);
		}

		public void updateParent(TreeNode newParent) throws TreeOperationException {
			if (parent != null) {
				parent.removeChild(getNodeId());
			}

			newParent.addChild(this);
			parent = newParent;
		}

		public String getParentId() {
			if(parent == null) return null;

			return parent.getNodeId();
		}
	}
}
