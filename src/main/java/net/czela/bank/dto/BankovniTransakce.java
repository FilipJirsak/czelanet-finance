package net.czela.bank.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class BankovniTransakce {
	private Long idTransakce;

	@NotNull
	private LocalDate datum;

	@NotNull
	private BigDecimal castka;

	private BigDecimal poplatek;

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
}
