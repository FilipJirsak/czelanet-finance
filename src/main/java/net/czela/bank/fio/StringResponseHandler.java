package net.czela.bank.fio;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by jirsakf on 25.4.2016.
 */
public class StringResponseHandler implements ResponseHandler<String> {
	@Override
	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() != 200) {
			EntityUtils.consume(entity);
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		return entity == null ? null : handleEntity(entity);
	}

	public String handleEntity(final HttpEntity entity) throws IOException {
		return EntityUtils.toString(entity);
	}

}
