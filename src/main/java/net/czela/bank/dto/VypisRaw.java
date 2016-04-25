package net.czela.bank.dto;

/**
 * Created by jirsakf on 25.4.2016.
 */
public class VypisRaw {
	private int id;
	private String vypis;
	private String banka;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVypis() {
		return vypis;
	}

	public void setVypis(String vypis) {
		this.vypis = vypis;
	}

	public String getBanka() {
		return banka;
	}

	public void setBanka(String banka) {
		this.banka = banka;
	}
}
