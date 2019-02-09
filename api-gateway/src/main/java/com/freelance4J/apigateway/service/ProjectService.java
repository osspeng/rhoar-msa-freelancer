package com.freelance4J.apigateway.service;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import com.freelance4J.apigateway.model.Project;

@ApplicationScoped
public class ProjectService {
	private Logger log = Logger.getLogger(ProjectService.class.getName());

	private WebTarget projectService;

	@Inject
	@ConfigurationValue("freelancer4J.project.service.url")
	private String projectUrl;

	@PostConstruct
	public void init() {
		ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) ClientBuilder
			.newBuilder();
		projectService = clientBuilder
			.connectionPoolSize(10)
			.build()
			.target(projectUrl)
			.path("projects");
	}

	public Project[] getProjects() {
		log.info("ProjectService.getProjects");
		Project[] projects = null;
		Response response = projectService
			.request(MediaType.APPLICATION_JSON)
			.get();
		log.info("URI: "+projectService.getUri().toString());
		log.info("Status Code: "+response.getStatus());
		log.info("Status Info: "+response.getStatusInfo());
		log.info("Response: "+response.toString());
		try {
			if (response.getStatus() == 200) {
				response.bufferEntity();
				log.info("RESPONSE CONTENT: "+ response.toString());
				projects = response.readEntity(Project[].class);
			}
		} finally {
			response.close();
		}
		return projects;

	}
	public Project getProject(String projectId) {
		log.info("ProjectService.getProject");
		Project project = null;
		Response response = projectService
			.path("{projectId}")
			.resolveTemplate("projectId", projectId)
			.request(MediaType.APPLICATION_JSON)
			.get();
		log.info("URI: "+projectService.getUri().toString());
		log.info("Status Code: "+response.getStatus());
		log.info("Status Info: "+response.getStatusInfo());
		log.info("Response: "+response.toString());
		try {
			if (response.getStatus() == 200) {
				response.bufferEntity();
				log.info("RESPONSE CONTENT: "+ response.toString());
				project = response.readEntity(Project.class);
			}
		} finally {
			response.close();
		}
		return project;

	}
	public Project[] getProjectsByStatus(String theStatus) {
		log.info("ProjectService.getProjectsByStatus");
		Project[] projects = null;
		Response response = projectService
				.path("status/{theStatus}")
				.resolveTemplate("theStatus", theStatus)
				.request(MediaType.APPLICATION_JSON)
				.get();
		log.info("URI: "+projectService.getUri().toString());
		log.info("Status Code: "+response.getStatus());
		log.info("Status Info: "+response.getStatusInfo());
		log.info("Response: "+response.toString());
		try {
			if (response.getStatus() == 200) {
				response.bufferEntity();
				log.info("RESPONSE CONTENT: "+ response.toString());
				projects = response.readEntity(Project[].class);
			}
		} finally {
			response.close();
		}
		return projects;

	}
}
