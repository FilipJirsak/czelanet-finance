package net.czela.bank.dto;

/**
 * Created by jirsakf on 25.4.2016.
 */
public class VypisRaw {
	private int id;
	private String vypis;
	private Banka banka;

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

	public int getBankaId() {
		return banka.getId();
	}

	public void setBankaId(int bankaId) {
		this.banka =  Banka.values()[bankaId-1];
	}

	public Banka getBanka() {
		return banka;
	}

	public void setBanka(Banka banka) {
		this.banka = banka;
	}
}
