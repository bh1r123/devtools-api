package com.wells.poc.models;

import java.util.List;

public class Hits {
	private List<Document> hits;
	private int total;

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setHits(List<Document> hits) {
		this.hits = hits;
	}

	public List<Document> getHits() {
		return hits;
	}

}
