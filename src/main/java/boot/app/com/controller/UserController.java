package boot.app.com.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import boot.app.com.Repositaroy.ContactRepository;
import boot.app.com.Repositaroy.UserRepositaroy;
import boot.app.com.entities.Contact;
import boot.app.com.entities.User;
import boot.app.com.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bcryptPwd;

	@Autowired
	private UserRepositaroy userrepo;

	@Autowired
	private ContactRepository contactRepo;

	Log log = LogFactory.getLog(UserController.class);

	// addCommonData
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		log.debug("==== Exution Start addCommonData===");
		String userName = principal.getName();
		// Principal Interface used to get Unique or Id Variable
		System.out.println(userName);
		log.info("User Name:" + userName);
		// by passing userName we will get the user details from DB with help
		// UserRepositaroy method getUserByUserName()

		User user = userrepo.getUserByUserName(userName);
		log.debug("==== Exution End addCommonData===");
		model.addAttribute("user", user);
	}

	// Dash board
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userrepo.getUserByUserName(userName);
		model.addAttribute("title", "User-Home");
		model.addAttribute("user", user);
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userrepo.getUserByUserName(name);

			// processing and uploading Image
			if (file.isEmpty()) {
				System.out.println("image not uploaded");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File file2 = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploaded");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userrepo.save(user);
			System.out.println("DATA " + contact);
			System.out.println("ADDED to DB");
// message success
			session.setAttribute("message", new Message("Your Contact Added..!! Add more", "success"));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			// Error Message
			session.setAttribute("message", new Message("Something went wrong,try Again", "danger"));

		}
		return "normal/add_contact_form";
	}

	// pagination
	@GetMapping("/show-contacts/{page}")
	public String showAllContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show User Contacts");
		String userName = principal.getName();
		User user = this.userrepo.getUserByUserName(userName);
		//
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", contacts.getTotalPages());
		return "normal/show_contacts";
	}

	// showing particular contact details
	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cId, Model model, Principal principal) {
		System.out.println(cId);
		Optional<Contact> contOptional = this.contactRepo.findById(cId);
		Contact contact = contOptional.get();

		String userName = principal.getName();
		User user = this.userrepo.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId())
			;
		{
			model.addAttribute("contact", contact);
		}
		return "normal/contactDetails";
	}

	// delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session) {
		Optional<Contact> coOptional = this.contactRepo.findById(cId);
		Contact contact = coOptional.get();

		contact.setUser(null);
		this.contactRepo.delete(contact);
		session.setAttribute("message", new Message("Contact Deletedsuccessfully....!!", "success"));
		System.out.println("Contact Deleted successfully....");
		return "redirect:/user/show-contacts/0";
	}

	// open update form

	@PostMapping("/update_contact/{cid}")
	public String updateForm(Model model, @PathVariable("cid") Integer cId) {
		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepo.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession httpSession, Principal principal) {
		try {
			// old contact details
			Contact oldContactDetails = this.contactRepo.findById(contact.getCid()).get();

			if (!file.isEmpty()) {
				// file working.......

				// delete Old file
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file3 = new File(deleteFile, oldContactDetails.getImage());
				file3.delete();

				// new Image uploading...
				contact.setImage(file.getOriginalFilename());
				File file2 = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("new Image uploaded");
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetails.getImage());
			}
			User user = this.userrepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);
			httpSession.setAttribute("message", new Message("Contact Details Updated", "success"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("Contact====" + contact.getName());
		System.out.println("Contact====" + contact.getCid());
		return "redirect:/user/" + contact.getCid() + "/contact";

	}

	// open setting handler
	@GetMapping("/settings")
	public String settings() {

		return "normal/settings";

	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldpassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		System.out.println("OLD PASSWORD =" + oldpassword);
		System.out.println("NEW PASSWORD =" + newPassword);
		String userName = principal.getName();
		User currentUser = this.userrepo.getUserByUserName(userName);
		System.out.println(currentUser);

		if (this.bcryptPwd.matches(oldpassword, currentUser.getPassword())) {
			// change password
			currentUser.setPassword(this.bcryptPwd.encode(newPassword));
			this.userrepo.save(currentUser);
			session.setAttribute("message", new Message("Password successfully changed..", "success"));
		} else {

			session.setAttribute("message", new Message("Please enter correct old password", "danger"));
		}
		return "redirect:/user/index";
	}

}