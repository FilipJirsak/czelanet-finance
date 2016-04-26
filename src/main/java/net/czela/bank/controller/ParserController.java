package net.czela.bank.controller;

import jodd.io.StreamUtil;
import net.czela.bank.service.RbUploadService;
import net.czela.bank.service.VypisyService;
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
public class ParserController {

	private final VypisyService vypisyService;

	@Autowired
	public ParserController(VypisyService vypisyService) {
		this.vypisyService = vypisyService;
	}

	@RequestMapping("/parse")
	@ResponseStatus(code = HttpStatus.OK)
	public void parse(@RequestParam("id") int id) throws IOException, DocumentException {
		vypisyService.zpracovatVypisy(id);
	}
}
