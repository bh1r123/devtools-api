package com.wells.poc.models;

import java.util.List;

public class PaginationResponse<T> {

	private int total;
	
	private List<T> data;
	
	private String message;
	
	
	public PaginationResponse(int total, String message, List<T> data) {
		this.data=data;
		this.message=message;
		this.total=total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}
