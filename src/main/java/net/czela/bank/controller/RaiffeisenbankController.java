package net.czela.bank.controller;

import jodd.io.StreamUtil;
import net.czela.bank.service.RbUploadService;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jirsakf on 26.4.2016.
 */
@Controller
public class RaiffeisenbankController {

	private final RbUploadService rbUploadService;

	@Autowired
	public RaiffeisenbankController(RbUploadService rbUploadService) {
		this.rbUploadService = rbUploadService;
	}

	@RequestMapping("/import/rb")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void vypisUpload(@RequestParam("file") Part file) throws IOException, DocumentException {
		try (InputStream inputStream = file.getInputStream()) {
			rbUploadService.zapsatVypis(new String(StreamUtil.readChars(inputStream, "ISO-8859-2")));
		}
	}
}
