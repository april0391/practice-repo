package toy.board.repository;

import toy.board.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    User save(User post);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);
}
