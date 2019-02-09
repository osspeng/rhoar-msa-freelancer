package com.freelancer4J.apigateway.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.freelance4J.apigateway.ApiGatewayApplication;

import io.restassured.RestAssured;

@RunWith(Arquillian.class)
public class RestApiTest {

    private static String port = "8181";

    private Client client;
    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, ApiGatewayApplication.class.getPackage())
                .addAsResource("project-local.yml", "project-defaults.yml");
    }

    @Before
    public void beforeTest() throws Exception {
    	client = ClientBuilder.newClient();
        RestAssured.baseURI = String.format("http://localhost:%s", port);
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Test
    @RunAsClient
    public void testGetFreelancers() throws Exception {
    	 WebTarget target = client.target("http://localhost:" + port).path("/gateway/freelancers");
         Response response = target.request(MediaType.APPLICATION_JSON).get();
         assertThat(response.getStatus(), equalTo(new Integer(200)));
         JsonArray value = Json.parse(response.readEntity(String.class)).asArray();
         assertThat(value.size(), equalTo(3));
    }

    @Test
    @RunAsClient
    public void testGetFreelancer() throws Exception {
        WebTarget target = client.target("http://localhost:" + port).path("/gateway/freelancers").path("/1");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), equalTo(new Integer(200)));
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.getInt("id", 0), equalTo(new Integer(1)));
        assertThat(value.getString("firstName", null), equalTo("Jason"));
        assertThat(value.getString("lastName", null), equalTo("Peng"));
    }
    

    @Test
    @RunAsClient
    public void testGetProjects() throws Exception {
    	 WebTarget target = client.target("http://localhost:" + port).path("/gateway/projects");
         Response response = target.request(MediaType.APPLICATION_JSON).get();
         assertThat(response.getStatus(), equalTo(new Integer(200)));
         JsonArray value = Json.parse(response.readEntity(String.class)).asArray();
         assertThat(value.size(), equalTo(5));
    }
    

    @Test
    @RunAsClient
    public void testGetProject() throws Exception {
    	 WebTarget target = client.target("http://localhost:" + port).path("/gateway/projects/111111");
         Response response = target.request(MediaType.APPLICATION_JSON).get();
         assertThat(response.getStatus(), equalTo(new Integer(200)));
         JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
         assertThat(value.getString("projectId", null), equalTo("111111"));
         assertThat(value.getString("firstName", null), equalTo("Jason"));
         assertThat(value.getString("lastName", null), equalTo("Peng"));
    }
    

    @Test
    @RunAsClient
    public void testGetProjectsByStatus() throws Exception {
    	 WebTarget target = client.target("http://localhost:" + port).path("/gateway/projects/status/OPEN");
         Response response = target.request(MediaType.APPLICATION_JSON).get();
         assertThat(response.getStatus(), equalTo(new Integer(200)));
         JsonArray value = Json.parse(response.readEntity(String.class)).asArray();
         assertThat(value.size(), equalTo(3));
    }

//    @Test
//    @RunAsClient
//    public void testGetFreelancer() throws Exception {
//        given()
//            .get("gateway/freelancers/{id}", 1)
//            .then()
//            .assertThat()
//            .statusCode(200)
//            .contentType(ContentType.JSON)
//            .body("id", equalTo(1))
//            .body("firstName", equalTo("Jason"))
//            .body("lastName", equalTo("Peng"));
//    }
}
