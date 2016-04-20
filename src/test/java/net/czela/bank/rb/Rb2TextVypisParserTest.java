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
public class Rb2TextVypisParserTest extends AbstractRbTextVypisParserTest {
	public Rb2TextVypisParserTest() {
		super("rb-2.txt");
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
		assertEquals(parser.getCisloVypisu(), 130);
	}

	@Test
	public void testObdobiVypisuOd() throws IOException {
		assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2014, 6, 21));
	}

	@Test
	public void testObdobiVypisuDo() throws IOException {
		assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2014, 6, 23));
	}

	@Test
	public void testPocatecniZustatek() throws IOException {
		assertEquals(parser.getPocatecniZustatek(), new BigDecimal("300000.00"));
	}

	@Test
	public void testKonecnyZustatek() throws IOException {
		assertEquals(parser.getKonecnyZustatek(), new BigDecimal("304200.00"));
	}

	@Test
	public void testTransakce() throws IOException {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(1L);
		transakce.setDatum(LocalDate.of(2014, 6, 23));
		transakce.setDatumCas(LocalDateTime.of(2014, 6, 23, 1, 40));
		transakce.setDatumOdepsano(LocalDate.of(2014, 6, 23));
		transakce.setCastka(new BigDecimal("1050.00"));
		transakce.setBankovniUcet(new BankovniUcet("NOVAK FILIP", "19", "5449850123", "0100", null));
		transakce.setVariabilniSymbol("5812");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(2L);
		transakce.setDatum(LocalDate.of(2014, 6, 23));
		transakce.setDatumCas(LocalDateTime.of(2014, 6, 23, 1, 48));
		transakce.setDatumOdepsano(LocalDate.of(2014, 6, 23));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setBankovniUcet(new BankovniUcet("NOVAKOVA LENKA", null, "8021234", "0300", null));
		transakce.setVariabilniSymbol("6618");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(3L);
		transakce.setDatum(LocalDate.of(2014, 6, 23));
		transakce.setDatumCas(LocalDateTime.of(2014, 6, 23, 5, 30));
		transakce.setDatumOdepsano(LocalDate.of(2014, 6, 23));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setBankovniUcet(new BankovniUcet("NOVAK RADEK", null, "860543012", "0800", null));
		transakce.setSpecifickySymbol("549208481");
		transakce.setVariabilniSymbol("7045");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(4L);
		transakce.setDatum(LocalDate.of(2014, 6, 23));
		transakce.setDatumCas(LocalDateTime.of(2014, 6, 23, 5, 30));
		transakce.setDatumOdepsano(LocalDate.of(2014, 6, 23));
		transakce.setCastka(new BigDecimal("1050.00"));
		transakce.setBankovniUcet(new BankovniUcet("Novak Jaroslav", null, "234501234", "0800", null));
		transakce.setVariabilniSymbol("5133");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(5L);
		transakce.setDatum(LocalDate.of(2014, 6, 23));
		transakce.setDatumCas(LocalDateTime.of(2014, 6, 23, 6, 55));
		transakce.setDatumOdepsano(LocalDate.of(2014, 6, 23));
		transakce.setCastka(new BigDecimal("1050.00"));
		transakce.setBankovniUcet(new BankovniUcet("Zajickova Stanisla", null, "1995830123", "0800", null));
		transakce.setVariabilniSymbol("6434");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		transakce.setKomentar("Internet - 3.ctvrtleti 2014");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(6L);
		transakce.setDatum(LocalDate.of(2014, 6, 23));
		transakce.setDatumCas(LocalDateTime.of(2014, 6, 23, 7, 04));
		transakce.setDatumOdepsano(LocalDate.of(2014, 6, 23));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setBankovniUcet(new BankovniUcet("Novak Jan", null, "2485060123", "0800", null));
		transakce.setVariabilniSymbol("7043");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		assertEquals(parser.getTransakce(), expected);
	}
}