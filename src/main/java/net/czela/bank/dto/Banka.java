package net.czela.bank.dto;

/**
 * Created by jirsakf on 26.4.2016.
 */
public enum Banka {
	RAIFFEISENBANK(1, "Raiffeisenbank"),
	FIO(2, "Fio"),;

	private final int id;
	private final String nazev;

	Banka(int id, String nazev) {
		this.id = id;
		this.nazev = nazev;
	}

	public int getId() {
		return id;
	}

	public String getNazev() {
		return nazev;
	}
}
