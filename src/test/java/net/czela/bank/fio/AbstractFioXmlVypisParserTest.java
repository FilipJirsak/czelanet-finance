package net.czela.bank.fio;

import jodd.io.StreamUtil;
import org.dom4j.DocumentException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by jirsakf on 20.4.2016.
 */
public abstract class AbstractFioXmlVypisParserTest {
	protected final FioXmlVypisParser parser;

	public AbstractFioXmlVypisParserTest(String filename) throws IOException, DocumentException {
		this.parser = new FioXmlVypisParser(new String(StreamUtil.readChars(AbstractFioXmlVypisParserTest.class.getResourceAsStream(filename), "UTF-8")));
	}

	@BeforeClass
	public void init() throws IOException {
		parser.read();
	}

	@AfterClass
	public void close() throws IOException {
		parser.close();
	}

	@Test
	public void testNazevBanky() throws IOException {
		assertNull(parser.getBankovniUcet().getNazevBanky());

	}

	@Test
	public void testNazevUctu() throws IOException {
		assertNull(parser.getBankovniUcet().getNazev(), null);
	}

	@Test
	public void testPredcisliUctu() throws IOException {
		assertNull(parser.getBankovniUcet().getPredcisli());

	}

	@Test
	public void testCisloUctu() throws IOException {
		assertEquals(parser.getBankovniUcet().getCislo(), "2600392940");

	}

	@Test
	public void testKodBanky() throws IOException {
		assertEquals(parser.getBankovniUcet().getKodBanky(), "2010");
	}


}
