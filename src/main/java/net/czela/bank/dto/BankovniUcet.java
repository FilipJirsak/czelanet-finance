package net.czela.bank.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class BankovniUcet {

	private static final Pattern RE_FORMAT = Pattern.compile("(?:(\\d{1,6})-)?(\\d{1,10})/(\\d{4})");

	private String nazev;

	@Digits(integer = 6, fraction = 0)
	private String predcisli;

	@NotNull
	@Digits(integer = 10, fraction = 0)
	private String cislo;

	@NotNull
	@Digits(integer = 4, fraction = 0)
	private String kodBanky;

	private String nazevBanky;

	public BankovniUcet() {
	}

	public BankovniUcet(String nazev, String predcisli, String cislo, String kodBanky, String nazevBanky) {
		this.nazev = nazev;
		this.predcisli = predcisli;
		this.cislo = cislo;
		this.kodBanky = kodBanky;
		this.nazevBanky = nazevBanky;
	}

	public BankovniUcet(String predcisli, String cislo, String kodBanky) {
		this.predcisli = predcisli;
		this.cislo = cislo;
		this.kodBanky = kodBanky;
	}

	public String getNazev() {
		return nazev;
	}

	public void setNazev(String nazev) {
		this.nazev = nazev;
	}

	public String getPredcisli() {
		return predcisli;
	}

	public void setPredcisli(String predcisli) {
		this.predcisli = predcisli;
	}

	public String getCislo() {
		return cislo;
	}

	public void setCislo(String cislo) {
		this.cislo = cislo;
	}

	public String getKodBanky() {
		return kodBanky;
	}

	public void setKodBanky(String kodBanky) {
		this.kodBanky = kodBanky;
	}

	public String getNazevBanky() {
		return nazevBanky;
	}

	public void setNazevBanky(String nazevBanky) {
		this.nazevBanky = nazevBanky;
	}

	public String getCeleCislo() {
		StringBuilder builder = new StringBuilder();
		if (predcisli != null) {
			builder.append(predcisli);
			builder.append('-');
		}
		builder.append(cislo);
		builder.append('/');
		builder.append(kodBanky);

		return builder.toString();
	}

	public void setCeleCislo(String text) {
		assert text != null;

		Matcher matcher = RE_FORMAT.matcher(text);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(String.format("Chybný formát čísla účtu: %s", text));
		}

		this.predcisli = matcher.group(1);
		this.cislo = matcher.group(2);
		this.kodBanky = matcher.group(3);
	}

	public static BankovniUcet parse(String text) {
		if (text == null) {
			return null;
		}

		BankovniUcet bankovniUcet = new BankovniUcet();
		bankovniUcet.setCeleCislo(text);
		return bankovniUcet;
	}
}
