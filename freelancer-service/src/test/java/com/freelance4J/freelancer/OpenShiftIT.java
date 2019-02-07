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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;

import io.restassured.http.ContentType;
import java.net.URL;
import java.util.Collections;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class OpenShiftIT {

	private static final String FREELANCER_PATH = "api/freelancers";

    @AwaitRoute(path = "/health")
    @RouteURL("${app.name}")
    private URL url;

    @Test
    public void testPostGetAndDelete() {
        Integer id =
                given()
                   .baseUri(url.toString())
                   .contentType(ContentType.JSON)
                   .body(Collections.singletonMap("firstName", "Jason"))
                   .body(Collections.singletonMap("lastName", "Peng"))
                   .post(FREELANCER_PATH)
                   .then()
                   .statusCode(201)
                   .body("id", not(isEmptyString()))
                   .body("fisrtName", is("Jason"))
                   .body("lastName", is("Peng"))
                   .extract()
                   .response()
                   .path("id");

        given()
           .baseUri(url.toString())
           .get(String.format("%s/%d",FREELANCER_PATH, id))
           .then()
           .statusCode(200)
           .body("id", is(id))
           .body("firstName", is("Jason"));

        given()
           .baseUri(url.toString())
           .delete(String.format("%s/%d",FREELANCER_PATH, id))
           .then()
           .statusCode(204);
    }

}

