package com.freelance4J.apigateway.model;

import java.util.TreeSet;

public class Freelancer {

	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private TreeSet<String> skills = new TreeSet();

	public Freelancer() {
	}

	public Freelancer(String firstName, String lastName, String email, TreeSet<String> skills) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.skills = skills;
	}

	public Freelancer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TreeSet<String> getSkills() {
		return skills;
	}

	public void setSkills(TreeSet<String> skills) {
		this.skills = skills;
	}
	

}
