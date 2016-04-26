package net.czela.bank.fio;

import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.BankovniUcet;
import org.dom4j.DocumentException;
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
public class Fio1XmlVypisParserTest extends AbstractFioXmlVypisParserTest {
	public Fio1XmlVypisParserTest() throws IOException, DocumentException {
		super("fio-1.xml");
	}

	@Test
	public void testCisloVypisu() throws IOException {
		assertEquals(parser.getCisloVypisu(), "9769329701-9769457101");
	}

	@Test
	public void testObdobiVypisuOd() throws IOException {
		assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 22));
	}

	@Test
	public void testObdobiVypisuDo() throws IOException {
		assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 25));
	}

	@Test
	public void testPocatecniZustatek() throws IOException {
		assertEquals(parser.getPocatecniZustatek(), new BigDecimal("599388.01"));
	}

	@Test
	public void testKonecnyZustatek() throws IOException {
		assertEquals(parser.getKonecnyZustatek(), new BigDecimal("602538.01"));
	}

	@Test
	public void testTransakce() throws IOException {
		BankovniTransakce transakce;
		List<BankovniTransakce> expected = new LinkedList<>();

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769329701L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Černý Petr", null, "1293341234", "0800", "Česká spořitelna, a.s."));
		transakce.setVariabilniSymbol("6704");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Černý Petr");
		transakce.setKomentar("Černý Petr");
		transakce.setIdPokynu("11011715003");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769333818L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("1050.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Čapek Karel", "35", "5608123456", "0100", "Komerční banka a.s."));
		transakce.setVariabilniSymbol("6767");
		transakce.setKonstantniSymbol("0000");
		transakce.setSpecifickySymbol("0");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Čapek Karel");
		transakce.setZpravaProPrijemce("PLATBA NETU PLATBA NETU");
		transakce.setKomentar("Čapek Karel");
		transakce.setIdPokynu("11011731663");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769349872L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Tichá Klára", null, "820490123", "0800", "Česká spořitelna, a.s."));
		transakce.setVariabilniSymbol("7182");
		transakce.setKonstantniSymbol("0308");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Tichá Klára");
		transakce.setKomentar("Tichá Klára");
		transakce.setZpravaProPrijemce("Tichá Klára,Rovná 1,Čelákovice");
		transakce.setIdPokynu("11011796276");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769380758L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Červená Růžena", null, "173401111", "0600", "GE Money Bank, a.s."));
		transakce.setVariabilniSymbol("6890");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Červená Růžena");
		transakce.setKomentar("Červená Růžena");
		transakce.setZpravaProPrijemce("Červená Růžena");
		transakce.setIdPokynu("11011919240");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769380759L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Novák Jan", null, "209633123", "0600", "GE Money Bank, a.s."));
		transakce.setVariabilniSymbol("7418");
		transakce.setKonstantniSymbol("0308");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Novák Jan");
		transakce.setKomentar("Novák Jan");
		transakce.setIdPokynu("11011919241");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769412468L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Bílá Hana", "670100", "2206381234", "6210", "mBank S.A., organizační složka"));
		transakce.setVariabilniSymbol("6258");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Bílá Hana");
		transakce.setKomentar("Bílá Hana");
		transakce.setZpravaProPrijemce("CZELA.NET");
		transakce.setIdPokynu("11012129224");
		expected.add(transakce);

		transakce = new BankovniTransakce();
		transakce.setIdTransakce(9769457101L);
		transakce.setDatum(LocalDate.of(2016, 4, 25));
		transakce.setCastka(new BigDecimal("350.00"));
		transakce.setMena("CZK");
		transakce.setBankovniUcet(new BankovniUcet("Malá Petra", null, "1738449123", "0800", "Česká spořitelna, a.s."));
		transakce.setVariabilniSymbol("6861");
		transakce.setTyp("Bezhotovostní příjem");
		transakce.setUzivatelskaIdentifikace("Malá Petra");
		transakce.setKomentar("Malá Petra");
		transakce.setIdPokynu("11012308663");
		expected.add(transakce);

		assertEquals(parser.getTransakce(), expected);
	}
}