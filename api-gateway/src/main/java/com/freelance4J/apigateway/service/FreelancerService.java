package com.freelance4J.apigateway.service;

import java.util.List;
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

import com.freelance4J.apigateway.model.Freelancer;

@ApplicationScoped
public class FreelancerService {
	private Logger log = Logger.getLogger(FreelancerService.class.getName());

	private WebTarget freelancerService;

	@Inject
	@ConfigurationValue("freelancer.service.url")
	private String freelancerUrl;

	@PostConstruct
	public void init() {
		ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) ClientBuilder
			.newBuilder();
		freelancerService = clientBuilder
			.connectionPoolSize(10)
			.build()
			.target(freelancerUrl)
			.path("api/freelancers");
	}

	public Freelancer[] getFreelancers() {
		log.info("FreelancerService.getFreelancers");
		Freelancer[] freelancer = null;
		Response response = freelancerService
			.request(MediaType.APPLICATION_JSON)
			.get();
		log.info("URI: "+freelancerService.getUri().toString());
		log.info("Status Code: "+response.getStatus());
		log.info("Status Info: "+response.getStatusInfo());
		log.info("Response: "+response.toString());
		try {
			if (response.getStatus() == 200) {
				response.bufferEntity();
				log.info("RESPONSE CONTENT: "+ response.toString());
				freelancer = response.readEntity(Freelancer[].class);
				List<Freelancer> rawJson = response.readEntity(List.class);
				log.info("LIST: "+ rawJson.toString());
			}
		} finally {
			response.close();
		}
		return freelancer;

	}
	public Freelancer getFreelancer(Integer freelancerId) {
		log.info("FreelancerService.getFreelancer");
		Freelancer freelancer = null;
		Response response = freelancerService
			.path("{freelancerId}")
			.resolveTemplate("freelancerId", freelancerId)
			.request(MediaType.APPLICATION_JSON)
			.get();
		log.info("URI: "+freelancerService.getUri().toString());
		log.info("Status Code: "+response.getStatus());
		log.info("Status Info: "+response.getStatusInfo());
		log.info("Response: "+response.toString());
		try {
			if (response.getStatus() == 200) {
				response.bufferEntity();
				log.info("RESPONSE CONTENT: "+ response.toString());
				freelancer = response.readEntity(Freelancer.class);
			}
		} finally {
			response.close();
		}
		return freelancer;

	}
}
