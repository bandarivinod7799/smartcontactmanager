package boot.app.com.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import boot.app.com.Repositaroy.UserRepositaroy;
import boot.app.com.entities.User;
import boot.app.com.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder pswdEncoder; // Dependency Injection(Reference type Dependecy)

	@Autowired
	private UserRepositaroy userRepo;

	@RequestMapping("/home")
	public String home(Model model) {

		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/singin")
	public String singin(Model model) {

		model.addAttribute("title", "Login-Smart Contact Manager");
		return "singin";
	}

	@RequestMapping("/singup")
	public String singup(Model model) {

		model.addAttribute("title", "SignUp-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "singup";
	}

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registration(@Valid @ModelAttribute("user") User user, BindingResult resultb,
			@RequestParam(value = "aggrement", defaultValue = "fasle") boolean agreement, Model model,
			HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("You have not agreed the T&C");
				throw new Exception("You have not agreed the T&C");
			}

			if (resultb.hasErrors()) {
				System.out.println("error" + resultb.toString());
				model.addAttribute("user", user);
				return "singup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImgUrl("default.png");
			user.setPassword(pswdEncoder.encode(user.getPassword()));

			User result = this.userRepo.save(user);
			System.out.println(result);
			System.out.println("Agreemetn " + agreement);
			System.out.println("user " + user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Succesfully Registered", "alert-success"));
			return "singup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("something went wrong..!!" + e.getMessage(), "alert-danger"));
			return "singup";
		}

	}

	@GetMapping("/singin")
	public String customLogin(Model model) {

		model.addAttribute("title", "Login-Smart Contact Manager");
		return "singin";
	}
}
