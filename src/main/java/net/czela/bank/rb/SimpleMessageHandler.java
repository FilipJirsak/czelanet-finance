package net.czela.bank.rb;

import net.czela.bank.service.RbUploadService;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;

public class SimpleMessageHandler implements MessageHandler {
	private final Logger logger = LoggerFactory.getLogger(SimpleMessageHandler.class);
	private final RbUploadService rbUploadService;

	public SimpleMessageHandler(MessageContext messageContext, RbUploadService rbUploadService) {
		this.rbUploadService = rbUploadService;
	}

	@Override
	public void from(String from) throws RejectException {
		if (!from.contains("info@rb.cz")) {
			logger.error("E-mail od {} byl odmítnut.", from);
			throw new RejectException();
		}
	}

	@Override
	public void recipient(String recipient) throws RejectException {
	}

	@Override
	public void data(InputStream data) throws RejectException, IOException {
		try {
			String vypis = parseMessage(data);
			rbUploadService.zapsatVypis(vypis);
		} catch (MessagingException | DocumentException e) {
			throw new IOException(e);
		}
	}

	protected String parseMessage(InputStream data) throws MessagingException, IOException {
		MimeMessage message = new MimeMessage(null, data);
		logger.debug("Přijat e-mail: {}", message);
		MimeMultipart multipart = (MimeMultipart) message.getContent();
		return (String) multipart.getBodyPart(1).getContent();
	}

	@Override
	public void done() {
	}
}