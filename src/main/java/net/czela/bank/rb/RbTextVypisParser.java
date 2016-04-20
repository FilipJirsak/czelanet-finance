package net.czela.bank.rb;

import jodd.util.StringUtil;
import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.BankovniUcet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class RbTextVypisParser implements Closeable {

	private static final Pattern RE_VYPIS_C = Pattern.compile("Bankovni vypis c. (\\d+)");
	private static final Pattern RE_VYPIS_DATUM = Pattern.compile("za (\\d{2}\\.\\d{2}\\.\\d{4})");
	private static final Pattern RE_VYPIS_OBDOBI = Pattern.compile("Za obdobi (\\d{2}\\.\\d{2}\\.\\d{4})/(\\d{2}\\.\\d{2}\\.\\d{4})");

	private static final int LINE_LENGTH = 86;
	private static final String SINGLE_SEPARATOR = StringUtil.repeat('-', LINE_LENGTH);
	private static final String DOUBLE_SEPARATOR = StringUtil.repeat('=', LINE_LENGTH);
	private static final String EMPTY_STRING = "";

	private static final DateTimeFormatter DATUM_VYPISU_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATUM_POHYBU_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.");
	private static final DateTimeFormatter CAS_POHYBU_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	private final Logger logger = LoggerFactory.getLogger(RbTextVypisParser.class);

	private final LineNumberReader reader;
	private Stav stav = Stav.VYPIS;
	private boolean canEnd = false;
	private String line;
	private Matcher matcher;

	private BankovniUcet bankovniUcet = new BankovniUcet();
	private int cisloVypisu;
	private LocalDate obdobiVypisuOd;
	private LocalDate obdobiVypisuDo;
	private BigDecimal pocatecniZustatek;
	private BigDecimal konecnyZustatek;
	private StringBuilder zpravaBuilder = new StringBuilder();
	private String zprava;
	private BankovniTransakce transakce;
	private List<BankovniTransakce> seznamTransakci = new LinkedList<>();

	public RbTextVypisParser(LineNumberReader reader) {
		this.reader = reader;
	}

	public RbTextVypisParser(Reader reader) {
		this(new LineNumberReader(reader));
	}

	public RbTextVypisParser(InputStream inputStream) {
		this(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
	}

	public void read() throws IOException {
		while (nextLine()) {
			switch (stav) {
				case VYPIS:
					parseVypis();
					break;
				case UCET:
					parseUcet();
					break;
				case HLAVICKA_SOUHRNU:
					logger.debug("Hlavička souhrnu: {}", line);
					nextLine();
					doubleSeparator();
					stav = Stav.SOUHRN;
					break;
				case SOUHRN:
					parseSouhrn();
					break;
				case ZPRAVA:
					parseZprava();
					break;
				case HLAVICKA:
					parseHlavicka();
					break;
				case POHYBY:
					parsePohyby();
					break;
				case PATICKA:
					break;
			}
		}
	}

	private boolean nextLine() throws IOException {
		boolean next = (line = reader.readLine()) != null;
		if (!next && !canEnd) {
			throw new IllegalArgumentException(String.format("Neočekávaný konec bankovního výpisu na řádku %d.", reader.getLineNumber()));
		}
		return next;
	}

	private void parseVypis() {
		if (reader.getLineNumber() == 1) {
			logger.debug("Banka: {}", line);
			bankovniUcet.setNazevBanky(line.trim());
		} else if (matches(RE_VYPIS_C)) {
			cisloVypisu = Integer.parseInt(matcher.group(1));
		} else if (matches(RE_VYPIS_DATUM)) {
			obdobiVypisuOd = LocalDate.parse(matcher.group(1), DATUM_VYPISU_FORMATTER);
			obdobiVypisuDo = obdobiVypisuOd;
		} else if (matches(RE_VYPIS_OBDOBI)) {
			obdobiVypisuOd = LocalDate.parse(matcher.group(1), DATUM_VYPISU_FORMATTER);
			obdobiVypisuDo = LocalDate.parse(matcher.group(2), DATUM_VYPISU_FORMATTER);
		} else if (StringUtil.isEmpty(line)) {
			stav = Stav.UCET;
		} else {
			formatException();
		}
	}

	private void parseUcet() {
		if (isDoubleSeparator()) {
			stav = Stav.HLAVICKA_SOUHRNU;
			return;
		}
		if (isEmptyLine()) {
			return;
		}
		String nazev = substring(0, 11);
		String hodnota = substring(12);
		switch (nazev) {
			case "Nazev uctu:":
				logger.debug("Název účtu: {}", hodnota);
				bankovniUcet.setNazev(hodnota);
				break;
			case "Cislo uctu:":
				logger.debug("Číslo účtu: {}", hodnota);
				bankovniUcet.setCeleCislo(hodnota);
				break;
			case "IBAN:":
				logger.debug("IBAN: {}", hodnota);
				break;
			case "Mena:":
				logger.debug("Měna: {}", hodnota);
				break;
		}
	}

	private void parseSouhrn() {
		if (isDoubleSeparator()) {
			stav = Stav.ZPRAVA;
			return;
		}
		String nazev = substring(0, 46);
		String hodnota1 = substring(46, 61);
		String hodnota2 = substring(72);
		logger.debug("Souhrn: {} | {} | {}", nazev, hodnota1, hodnota2);
		switch (nazev) {
			case "Pocatecni zustatek":
				pocatecniZustatek = parseCastka(hodnota2);
				break;
			case "Konecny zustatek":
				konecnyZustatek = parseCastka(hodnota2);
				break;
		}
	}

	private void parseZprava() throws IOException {
		if (line.trim().equals("Pohyby na beznem uctu")) {
			zprava = zpravaBuilder.toString();
			nextLine();
			doubleSeparator();
			stav = Stav.HLAVICKA;
			return;
		}
		zpravaBuilder.append(line);
		zpravaBuilder.append("\r\n");
	}

	private void parseHlavicka() throws IOException {
		nextLine();
		nextLine();
		nextLine();
		doubleSeparator();
		stav = Stav.POHYBY;
		canEnd = true;
	}

	private void parsePohyby() throws IOException {
		if (isEmptyLine()) {
			stav = Stav.PATICKA;
			return;
		}
		canEnd = false;
		transakce = new BankovniTransakce();
		seznamTransakci.add(transakce);
		parsePohybyRadek1();
		nextLine();
		parsePohybyRadek2();
		nextLine();
		parsePohybyRadek3();
		nextLine();
		if (!isSingleSeparator()) {
			parsePohybyRadek4();
			nextLine();
		}
		singleSeparator();
		canEnd = true;
	}

	private void parsePohybyRadek1() {
		transakce.setIdTransakce(Long.valueOf(substring(0, 4)));
		transakce.setDatum(parseDayMonth(substring(5, 11)));
		transakce.setKomentar(substring(11, 33));
		transakce.setDatumOdepsano(parseDayMonth(substring(33, 39)));
		transakce.setSpecifickySymbol(substring(44, 54));
		transakce.setCastka(parseCastka(substring(56, 76)));
		transakce.setPoplatek(parseCastka(substring(77, 86)));
	}

	private void parsePohybyRadek2() {
		transakce.setDatumCas(LocalDateTime.of(transakce.getDatum(), LocalTime.parse(substring(5, 11), CAS_POHYBU_FORMATTER)));
		transakce.getBankovniUcet().setNazev(substring(11, 33));
		transakce.setVariabilniSymbol(substring(44, 54));
		transakce.setPoplatekSmena(parseCastka(substring(77, 86)));
	}

	private void parsePohybyRadek3() {
		transakce.getBankovniUcet().setCeleCislo(substring(11, 33));
		transakce.setKonstantniSymbol(substring(44, 54));
		transakce.setTyp(substring(55, 76));
		transakce.setPoplatekZprava(parseCastka(substring(77, 86)));
	}

	private void parsePohybyRadek4() {
		transakce.setKomentar(substring(11));
	}

	private boolean matches(Pattern pattern) {
		matcher = pattern.matcher(line);
		return matcher.matches();
	}

	private boolean isSingleSeparator() {
		return SINGLE_SEPARATOR.equals(line);
	}

	private boolean isDoubleSeparator() {
		return DOUBLE_SEPARATOR.equals(line);
	}

	private void singleSeparator() {
		if (!isSingleSeparator()) {
			formatException();
		}
	}

	private void doubleSeparator() {
		if (!isDoubleSeparator()) {
			formatException();
		}
	}

	private boolean isEmptyLine() {
		return line.isEmpty();
	}

	private String substring(int begin, int end) {
		if (line.length() <= begin) {
			return EMPTY_STRING;
		}
		if (end > line.length()) {
			return StringUtil.trimDown(line.substring(begin));
		}
		return StringUtil.trimDown(line.substring(begin, end));
	}

	private String substring(int begin) {
		if (line.length() <= begin) {
			return EMPTY_STRING;
		}
		return StringUtil.trimDown(line.substring(begin));
	}

	private void formatException() {
		throw new IllegalArgumentException(String.format("Neočekávaný text bankovního výpisu na řádku %d: %s", reader.getLineNumber(), line));
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	public BankovniUcet getBankovniUcet() {
		return bankovniUcet;
	}

	public int getCisloVypisu() {
		return cisloVypisu;
	}

	public LocalDate getObdobiVypisuOd() {
		return obdobiVypisuOd;
	}

	public LocalDate getObdobiVypisuDo() {
		return obdobiVypisuDo;
	}

	public BigDecimal getPocatecniZustatek() {
		return pocatecniZustatek;
	}

	public BigDecimal getKonecnyZustatek() {
		return konecnyZustatek;
	}

	public String getZprava() {
		return zprava.toString();
	}

	public List<BankovniTransakce> getTransakce() {
		return seznamTransakci;
	}

	private static BigDecimal parseCastka(String value) {
		if (StringUtil.isBlank(value)) {
			return null;
		}
		return new BigDecimal(StringUtil.remove(value, ' '));
	}

	private LocalDate parseDayMonth(String text) {
		return DATUM_POHYBU_FORMATTER.parse(text, temporal -> LocalDate.of(obdobiVypisuOd.getYear(), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH)));
	}

	private enum Stav {
		VYPIS,
		UCET,
		HLAVICKA_SOUHRNU,
		SOUHRN,
		ZPRAVA,
		HLAVICKA,
		POHYBY,
		PATICKA,
	}

}
