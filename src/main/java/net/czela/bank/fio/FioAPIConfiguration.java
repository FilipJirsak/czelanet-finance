package net.czela.bank.fio;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by jirsakf on 25.4.2016.
 */
@Component
@ConfigurationProperties(prefix="fio.api")
public class FioAPIConfiguration {
	private String host;
	private String token;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
