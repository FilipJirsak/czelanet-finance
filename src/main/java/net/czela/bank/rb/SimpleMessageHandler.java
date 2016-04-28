package net.czela.bank.rb;

import jodd.io.StreamUtil;
import net.czela.bank.service.RbUploadService;
import org.dom4j.DocumentException;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

public class SimpleMessageHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(SimpleMessageHandler.class);
	private final RbUploadService rbUploadService;

	public SimpleMessageHandler(MessageContext messageContext, RbUploadService rbUploadService) {
		this.rbUploadService = rbUploadService;
	}

	@Override
	public void from(String from) throws RejectException {
	}

	@Override
	public void recipient(String recipient) throws RejectException {
	}

	@Override
	public void data(InputStream data) throws RejectException, TooMuchDataException, IOException {
		try {
			MimeMessage message = new MimeMessage(null, data);
			logger.debug("Přijat e-mail: {}", message);
			rbUploadService.zapsatVypis((String) message.getContent());
		} catch (MessagingException | DocumentException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void done() {
	}
}