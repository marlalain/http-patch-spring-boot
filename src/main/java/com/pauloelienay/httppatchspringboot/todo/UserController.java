package com.pauloelienay.httppatchspringboot.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
	private final UserBusiness business;

	@PatchMapping("{id}")
	public void updateNonNullFields(@PathVariable Long id, @RequestBody User user) {
		business.updateNonFields(id, user);
	}

	@PostMapping
	public User create(@RequestBody User user) {
		return business.create(user);
	}

	@GetMapping("{id}")
	public User findById(@PathVariable Long id) {
		return business.findById(id);
	}
}
