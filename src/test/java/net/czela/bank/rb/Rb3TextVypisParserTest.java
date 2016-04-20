package net.czela.bank.rb;

import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.BankovniUcet;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class Rb3TextVypisParserTest extends AbstractRbTextVypisParserTest {
	public Rb3TextVypisParserTest() {
		super("rb-3.txt");
	}

	@Test
	public void testNazevBanky() throws IOException {
		assertEquals(parser.getBankovniUcet().getNazevBanky(), "Raiffeisenbank a.s.");

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
		assertEquals(parser.getCisloVypisu(), 61);
	}

	@Test
	public void testObdobiVypisuOd() throws IOException {
		assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 5));
	}

	@Test
	public void testObdobiVypisuDo() throws IOException {
		assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 5));
	}

	@Test
	public void testPocatecniZustatek() throws IOException {
		assertEquals(parser.getPocatecniZustatek(), new BigDecimal("24582.01"));
	}

	@Test
	public void testKonecnyZustatek() throws IOException {
		assertEquals(parser.getKonecnyZustatek(), new BigDecimal("12430.01"));
	}

	@Test
	public void testTransakce() throws IOException {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(1L);
		transakce.setDatum(LocalDate.of(2016, 4, 5));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 5, 9, 12));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 5));
		transakce.setCastka(new BigDecimal("-2552.00"));
		transakce.setBankovniUcet(new BankovniUcet("Kabel Kabelovic", null, "676706767", "5500", null));
		transakce.setVariabilniSymbol("2016004");
		transakce.setTyp("Prevod");
		transakce.setKomentar("kabelaz Kotelna Hybesova");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(2L);
		transakce.setDatum(LocalDate.of(2016, 4, 5));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 5, 9, 18));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 5));
		transakce.setCastka(new BigDecimal("-9600.00"));
		transakce.setBankovniUcet(new BankovniUcet(null, null, "2900089556", "2010", null));
		transakce.setVariabilniSymbol("16230");
		transakce.setTyp("Prevod");
		transakce.setKomentar("Okruh freetel - 3/2016");
		expected.add(transakce);

		assertEquals(parser.getTransakce(), expected);
	}
}