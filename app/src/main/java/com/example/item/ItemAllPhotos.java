package com.example.item;

public class ItemAllPhotos {
	

	private String CategoryName;
	private int CategoryId; 
	private String CategoryImage;
	
	
	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryImage(String categoryimage) {
		this.CategoryImage = categoryimage;
	}
	
	public String getCategoryImage() {
		return CategoryImage;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}
	
	public int getCategoryId() {
		return CategoryId;
	}

	public void setCategoryId(int categoryid) {
		this.CategoryId = categoryid;
	}

}
