package ru.gctc.inventory.server.db.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User getByUsername(String userName);
    User getByUsernameAndPassword(String userName, String password);
}
