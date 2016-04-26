package net.czela.bank.fio;

import net.czela.bank.dto.BankovniTransakce;
import org.dom4j.DocumentException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.testng.Assert.*;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class EmptyFioXmlVypisParserTest extends AbstractFioXmlVypisParserTest {
	public EmptyFioXmlVypisParserTest() throws IOException, DocumentException {
		super("empty.xml");
	}

	@Test
	public void testCisloVypisu() throws IOException {
		assertNull(parser.getCisloVypisu());
	}

	@Test
	public void testObdobiVypisuOd() throws IOException {
		assertEquals(parser.getObdobiVypisuOd(), LocalDate.of(2016, 4, 24));
	}

	@Test
	public void testObdobiVypisuDo() throws IOException {
		assertEquals(parser.getObdobiVypisuDo(), LocalDate.of(2016, 4, 24));
	}

	@Test
	public void testPocatecniZustatek() throws IOException {
		assertEquals(parser.getPocatecniZustatek(), new BigDecimal("599388.01"));
	}

	@Test
	public void testKonecnyZustatek() throws IOException {
		assertEquals(parser.getKonecnyZustatek(), new BigDecimal("599388.01"));
	}

	@Test
	public void testTransakce() throws IOException {
		BankovniTransakce transakce;
		assertTrue(parser.getTransakce().isEmpty());
	}
}