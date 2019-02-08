package com.freelance4J.apigateway.model;

import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonObject;

public class Project implements Serializable {

	private static final long serialVersionUID = -6994655395272795259L;

	private String projectId;
	private String firstName;
	private String lastName;
	private String email;
	private String title;
	private String description;

	private Status status;

	public enum Status {
//		OPEN("0"), INPROGRESS("1"), COMPLETED("2"), CANCELLED("3");
		OPEN, INPROGRESS, COMPLETED, CANCELLED;
		private String value;

		Status() {
		}

		Status(String value) {
			this.value = value;
		}

//		@Override
//		public String toString() {
//			return this.value;
//		}
	}

	public Project() {

	}

	public Project(JsonObject json) {
		this.projectId = json
			.getString("projectId");
		this.firstName = json
			.getString("firstName");
		this.lastName = json
			.getString("lastName");
		this.email = json
			.getString("email");
		this.title = json
			.getString("title");
		this.description = json
			.getString("description");
		this.status = Status
			.valueOf(json
				.getString("status"));
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public JsonObject toJson() {
		final JsonObject json = Json.createObjectBuilder()
			.add("projectId", this.projectId)
			.add("firstName", this.firstName)
			.add("lastName", this.lastName)
			.add("email", this.email)
			.add("title", this.title)
			.add("description", this.description)
			.add("status", this.status.toString())
			.build();
		return json;
	}

}
