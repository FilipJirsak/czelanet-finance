package net.czela.bank.fio;

/**
 * Created by jirsakf on 25.4.2016.
 */
public enum FioAPIFormatVypisu implements FioAPIFormat {
	MT940("sta"),
	PDF("pdf"),
	SBA_XML("sba_xml"),
	ČBA_XML("cba_xml"),;

	private final String format;

	FioAPIFormatVypisu(String format) {
		this.format = format;
	}

	@Override
	public String getFormat() {
		return format;
	}

}
