package com.hierarchy.exception;

public class TreeOperationException extends Exception {
	public TreeOperationException(String message, Code code) {
		super(message);
	}

	public enum Code {
		CHILD_IS_NOT_FOUND, CHILDREN_ARE_NOT_EMPTY, NODE_ALREADY_EXISTS, NODE_IS_NOT_FOUND, SIBLING_NAME_EXISTS,
		CIRCLE_DETECTED, EMPTY_VALUE
	}
}
