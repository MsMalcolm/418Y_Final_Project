package com.example.demo;
// package com.example.accessingdatamysql;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {

	User findByEmail(String email);
	List<User> findByPassword(String password);
}