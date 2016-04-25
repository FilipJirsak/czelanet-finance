package net.czela.bank.dto;

import jodd.util.StringUtil;

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
	private static final Pattern RE_FORMAT_PREDCISLI_CISLO = Pattern.compile("(?:(\\d{1,6})-)?(\\d{1,10})");

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
		if (StringUtil.isAllEmpty(predcisli, cislo, kodBanky)) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		getCeleCislo(builder);

		return builder.toString();
	}

	protected void getCeleCislo(StringBuilder builder) {
		if (StringUtil.isAllEmpty(predcisli, cislo, kodBanky)) {
			return;
		}
		if (predcisli != null) {
			builder.append(predcisli);
			builder.append('-');
		}
		builder.append(cislo);
		builder.append('/');
		builder.append(kodBanky);
	}

	public void setCeleCislo(String text) {
		if (text == null) {
			return;
		}

		Matcher matcher = RE_FORMAT.matcher(text);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(String.format("Chybný formát čísla účtu: %s", text));
		}

		this.predcisli = matcher.group(1);
		this.cislo = matcher.group(2);
		this.kodBanky = matcher.group(3);
	}

	public void setPredcisliCislo(String text) {
		if (text == null) {
			return;
		}

		Matcher matcher = RE_FORMAT_PREDCISLI_CISLO.matcher(text);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(String.format("Chybný formát čísla účtu: %s", text));
		}

		this.predcisli = matcher.group(1);
		this.cislo = matcher.group(2);
	}

	public static BankovniUcet parse(String text) {
		if (text == null) {
			return null;
		}

		BankovniUcet bankovniUcet = new BankovniUcet();
		bankovniUcet.setCeleCislo(text);
		return bankovniUcet;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		getCeleCislo(builder);
		if (StringUtil.isNotEmpty(nazev)) {
			builder.append(' ');
			builder.append('[');
			builder.append(nazev);
			builder.append(']');
		}
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BankovniUcet)) return false;

		BankovniUcet that = (BankovniUcet) o;

		if (nazev != null ? !nazev.equals(that.nazev) : that.nazev != null) return false;
		if (predcisli != null ? !predcisli.equals(that.predcisli) : that.predcisli != null) return false;
		if (!cislo.equals(that.cislo)) return false;
		if (!kodBanky.equals(that.kodBanky)) return false;
		return nazevBanky != null ? nazevBanky.equals(that.nazevBanky) : that.nazevBanky == null;

	}

	@Override
	public int hashCode() {
		int result = nazev != null ? nazev.hashCode() : 0;
		result = 31 * result + (predcisli != null ? predcisli.hashCode() : 0);
		result = 31 * result + cislo.hashCode();
		result = 31 * result + kodBanky.hashCode();
		result = 31 * result + (nazevBanky != null ? nazevBanky.hashCode() : 0);
		return result;
	}
}
