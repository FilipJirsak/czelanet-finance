package net.czela.bank.rb;

import jodd.io.FileUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.testng.Assert.*;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class RbTextVypisParserTest {

	private RbTextVypisParser parser;

	@BeforeClass
	public void init() throws IOException {
		parser = new RbTextVypisParser(RbTextVypisParserTest.class.getResourceAsStream("rb-1.txt"));
		parser.read();
	}

	@AfterClass
	public void close() throws IOException {
		parser.close();
	}

	@Test
	public void testNazevBanky() throws IOException {
		assertEquals(parser.getBankovniUcet().getNazevBanky(), "Raiffeisenbank a.s. ");

	}

	@Test
	public void testNazevUctu() throws IOException {
		assertEquals(parser.getBankovniUcet().getNazev(), "czela.net");
	}

	@Test
	public void testPredcisliUctu() throws IOException {
		assertNull(parser.getBankovniUcet().getPredcisli());

	}

	@Test
	public void testCisloUctu() throws IOException {
		assertEquals(parser.getBankovniUcet().getCislo(), "1222733001");

	}

	@Test
	public void testKodBanky() throws IOException {
		assertEquals(parser.getBankovniUcet().getKodBanky(), "5500");
	}

	@Test
	public void testCisloVypisu() throws IOException {
		assertEquals(parser.getCisloVypisu(), 67);
	}

	@Test
	public void testDatumVypisu() throws IOException {
		assertEquals(parser.getDatumVypisu(), LocalDate.of(2016, 4, 13));
	}

	@Test
	public void testPocatecniZustatek() throws IOException {
		assertEquals(parser.getPocatecniZustatek(), new BigDecimal("23280.01"));
	}

	@Test
	public void testKonecnyZustatek() throws IOException {
		assertEquals(parser.getKonecnyZustatek(), new BigDecimal("23980.01"));
	}

	@Test
	public void testPocetTransakci() throws IOException {
		assertEquals(parser.getTransakce().size(), 2);
	}
}