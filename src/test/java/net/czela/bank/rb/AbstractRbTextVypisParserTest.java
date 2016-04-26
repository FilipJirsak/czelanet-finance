package net.czela.bank.rb;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by jirsakf on 20.4.2016.
 */
public abstract class AbstractRbTextVypisParserTest {
	protected final RbTextVypisParser parser;

	public AbstractRbTextVypisParserTest(String filename) {
		this.parser = new RbTextVypisParser(AbstractRbTextVypisParserTest.class.getResourceAsStream(filename));
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


}
