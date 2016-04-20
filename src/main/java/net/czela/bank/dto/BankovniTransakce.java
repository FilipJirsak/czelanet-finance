package net.czela.bank.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class BankovniTransakce {
	private Long idTransakce;

	@NotNull
	private LocalDate datum;

	private LocalDateTime datumCas;

	private LocalDate datumOdepsano;

	@NotNull
	private BigDecimal castka;

	private BigDecimal poplatek;

	private BigDecimal poplatekSmena;

	private BigDecimal poplatekZprava;

	@NotNull
	private String mena;

	private BankovniUcet bankovniUcet =  new BankovniUcet();

	private String uzivatelskaIdentifikace;

	private String zpravaProPrijemce;

	private String komentar;

	private String typ;

	@Digits(integer = 10, fraction = 0)
	private String variabilniSymbol;

	@Digits(integer = 4, fraction = 0)
	private String konstantniSymbol;

	@Digits(integer = 10, fraction = 0)
	private String specifickySymbol;

	private String idPokynu;

	public Long getIdTransakce() {
		return idTransakce;
	}

	public void setIdTransakce(Long idTransakce) {
		this.idTransakce = idTransakce;
	}

	public LocalDate getDatum() {
		return datum;
	}

	public void setDatum(LocalDate datum) {
		this.datum = datum;
	}

	public LocalDateTime getDatumCas() {
		return datumCas;
	}

	public void setDatumCas(LocalDateTime datumCas) {
		this.datumCas = datumCas;
	}

	public LocalDate getDatumOdepsano() {
		return datumOdepsano;
	}

	public void setDatumOdepsano(LocalDate datumOdepsano) {
		this.datumOdepsano = datumOdepsano;
	}

	public BigDecimal getCastka() {
		return castka;
	}

	public void setCastka(BigDecimal castka) {
		this.castka = castka;
	}

	public BigDecimal getPoplatek() {
		return poplatek;
	}

	public void setPoplatek(BigDecimal poplatek) {
		this.poplatek = poplatek;
	}

	public String getMena() {
		return mena;
	}

	public void setMena(String mena) {
		this.mena = mena;
	}

	public BankovniUcet getBankovniUcet() {
		return bankovniUcet;
	}

	public void setBankovniUcet(BankovniUcet bankovniUcet) {
		this.bankovniUcet = bankovniUcet;
	}

	public String getUzivatelskaIdentifikace() {
		return uzivatelskaIdentifikace;
	}

	public void setUzivatelskaIdentifikace(String uzivatelskaIdentifikace) {
		this.uzivatelskaIdentifikace = uzivatelskaIdentifikace;
	}

	public String getZpravaProPrijemce() {
		return zpravaProPrijemce;
	}

	public void setZpravaProPrijemce(String zpravaProPrijemce) {
		this.zpravaProPrijemce = zpravaProPrijemce;
	}

	public String getKomentar() {
		return komentar;
	}

	public void setKomentar(String komentar) {
		this.komentar = komentar;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getVariabilniSymbol() {
		return variabilniSymbol;
	}

	public void setVariabilniSymbol(String variabilniSymbol) {
		this.variabilniSymbol = variabilniSymbol;
	}

	public String getKonstantniSymbol() {
		return konstantniSymbol;
	}

	public void setKonstantniSymbol(String konstantniSymbol) {
		this.konstantniSymbol = konstantniSymbol;
	}

	public String getSpecifickySymbol() {
		return specifickySymbol;
	}

	public void setSpecifickySymbol(String specifickySymbol) {
		this.specifickySymbol = specifickySymbol;
	}

	public String getIdPokynu() {
		return idPokynu;
	}

	public void setIdPokynu(String idPokynu) {
		this.idPokynu = idPokynu;
	}

	public BigDecimal getPoplatekSmena() {
		return poplatekSmena;
	}

	public void setPoplatekSmena(BigDecimal poplatekSmena) {
		this.poplatekSmena = poplatekSmena;
	}

	public BigDecimal getPoplatekZprava() {
		return poplatekZprava;
	}

	public void setPoplatekZprava(BigDecimal poplatekZprava) {
		this.poplatekZprava = poplatekZprava;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(idTransakce);
		sb.append(' ');
		sb.append(datum);
		sb.append(' ');
		bankovniUcet.getCeleCislo(sb);
		sb.append(' ');
		sb.append(castka);
		sb.append(' ');
		sb.append(mena);
		if (variabilniSymbol != null) {
			sb.append(" VS: ");
			sb.append(variabilniSymbol);
		}
		if (konstantniSymbol != null) {
			sb.append(" KS: ");
			sb.append(konstantniSymbol);
		}
		if (specifickySymbol != null) {
			sb.append(" SS: ");
			sb.append(specifickySymbol);
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BankovniTransakce)) return false;

		BankovniTransakce that = (BankovniTransakce) o;

		if (!idTransakce.equals(that.idTransakce)) return false;
		if (!datum.equals(that.datum)) return false;
		if (!castka.equals(that.castka)) return false;
		if (datumCas != null ? !datumCas.equals(that.datumCas) : that.datumCas != null) return false;
		if (datumOdepsano != null ? !datumOdepsano.equals(that.datumOdepsano) : that.datumOdepsano != null) return false;
		if (poplatek != null ? !poplatek.equals(that.poplatek) : that.poplatek != null) return false;
		if (poplatekSmena != null ? !poplatekSmena.equals(that.poplatekSmena) : that.poplatekSmena != null) return false;
		if (poplatekZprava != null ? !poplatekZprava.equals(that.poplatekZprava) : that.poplatekZprava != null) return false;
		if (mena != null ? !mena.equals(that.mena) : that.mena != null) return false;
		if (bankovniUcet != null ? !bankovniUcet.equals(that.bankovniUcet) : that.bankovniUcet != null) return false;
		if (uzivatelskaIdentifikace != null ? !uzivatelskaIdentifikace.equals(that.uzivatelskaIdentifikace) : that.uzivatelskaIdentifikace != null) return false;
		if (zpravaProPrijemce != null ? !zpravaProPrijemce.equals(that.zpravaProPrijemce) : that.zpravaProPrijemce != null) return false;
		if (komentar != null ? !komentar.equals(that.komentar) : that.komentar != null) return false;
		if (typ != null ? !typ.equals(that.typ) : that.typ != null) return false;
		if (variabilniSymbol != null ? !variabilniSymbol.equals(that.variabilniSymbol) : that.variabilniSymbol != null) return false;
		if (konstantniSymbol != null ? !konstantniSymbol.equals(that.konstantniSymbol) : that.konstantniSymbol != null) return false;
		if (specifickySymbol != null ? !specifickySymbol.equals(that.specifickySymbol) : that.specifickySymbol != null) return false;
		return idPokynu != null ? idPokynu.equals(that.idPokynu) : that.idPokynu == null;

	}

	@Override
	public int hashCode() {
		int result = idTransakce.hashCode();
		result = 31 * result + datum.hashCode();
		result = 31 * result + castka.hashCode();
		result = 31 * result + (datumCas != null ? datumCas.hashCode() : 0);
		result = 31 * result + (datumOdepsano != null ? datumOdepsano.hashCode() : 0);
		result = 31 * result + (poplatek != null ? poplatek.hashCode() : 0);
		result = 31 * result + (poplatekSmena != null ? poplatekSmena.hashCode() : 0);
		result = 31 * result + (poplatekZprava != null ? poplatekZprava.hashCode() : 0);
		result = 31 * result + (mena != null ? mena.hashCode() : 0);
		result = 31 * result + (bankovniUcet != null ? bankovniUcet.hashCode() : 0);
		result = 31 * result + (uzivatelskaIdentifikace != null ? uzivatelskaIdentifikace.hashCode() : 0);
		result = 31 * result + (zpravaProPrijemce != null ? zpravaProPrijemce.hashCode() : 0);
		result = 31 * result + (komentar != null ? komentar.hashCode() : 0);
		result = 31 * result + (typ != null ? typ.hashCode() : 0);
		result = 31 * result + (variabilniSymbol != null ? variabilniSymbol.hashCode() : 0);
		result = 31 * result + (konstantniSymbol != null ? konstantniSymbol.hashCode() : 0);
		result = 31 * result + (specifickySymbol != null ? specifickySymbol.hashCode() : 0);
		result = 31 * result + (idPokynu != null ? idPokynu.hashCode() : 0);
		return result;
	}
}
