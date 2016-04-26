package net.czela.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jirsakf on 26.4.2016.
 */
public class UploadovanyVypis {
	private String vypis;
	private String cisloVypisu;
	private LocalDate obdobiOd;
	private LocalDate obdobiDo;
	private BigDecimal pocatecniZustatek;
	private BigDecimal konecnyZustatek;
	private Banka banka;

	public String getVypis() {
		return vypis;
	}

	public void setVypis(String vypis) {
		this.vypis = vypis;
	}

	public String getCisloVypisu() {
		return cisloVypisu;
	}

	public void setCisloVypisu(String cisloVypisu) {
		this.cisloVypisu = cisloVypisu;
	}

	public LocalDate getObdobiOd() {
		return obdobiOd;
	}

	public void setObdobiOd(LocalDate obdobiOd) {
		this.obdobiOd = obdobiOd;
	}

	public LocalDate getObdobiDo() {
		return obdobiDo;
	}

	public void setObdobiDo(LocalDate obdobiDo) {
		this.obdobiDo = obdobiDo;
	}

	public BigDecimal getPocatecniZustatek() {
		return pocatecniZustatek;
	}

	public void setPocatecniZustatek(BigDecimal pocatecniZustatek) {
		this.pocatecniZustatek = pocatecniZustatek;
	}

	public BigDecimal getKonecnyZustatek() {
		return konecnyZustatek;
	}

	public void setKonecnyZustatek(BigDecimal konecnyZustatek) {
		this.konecnyZustatek = konecnyZustatek;
	}

	public Banka getBanka() {
		return banka;
	}

	public void setBanka(Banka banka) {
		this.banka = banka;
	}
}
