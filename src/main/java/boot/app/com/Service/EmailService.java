package boot.app.com.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject ,String to ,String message ) {
		
		boolean f= false;
		String from ="vinnubandari85@gmail.com";
		
		String host="smtp.gmail.com";
		Properties properties=System.getProperties();
		
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		Session session=Session.getInstance(properties, new Authenticator() {
			
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("vinnubandari85@gmail.com", "bandarivinnu!@#123");};
		} );
		session.setDebug(true);
		MimeMessage mimeMessage= new MimeMessage(session);
		try {
			mimeMessage.setFrom(from);
			
			
		//	mimeMessage.setText(message);
			
			mimeMessage.setContent(message, "text/html");
			
			
			mimeMessage.setSubject(subject);
			//adding recipient to message
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			Transport.send(mimeMessage);
			f=true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return f;
		
	}
}
