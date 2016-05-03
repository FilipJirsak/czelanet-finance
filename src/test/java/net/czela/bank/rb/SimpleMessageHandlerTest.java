package net.czela.bank.rb;

import jodd.io.StreamUtil;
import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.BankovniUcet;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by jirsakf on 3.5.2016.
 */
public class SimpleMessageHandlerTest {

	private final SimpleMessageHandler messageHandler;

	public SimpleMessageHandlerTest() {
		this.messageHandler = new SimpleMessageHandler(null, null);
	}

	@Test
	public void testParseMessage2120() throws Exception {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(1L);
		transakce.setDatum(LocalDate.of(2016, 4, 26));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 26, 3, 28));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 26));
		transakce.setCastka(new BigDecimal("1050.00"));
		transakce.setBankovniUcet(new BankovniUcet("Lucie Novakova", null, "213641234", "0800", null));
		transakce.setVariabilniSymbol("6835");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(2L);
		transakce.setDatum(LocalDate.of(2016, 4, 26));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 26, 3, 29));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 26));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setBankovniUcet(new BankovniUcet("Riha Jiri", null, "193580123", "0100", null));
		transakce.setVariabilniSymbol("7029");
		transakce.setKonstantniSymbol("308");
		transakce.setTyp("Prichozi platba");
		expected.add(transakce);

		String vypis = parseVypis("2120");
		try (RbTextVypisParser parser = new RbTextVypisParser(vypis)) {
			parser.read();
			testHeader(parser);
			assertEquals(parser.getCisloVypisu(), "75");
			assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 26));
			assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 26));
			assertEquals(parser.getPocatecniZustatek(), new BigDecimal("38980.01"));
			assertEquals(parser.getKonecnyZustatek(), new BigDecimal("40380.01"));
			assertEquals(parser.getTransakce(), expected);
		}
	}

	@Test
	public void testParseMessage2121() throws Exception {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(1L);
		transakce.setDatum(LocalDate.of(2016, 4, 27));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 27, 2, 17));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 27));
		transakce.setCastka(new BigDecimal("1050.00"));
		transakce.setBankovniUcet(new BankovniUcet("Honza Hanak", null, "185111234", "0300", null));
		transakce.setVariabilniSymbol("7015");
		transakce.setKonstantniSymbol("225");
		transakce.setTyp("Prichozi platba");
		transakce.setKomentar("129049");
		expected.add(transakce);

		String vypis = parseVypis("2121");
		try (RbTextVypisParser parser = new RbTextVypisParser(vypis)) {
			parser.read();
			testHeader(parser);
			assertEquals(parser.getCisloVypisu(), "76");
			assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 27));
			assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 27));
			assertEquals(parser.getPocatecniZustatek(), new BigDecimal("40380.01"));
			assertEquals(parser.getKonecnyZustatek(), new BigDecimal("41430.01"));
			assertEquals(parser.getTransakce(), expected);
		}
	}

	@Test
	public void testParseMessage2123() throws Exception {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(1L);
		transakce.setDatum(LocalDate.of(2016, 4, 30));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 30, 23, 59));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 30));
		transakce.setPoplatek(new BigDecimal("-500.00"));
		transakce.setKonstantniSymbol("898");
		transakce.setTyp("Sprava uctu");
		transakce.setKomentar("Sprava uctu 1222733001");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(2L);
		transakce.setDatum(LocalDate.of(2016, 4, 30));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 30, 23, 59));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 30));
		transakce.setCastka(new BigDecimal("0.24"));
		transakce.setBankovniUcet(new BankovniUcet("czela.net", null, "1222733001", "5500", null));
		transakce.setKonstantniSymbol("598");
		transakce.setTyp("Kladny urok");
		transakce.setKomentar("Urok 04/2016");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(3L);
		transakce.setDatum(LocalDate.of(2016, 4, 30));
		transakce.setDatumCas(LocalDateTime.of(2016, 4, 30, 23, 59));
		transakce.setDatumOdepsano(LocalDate.of(2016, 4, 30));
		transakce.setCastka(new BigDecimal("-0.05"));
		transakce.setBankovniUcet(new BankovniUcet("Srazkova dan z BU PO", null, "8347092", "5500", null));
		transakce.setKonstantniSymbol("1148");
		transakce.setTyp("Srazka dane z uroku");
		transakce.setKomentar("Srazkova dan");
		expected.add(transakce);

		String vypis = parseVypis("2123");
		try (RbTextVypisParser parser = new RbTextVypisParser(vypis)) {
			parser.read();
			testHeader(parser);
			assertEquals(parser.getCisloVypisu(), "78");
			assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 29));
			assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 30));
			assertEquals(parser.getPocatecniZustatek(), new BigDecimal("41780.01"));
			assertEquals(parser.getKonecnyZustatek(), new BigDecimal("41280.20"));
			assertEquals(parser.getTransakce(), expected);
		}
	}

	protected String parseVypis(String name) throws IOException, MessagingException {
		try (InputStream inputStream = SimpleMessageHandlerTest.class.getResourceAsStream(String.format("%s.eml", name))) {
			String vypis = messageHandler.parseMessage(inputStream);
			String expected = new String(StreamUtil.readChars(SimpleMessageHandlerTest.class.getResourceAsStream(String.format("%s.txt", name))));
			Assert.assertEquals(vypis, expected);
			return vypis;
		}
	}

	protected void testHeader(RbTextVypisParser parser) {
		assertEquals(parser.getBankovniUcet().getNazevBanky(), "Raiffeisenbank a.s.");
		assertEquals(parser.getBankovniUcet().getNazev(), "czela.net");
		assertNull(parser.getBankovniUcet().getPredcisli());
		assertEquals(parser.getBankovniUcet().getCislo(), "1222733001");
		assertEquals(parser.getBankovniUcet().getKodBanky(), "5500");
	}
}