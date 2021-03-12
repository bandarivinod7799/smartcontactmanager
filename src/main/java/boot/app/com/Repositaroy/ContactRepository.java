package boot.app.com.Repositaroy;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import boot.app.com.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

	// get list of contacts based search functionality
	@Query("select c from Contact c where c.email=:name")
	public List<Contact> searchByName(@Param("name") String name  );
}
