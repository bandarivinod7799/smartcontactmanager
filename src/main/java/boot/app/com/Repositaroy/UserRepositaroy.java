package boot.app.com.Repositaroy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import boot.app.com.entities.User;

@Repository
public interface UserRepositaroy  extends JpaRepository<User, Integer> {
// Injecting to Dependency
	@Query("select u from User u where u.email= :email")
	public User getUserByUserName(@Param("email") String email ) ;
		
	
	
}
