package net.czela.bank.rb;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

/**
 * Created by jirsakf on 20.4.2016.
 */
public abstract class AbstractRbTextVypisParserTest {
	protected final RbTextVypisParser parser;

	public AbstractRbTextVypisParserTest(String filename) {
		this.parser = new RbTextVypisParser(Rb1TextVypisParserTest.class.getResourceAsStream(filename));
	}

	@BeforeClass
	public void init() throws IOException {
		parser.read();
	}

	@AfterClass
	public void close() throws IOException {
		parser.close();
	}
}
