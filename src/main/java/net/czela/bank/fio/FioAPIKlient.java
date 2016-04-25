package net.czela.bank.fio;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Created by jirsakf on 25.4.2016.
 */
@Component
public class FioAPIKlient implements Closeable {
	private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
	private final CloseableHttpClient httpClient;
	private final StringResponseHandler stringResponseHandler = new StringResponseHandler();
	private final String token;
	private final HttpHost host;

	@Autowired
	public FioAPIKlient(FioAPIConfiguration configuration) {
		this.httpClient = HttpClientBuilder.create().build();
		this.token = configuration.getToken();
		this.host = HttpHost.create(configuration.getHost());
	}

	public String nacistPohybyZaObdobi(LocalDate datumOd, LocalDate datumDo, FioAPIFormatTransakci format) throws IOException {
		HttpGet request = createGet("periods", format, datumOd, datumDo);
		return httpClient.execute(host, request, stringResponseHandler);
	}

	public String nacistPohybyZaObdobi(LocalDate datum, FioAPIFormatTransakci format) throws IOException {
		HttpGet request = createGet("periods", format, datum, datum);
		return httpClient.execute(host, request, stringResponseHandler);
	}

	public String nacistVypis(int rok, int cislo, FioAPIFormatVypisu format) throws IOException {
		HttpGet request = createGet("by-id", format, rok, cislo);
		return httpClient.execute(host, request, stringResponseHandler);
	}

	public String nacistPohybyOdMinule(FioAPIFormatTransakci format) throws IOException {
		HttpGet request = createGet("last", format);
		return httpClient.execute(host, request, stringResponseHandler);
	}

	public String nastavitZarazku(long id) throws IOException {
		HttpGet request = createGet("set-last-id", id);
		return httpClient.execute(host, request, stringResponseHandler);
	}

	public String nastavitZarazku(LocalDate datum) throws IOException {
		HttpGet request = createGet("set-last-date", datum);
		return httpClient.execute(host, request, stringResponseHandler);
	}

	protected HttpGet createGet(String method, FioAPIFormat format, Object... args) {
		return createGet(method, builder -> {
			builder.append("/transactions.");
			builder.append(format.getFormat());
		}, args);
	}

	protected HttpGet createGet(String method, Object... args) {
		return createGet(method, (Consumer<StringBuilder>) null, args);
	}

	protected HttpGet createGet(String method, Consumer<StringBuilder> modifier, Object... args) {
		StringBuilder builder = new StringBuilder();
		builder.append("/ib_api/rest/");
		builder.append(method);
		builder.append('/');
		builder.append(token);
		for (Object arg : args) {
			builder.append('/');
			if (arg instanceof LocalDate) {
				builder.append(((LocalDate) arg).format(DATE_FORMATTER));
			} else {
				builder.append(arg);
			}
		}
		if (modifier != null) {
			modifier.accept(builder);
		} else {
			builder.append('/');
		}
		return new HttpGet(builder.toString());
	}

	@PreDestroy
	@Override
	public void close() throws IOException {
		this.httpClient.close();
	}
}
