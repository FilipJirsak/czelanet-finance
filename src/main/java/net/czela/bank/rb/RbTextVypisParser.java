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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jirsakf on 18.4.2016.
 */
public class RbTextVypisParser implements Closeable {

	private static final Pattern RE_VYPIS_C = Pattern.compile("Bankovni vypis c. (\\d+)");
	private static final Pattern RE_VYPIS_OBDOBI = Pattern.compile("za (\\d{2}\\.\\d{2}\\.\\d{4})");

	private static final int LINE_LENGTH = 86;
	private static final String SINGLE_SEPARATOR = StringUtil.repeat('-', LINE_LENGTH);
	private static final String DOUBLE_SEPARATOR = StringUtil.repeat('=', LINE_LENGTH);

	private static final DateTimeFormatter DATUM_VYPISU_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATUM_POHYBU_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.");

	private final Logger logger = LoggerFactory.getLogger(RbTextVypisParser.class);

	private final LineNumberReader reader;
	private Stav stav = Stav.VYPIS;
	private boolean canEnd = false;
	private String line;
	private Matcher matcher;

	private BankovniUcet bankovniUcet = new BankovniUcet();
	private int cisloVypisu;
	private LocalDate datumVypisu;
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
			bankovniUcet.setNazevBanky(line);
		} else if (matches(RE_VYPIS_C)) {
			cisloVypisu = Integer.parseInt(matcher.group(1));
		} else if (matches(RE_VYPIS_OBDOBI)) {
			datumVypisu = LocalDate.parse(matcher.group(1), DATUM_VYPISU_FORMATTER);
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
		String nazev = line.substring(0, 11).trim();
		String hodnota = line.substring(12);
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
		String nazev = line.substring(0, 46).trim();
		String hodnota1 = line.substring(46, 61).trim();
		String hodnota2 = line.substring(72).trim();
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
		canEnd = false;
		transakce = new BankovniTransakce();
		seznamTransakci.add(transakce);
		parsePohybyRadek1();
		nextLine();
		parsePohybyRadek2();
		nextLine();
		parsePohybyRadek3();
		nextLine();
		singleSeparator();
		canEnd = true;
	}

	private void parsePohybyRadek1() {
//		transakce.setPoradoveCislo(Integer.valueOf(line.substring(0, 4).trim()));
		transakce.setDatum(parseDayMonth(line.substring(5, 11)));
		transakce.setKomentar(line.substring(11, 33).trim());
//		transakce.setOdepsano(parseDayMonth(line.substring(33, 39)));
		transakce.setSpecifickySymbol(line.substring(44, 54).trim());
		transakce.setCastka(parseCastka(line.substring(56, 76).trim()));
		transakce.setPoplatek(parseCastka(line.substring(77, 86).trim()));
	}

	private void parsePohybyRadek2() {
//		transakce.setCas(LocalTime.parse(line.substring(5, 11), DATUM_POHYBU_FORMATTER));
		transakce.getBankovniUcet().setNazev(line.substring(11, 33).trim());
		transakce.setVariabilniSymbol(line.substring(44, 54).trim());
//		transakce.setSmena(parseCastka(line.substring(77, 86).trim()));
	}

	private void parsePohybyRadek3() {
		transakce.getBankovniUcet().setCeleCislo(line.substring(11, 33).trim());
		transakce.setKomentar(line.substring(44, 54).trim());
		transakce.setZpravaProPrijemce(line.substring(77, 86).trim());
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

	public LocalDate getDatumVypisu() {
		return datumVypisu;
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
		return DATUM_POHYBU_FORMATTER.parse(text, temporal -> LocalDate.of(datumVypisu.getYear(), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH)));
	}

	private enum Stav {
		VYPIS,
		UCET,
		HLAVICKA_SOUHRNU,
		SOUHRN,
		ZPRAVA,
		HLAVICKA,
		POHYBY
	}

}
