package net.czela.bank.controller;

import net.czela.bank.service.FioService;
import net.czela.bank.service.VypisyService;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by jirsakf on 26.4.2016.
 */
@Controller(value = "/fio")
public class FioController {

	private final FioService fioService;

	@Autowired
	public FioController(FioService fioService) {
		this.fioService = fioService;
	}

	@RequestMapping("/vypis")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<String> vypis(@RequestParam("od") String datumOd, @RequestParam("do") String datumDo) throws IOException, DocumentException {
		String response = fioService.nacistPohyby(LocalDate.parse(datumOd), LocalDate.parse(datumDo));
		return ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_XML)
				.body(response);
	}
}
