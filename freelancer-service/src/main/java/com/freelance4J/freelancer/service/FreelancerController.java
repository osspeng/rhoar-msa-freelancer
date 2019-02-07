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

package com.freelance4J.freelancer.service;

import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.freelance4J.freelancer.exception.NotFoundException;
import com.freelance4J.freelancer.exception.UnprocessableEntityException;
import com.freelance4J.freelancer.exception.UnsupportedMediaTypeException;

@RestController
@RequestMapping(value = "/api/freelancers")
public class FreelancerController {

	private final FreelancerRepository repository;

	public FreelancerController(FreelancerRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/{id}")
	public Freelancer get(@PathVariable("id") Integer id) {
		verifyFreelancerExists(id);

		return repository.findOne(id);
	}

	@GetMapping
	public List<Freelancer> getAll() {
		Spliterator<Freelancer> freelancers = repository.findAll().spliterator();

		return StreamSupport.stream(freelancers, false).collect(Collectors.toList());
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public Freelancer post(@RequestBody(required = false) Freelancer freelancer) {
		verifyCorrectPayload(freelancer);

		return repository.save(freelancer);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/{id}")
	public Freelancer put(@PathVariable("id") Integer id, @RequestBody(required = false) Freelancer freelancer) {
		verifyFreelancerExists(id);
		verifyCorrectPayload(freelancer);

		freelancer.setId(id);
		return repository.save(freelancer);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		verifyFreelancerExists(id);

		repository.delete(id);
	}

	private void verifyFreelancerExists(Integer id) {
		if (!repository.exists(id)) {
			throw new NotFoundException(String.format("Freelancer with id=%d was not found", id));
		}
	}

	private void verifyCorrectPayload(Freelancer freelancer) {
		if (Objects.isNull(freelancer)) {
			throw new UnsupportedMediaTypeException("Invalid payload!");
		}

		if ((Objects.isNull(freelancer.getFirstName()) || freelancer.getFirstName().trim().length() == 0)
				&& (Objects.isNull(freelancer.getLastName()) || freelancer.getLastName().trim().length() == 0)) {
			throw new UnprocessableEntityException("The name is required!");
		}

		if (!Objects.isNull(freelancer.getId())) {
			throw new UnprocessableEntityException("Id was invalidly set on request.");
		}
	}

}
