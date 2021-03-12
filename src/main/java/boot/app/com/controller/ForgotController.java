package boot.app.com.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.app.com.Repositaroy.UserRepositaroy;
import boot.app.com.Service.EmailService;
import boot.app.com.entities.User;

@Controller
public class ForgotController {
	Random random = new Random(1000);

	@Autowired
	private EmailService serviceEmail;

	@Autowired
	private UserRepositaroy userrepo;

	@Autowired
	private BCryptPasswordEncoder bcrypassword;

	@RequestMapping("/forgot")
	public String openEmail(Model model) {
		model.addAttribute("title", "Forgot-Smart Contact Manager");
		return "forgot_email_form";

	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String emailID, HttpSession session) {

		System.out.println(emailID);
		// generating 4digit otp

		int otp = random.nextInt(999999);
		System.out.println(otp);
		String subject = "OTP From SCM";
		String message = "" + "<div style='border 1px solid #008000; padding:20px'>" + "<h1>"
				+ "OTP Sending From SmartContactManager " + "<b>" + otp + "</n>" + "<h1>" + "</div>";
		String to = emailID;
		boolean flag = this.serviceEmail.sendEmail(subject, to, message);
		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", emailID);
			return "verify_otp";
		} else {
			session.setAttribute("message", "chek your mailID");
			return "forgot_email_form";
		}

	}

	// verifying otp
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession httpSession,Model model) {

		int myOtp = (int) httpSession.getAttribute("myotp");
		String email = (String) httpSession.getAttribute("email");
		if (myOtp == otp) {
			// change password form
			User user = this.userrepo.getUserByUserName(email);
			if (user == null) {
				// error message
				httpSession.setAttribute("message", "User doesn't exist with this EamilID");
				return "forgot_email_form";
			} else {
				// change password
				model.addAttribute("title", "Password Change-Smart Contact Manager");
				return "password_Change_form";
			}

		} else {
			httpSession.setAttribute("message", "you have entered Wrong OTP");
			return "verify_otp";
		}

	}

	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		String email = (String) session.getAttribute("email");
		User user = this.userrepo.getUserByUserName(email);
		user.setPassword(this.bcrypassword.encode(newpassword));
		this.userrepo.save(user);
		System.out.println("Password Saved INto DATA BASE" + user.getPassword());
		return "redirect:/singin?change=password changes Successfully..!!";

	}

}
