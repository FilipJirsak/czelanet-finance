package net.czela.bank.dto;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class BankovniUcetTest {

	@Test
	public void testParseJednoduche() {
		BankovniUcet bu = BankovniUcet.parse("2600392940/2010");
		assertNull(bu.getPredcisli());
		assertEquals(bu.getCislo(), "2600392940");
		assertEquals(bu.getKodBanky(), "2010");
		assertEquals(bu.getCeleCislo(), "2600392940/2010");
	}

	@Test
	public void testParsePredcisli() {
		BankovniUcet bu = BankovniUcet.parse("123-2600392940/2010");
		assertEquals(bu.getPredcisli(), "123");
		assertEquals(bu.getCislo(), "2600392940");
		assertEquals(bu.getKodBanky(), "2010");
		assertEquals(bu.getCeleCislo(), "123-2600392940/2010");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne1() {
		BankovniUcet.parse("00-00-2600392940/2010");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne2() {
		BankovniUcet.parse("26003A2940/2010");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne3() {
		BankovniUcet.parse("0A-2600392940/2010");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne4() {
		BankovniUcet.parse("2600392940");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne5() {
		BankovniUcet.parse("01-2600392940");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne6() {
		BankovniUcet.parse("2600392940/010");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne7() {
		BankovniUcet.parse("2600392940/A010");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testParseChybne8() {
		BankovniUcet.parse("2600392940/12345");
	}

}