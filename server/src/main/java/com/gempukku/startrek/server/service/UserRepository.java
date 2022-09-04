package com.gempukku.startrek.server.service;

import com.gempukku.startrek.server.service.vo.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}