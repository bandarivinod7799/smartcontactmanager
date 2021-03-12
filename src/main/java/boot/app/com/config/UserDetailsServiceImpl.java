package boot.app.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import boot.app.com.Repositaroy.UserRepositaroy;
import boot.app.com.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepositaroy repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User userByUserName = repo.getUserByUserName(username);
		if (userByUserName == null) {
			throw new UsernameNotFoundException("Cound not found user...!!!");
		}
		 CustomUserDatails customUserDatails=new CustomUserDatails(userByUserName);
		
		return customUserDatails;
	}

}
