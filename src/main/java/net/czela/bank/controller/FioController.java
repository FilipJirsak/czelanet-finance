package net.czela.bank.controller;

import net.czela.bank.service.FioService;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Controller
@RequestMapping("/fio")
public class FioController {
  private final Logger logger = LoggerFactory.getLogger(FioController.class);

  private final FioService fioService;

  @Autowired
  public FioController(FioService fioService) {
    this.fioService = fioService;
  }

  @RequestMapping("/vypis")
  public ResponseEntity<String> vypis(@RequestParam("od") String datumOd, @RequestParam("do") String datumDo) throws IOException, DocumentException {
    String response = fioService.nacistPohyby(LocalDate.parse(datumOd), LocalDate.parse(datumDo));
    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  @RequestMapping("/nove")
  public ResponseEntity<String> nove() throws IOException, DocumentException {
    String response = fioService.nacistNovePlatby();
    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  @RequestMapping("/nastavit-zarazku")
  @ResponseStatus(code = HttpStatus.ACCEPTED)
  public void nastavitZarazku(@RequestParam("zarazka") long zarazka) throws IOException, DocumentException {
    fioService.nastavitZarazku(zarazka);
  }
}
