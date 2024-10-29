package com.liangzai.exampleinterface.model;


import java.io.Serializable;

/**
 * @Author dengpei
 * @Date 2024/10/29 16:34
 * @Descprition
 */
public class User implements Serializable {

	private Long id;
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}




}
