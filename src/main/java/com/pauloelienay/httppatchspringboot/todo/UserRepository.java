package com.pauloelienay.httppatchspringboot.todo;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

@Repository
public class UserRepository extends SimpleJpaRepository<User, Long> {
	// Private fields coming from the constructor
	private final Class<User> clazz;
	private final EntityManager em;

	public UserRepository(EntityManager em) {
		// Provide our User class to SimpleJpaRepository and
		// receive our entity manager
		super(User.class, em);
		this.clazz = User.class;
		this.em = em;
	}

	@Transactional
	public void updateNonNullFields(User user) {
		// Instantiating our required objects
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaUpdate<User> criteria = builder.createCriteriaUpdate(clazz);
		Root<User> root = criteria.from(clazz);
		List<Field> nonNullFields = new ArrayList<>();

		// Cycle through our user looking for every non-null value
		Arrays.stream(clazz.getDeclaredFields())
			.forEach(field -> {
				makeAccessible(field);
				if (nonNull(getField(field, user)) && !field.getName().equals("id")) {
					nonNullFields.add(field);
				}
			});

		// Create a SQL query with the non-null values
		nonNullFields.parallelStream()
			.forEach(field -> {
				if (field.getType().isAnnotationPresent(Entity.class) && nonNull(getField(field, user))) {
					criteria.set(root.<Long>get(field.getName()), builder.nullLiteral(Long.class));
				} else {
					criteria.set(field.getName(), getField(field, user));
				}
			});

		// Set the SQL WHERE, so we only update one row
		criteria.where(builder.equal(root.<User>get("id"), user.getId()));
		// Execute the SQL query
		em.createQuery(criteria).executeUpdate();
	}
}
