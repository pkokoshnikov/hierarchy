package com.hierarchy.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SimpleDataResponse {
	public static SimpleDataResponse SUCCESS = new SimpleDataResponse(true);
	public static SimpleDataResponse FAILURE = new SimpleDataResponse(false);

	@JsonProperty(value = "ok")
	private boolean value;

	private SimpleDataResponse(boolean value) {
		this.value = value;
	}
}
