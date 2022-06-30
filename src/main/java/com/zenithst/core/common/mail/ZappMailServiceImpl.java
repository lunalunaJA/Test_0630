package com.zenithst.core.common.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.common.extend.ZappService;


@Service("zappMailService")
public class ZappMailServiceImpl extends ZappService implements ZappMailService {

//	@Autowired
//    JavaMailSender mailSender;
//	
//	public void sendEmail(String content) {
//
//		final MimeMessagePreparator preparator = new MimeMessagePreparator() {
//            @Override
//            public void prepare(MimeMessage mimeMessage) throws Exception {
//                final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//                
//                helper.setFrom(mail.getMailFrom()); // recipient
//                helper.setTo(mail.getMailTo()); //sender
//                helper.setSubject(mail.getMailSubject()); // mail title
//                helper.setText(mail.getMailContent(), true); // mail content
//            }
//        };
// 
//        mailSender.send(preparator);
//		
//	}

}
