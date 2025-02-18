package edu.asu.diging.citesphere.importer.core.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.citesphere.user.impl.User;

//@JaversSpringDataAuditable
//@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    public List<User> findByEmail(String email);
}
