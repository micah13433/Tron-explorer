package com.tron.explorer.model;

import java.util.Map;


public class Node4Chart  {

	private String country;
	private Map<String,Integer> city;
	private int count;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Map<String, Integer> getCity() {
		return city;
	}
	public void setCity(Map<String, Integer> city) {
		this.city = city;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
