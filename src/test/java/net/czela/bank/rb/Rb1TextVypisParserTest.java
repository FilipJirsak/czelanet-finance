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

import static org.testng.Assert.*;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class Rb1TextVypisParserTest extends AbstractRbTextVypisParserTest {
	public Rb1TextVypisParserTest() {
		super("rb-1.txt");
	}

	@Test
	public void testCisloVypisu() throws IOException {
		assertEquals(parser.getCisloVypisu(), "67");
	}

	@Test
	public void testObdobiVypisuOd() throws IOException {
		assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 13));
	}

	@Test
	public void testObdobiVypisuDo() throws IOException {
		assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 13));
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
	public void testTransakce() throws IOException {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(1L);
		transakce.setDatum(LocalDate.of(2016, 4, 13));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 13, 5, 25));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 13));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setBankovniUcet(new BankovniUcet("NOVAK JIRI", "43", "6172590012", "0100", null));
		transakce.setVariabilniSymbol("7080");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(2L);
		transakce.setDatum(LocalDate.of(2016, 4, 13));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 13, 7, 10));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 13));
		transakce.setKomentar("Internet");
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setBankovniUcet(new BankovniUcet("BILA ANASTAZIE", null, "150970012", "0600", null));
		transakce.setVariabilniSymbol("5915");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		assertEquals(parser.getTransakce(), expected);
	}
}