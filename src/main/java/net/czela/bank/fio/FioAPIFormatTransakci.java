package net.czela.bank.fio;

/**
 * Created by jirsakf on 25.4.2016.
 */
public enum FioAPIFormatTransakci implements FioAPIFormat {
	XML("xml"),
	OFX("ofx"),
	GPC("gpc"),
	CSV("csv"),
	HTML("html"),
	JSON("json"),
	;

	private final String format;

	FioAPIFormatTransakci(String format) {
		this.format = format;
	}

	@Override
	public String getFormat() {
		return format;
	}
}
