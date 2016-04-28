package net.czela.bank.smtp;

import net.czela.bank.config.SmtpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class SmtpServer {
	private final SMTPServer smtpServer;

	@Autowired
	public SmtpServer(MessageHandlerFactory messageHandlerFactory, SmtpConfig smtpConfig) throws UnknownHostException {
		smtpServer = new SMTPServer(messageHandlerFactory);
		smtpServer.setBindAddress(InetAddress.getByName(smtpConfig.getHost()));
		smtpServer.setPort(smtpConfig.getPort());
		smtpServer.setHostName(smtpConfig.getHostName());
	}

	@PostConstruct
	public void start() {
		smtpServer.start();
	}

	@PreDestroy
	public void stop() {
		smtpServer.stop();
	}
}