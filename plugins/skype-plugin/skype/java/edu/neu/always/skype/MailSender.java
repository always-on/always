package edu.neu.always.skype;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class MailSender {
	final static String username = "always.on.system@gmail.com";
	final static String password = "stmpgmail";

	public static boolean sendEmail(String body, String subject, String to) {
		Properties props = new Properties();		
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);
			
			Transport.send(message);
			
			System.out.println("done");
			return true;
		} catch(MessagingException e){
			System.out.println("failed to send message:" + e);
			return false;
		}
	}
	
	
	public static void main(String args[]){
		sendEmail("hello world","test subject","lazloring@gmail.com");
	}
	
}