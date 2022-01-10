package com.pauloelienay.httppatchspringboot.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserBusiness {
	private final UserRepository repository;

	public void updateNonFields(Long id, User user) {
		if (isNull(id) || isNull(user) || !repository.existsById(id)) return;

		user.setId(id);
		repository.updateNonNullFields(user);
	}

	public User create(User user) {
		return repository.save(user);
	}

	public User findById(Long id) {
		return repository.findById(id)
			.orElse(null);
	}
}
