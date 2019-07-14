package by.touchsoft.chernetski.restchat.dao.repository;

import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByStatus(int status, Pageable page);
    Page<User> findByRolesAndStatus(Role role, int status, Pageable page);
    Page<User> findByRoles(Role role, Pageable page);
    Page<User> findAll(Pageable page);
    User findByName(String name);
    Optional<User> findById(Long id);
}
