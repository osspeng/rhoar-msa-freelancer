package com.freelance4J.freelancer.service;

import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "freelancer")
public class Freelancer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	@Column
	@Convert(converter = SkillConverter.class)
	private TreeSet<String> skills = new TreeSet();
//	private String skills;

	public Freelancer() {
	}

	public Freelancer(String firstName, String lastName, String email, TreeSet<String> skills) {
//	public Freelancer(String firstName, String lastName, String email, String skills) {
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

//	public String getSkills() {
//		return skills;
//	}
//
//	public void setSkills(String skills) {
//		this.skills = skills;
//	}

	public TreeSet<String> getSkills() {
		return skills;
	}

	public void setSkills(TreeSet<String> skills) {
		this.skills = skills;
	}
	

}
