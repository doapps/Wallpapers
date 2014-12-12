package com.example.favorite;

public class Pojo {
	
	private int id;
	
	private String CategoryName;
	 
	private String ImageUrl; 
	
	
	
	public Pojo()
	{
		
	}
	
	public Pojo(String imageid)
	{
		this.ImageUrl=imageid;
	}
	
	public Pojo(String cat_name,String img_url)
	{
		 
		this.CategoryName=cat_name;
		this.ImageUrl=img_url;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	 
	
	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}
	
	 
	public String getImageurl()
	{
		return ImageUrl;
		
	}
	
	public void setImageurl(String imageurl)
	{
		this.ImageUrl=imageurl;
	}

}
