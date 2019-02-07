/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freelance4J.freelancer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.freelance4J.freelancer.service.Freelancer;
import com.freelance4J.freelancer.service.FreelancerRepository;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FreelancerApplicationTest {

	private static final String FREELANCER_PATH = "api/freelancers";

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private FreelancerRepository freelancerRepository;

	@Before
	public void beforeTest() {
		freelancerRepository.deleteAll();
		RestAssured.baseURI = String.format("http://localhost:%d/" + FREELANCER_PATH, port);
	}

	@Test
	public void testGetAll() {
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng"));
		Freelancer eddie = freelancerRepository.save(new Freelancer("Eddie", "Liu"));
		requestSpecification()
			.get()
			.then()
			.statusCode(200)
			.body("id", hasItems(jason.getId(), eddie.getId()))
			.body("firstName", hasItems(jason.getFirstName(), eddie.getFirstName()))
			.body("lastName", hasItems(jason.getLastName(), eddie.getLastName()));
	}

	@Test
	public void testGetEmptyArray() {
		requestSpecification().get().then().statusCode(200).body(is("[]"));
	}

	@Test
	public void testGetOne() {
		TreeSet<String> skills = new TreeSet<String>();
		skills.add("Java");
		skills.add("Ruby");
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng", "hpeng@redhat.com", skills));
		requestSpecification()
			.get(String.valueOf(jason.getId()))
			.then()
			.statusCode(200)
			.body("id", is(jason.getId()))
			.body("firstName", is(jason.getFirstName()))
			.body("lastName", is(jason.getLastName()))
			.body("skills", hasItems("Java","Ruby"));
	}

	@Test
	public void testGetNotExisting() {
		requestSpecification()
			.get("0")
			.then()
			.statusCode(404);
	}

	@Test
	public void testPost() {
		Map<String, Object>  jsonAsMap = new HashMap<>();
		jsonAsMap.put("firstName", "Jason");
		jsonAsMap.put("lastName", "Peng");
		requestSpecification()
			.contentType(ContentType.JSON)
			.body(jsonAsMap)
			.post()
			.then()
			.statusCode(201)
			.body("id", not(isEmptyString()))
			.body("firstName", is("Jason"))
			.body("lastName", is("Peng"));
	}

	@Test
	public void testPostWithWrongPayload() {
		requestSpecification()
			.contentType(ContentType.JSON)
			.body(Collections
				.singletonMap("id", 0))
			.when()
			.post()
			.then()
			.statusCode(422);
	}

	@Test
	public void testPostWithNonJsonPayload() {
		requestSpecification()
			.contentType(ContentType.XML)
			.when()
			.post()
			.then()
			.statusCode(415);
	}

	@Test
	public void testPostWithEmptyPayload() {
		requestSpecification()
			.contentType(ContentType.JSON)
			.when()
			.post()
			.then()
			.statusCode(415);
	}

	@Test
	public void testPut() {
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng"));
		Map<String, Object>  jsonAsMap = new HashMap<>();
		jsonAsMap.put("firstName", "Lemon");
		jsonAsMap.put("lastName", "Lee");
		requestSpecification()
			.contentType(ContentType.JSON)
			.body(jsonAsMap)
			.when()
			.put(String.valueOf(jason.getId()))
			.then()
			.statusCode(200)
			.body("id", is(jason.getId()))
			.body("firstName", is("Lemon"))
			.body("lastName", is("Lee"));

	}

	@Test
	public void testPutNotExisting() {
		requestSpecification()
			.contentType(ContentType.JSON)
			.body(Collections.singletonMap("name", "Lemon"))
			.when()
			.put("/0")
			.then()
			.statusCode(404);
	}

	@Test
	public void testPutWithWrongPayload() {
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng"));
		requestSpecification()
			.contentType(ContentType.JSON)
			.body(Collections
				.singletonMap("id", 0))
			.when()
			.put(String.valueOf(jason.getId()))
			.then()
			.statusCode(422);
	}

	@Test
	public void testPutWithNonJsonPayload() {
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng"));
		requestSpecification()
			.contentType(ContentType.XML)
			.when()
			.put(String.valueOf(jason.getId()))
			.then()
			.statusCode(415);
	}

	@Test
	public void testPutWithEmptyPayload() {
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng"));
		requestSpecification()
			.contentType(ContentType.JSON)
			.when()
			.put(String.valueOf(jason.getId()))
			.then()
			.statusCode(415);
	}

	@Test
	public void testDelete() {
		Freelancer jason = freelancerRepository.save(new Freelancer("Jason", "Peng"));
		requestSpecification()
			.delete(String.valueOf(jason.getId()))
			.then()
			.statusCode(204);
		assertFalse(freelancerRepository
			.exists(jason.getId()));
	}

	@Test
	public void testDeleteNotExisting() {
		requestSpecification().delete("/0").then().statusCode(404);
	}

	private RequestSpecification requestSpecification() {
		return given()
			.filter(new RequestLoggingFilter())
			.filter(new ResponseLoggingFilter())
			.baseUri(String.format("http://localhost:%d/%s", port, FREELANCER_PATH));
	}
}
