package net.czela.bank.rb;

import net.czela.bank.service.RbUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;

@Component
public class SimpleMessageHandlerFactory implements MessageHandlerFactory {

	private final RbUploadService rbUploadService;

	@Autowired
	public SimpleMessageHandlerFactory(RbUploadService rbUploadService) {
		this.rbUploadService = rbUploadService;
	}

	@Override
	public MessageHandler create(MessageContext ctx) {
		return new SimpleMessageHandler(ctx, rbUploadService);
	}
}