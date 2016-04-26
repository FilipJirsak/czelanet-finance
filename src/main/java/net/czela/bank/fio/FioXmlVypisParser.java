package net.czela.bank.fio;

import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.BankovniUcet;
import net.czela.bank.service.Parser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jirsakf on 25.4.2016.
 */
public class FioXmlVypisParser implements Parser, Closeable {
	private static final DateTimeFormatter DATUM_VYPISU_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-ddxxx");

	private final Document document;

	private BankovniUcet bankovniUcet = new BankovniUcet();
	private List<BankovniTransakce> seznamTransakci = new LinkedList<>();
	private String cisloVypisu;
	private LocalDate obdobiVypisuOd;
	private LocalDate obdobiVypisuDo;
	private BigDecimal pocatecniZustatek;
	private BigDecimal konecnyZustatek;

	public FioXmlVypisParser(String xml) throws DocumentException, IOException {
		try (Reader reader = new StringReader(xml)) {
			this.document = new SAXReader().read(reader);
		}
	}

	@Override
	public boolean read() {
		Element root = document.getRootElement();
		Element info = root.element("Info");
		bankovniUcet.setCislo(info.elementText("accountId"));
		bankovniUcet.setKodBanky(info.elementText("bankId"));
		pocatecniZustatek = new BigDecimal(info.elementText("openingBalance"));
		konecnyZustatek = new BigDecimal(info.elementText("closingBalance"));
		obdobiVypisuOd = LocalDate.parse(info.elementText("dateStart"), DATUM_VYPISU_FORMATTER);
		obdobiVypisuDo = LocalDate.parse(info.elementText("dateEnd"), DATUM_VYPISU_FORMATTER);
		if (info.element("idFrom") != null && info.element("idTo") != null) {
			cisloVypisu = String.format("%s-%s", info.elementText("idFrom"), info.elementText("idTo"));
		}

		Element transactionList = root.element("TransactionList");
		List<Element> transactions = transactionList.elements("Transaction");
		if (transactions.isEmpty())
			return false;
		for (Element transactionElement : transactions) {
			BankovniTransakce transakce = new BankovniTransakce();
			transakce.setIdTransakce(Long.parseLong(getValue(transactionElement, "ID pohybu")));
			transakce.setDatum(LocalDate.parse(getValue(transactionElement, "Datum"), DATUM_VYPISU_FORMATTER));
			transakce.setCastka(new BigDecimal(getValue(transactionElement, "Objem")));
			transakce.setMena(getValue(transactionElement, "Měna"));
			transakce.getBankovniUcet().setPredcisliCislo(getValue(transactionElement, "Protiúčet"));
			transakce.getBankovniUcet().setNazev(getValue(transactionElement, "Název protiúčtu"));
			transakce.getBankovniUcet().setKodBanky(getValue(transactionElement, "Kód banky"));
			transakce.getBankovniUcet().setNazevBanky(getValue(transactionElement, "Název banky"));
			transakce.setKonstantniSymbol(getValue(transactionElement, "KS"));
			transakce.setVariabilniSymbol(getValue(transactionElement, "VS"));
			transakce.setSpecifickySymbol(getValue(transactionElement, "SS"));
			transakce.setUzivatelskaIdentifikace(getValue(transactionElement, "Uživatelská identifikace"));
			transakce.setZpravaProPrijemce(getValue(transactionElement, "Zpráva pro příjemce"));
			transakce.setTyp(getValue(transactionElement, "Typ"));
			transakce.setKomentar(getValue(transactionElement, "Komentář"));
			transakce.setIdPokynu(getValue(transactionElement, "ID pokynu"));
			seznamTransakci.add(transakce);
		}
		return true;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public BankovniUcet getBankovniUcet() {
		return bankovniUcet;
	}

	@Override
	public String getCisloVypisu() {
		return cisloVypisu;
	}

	@Override
	public LocalDate getObdobiVypisuOd() {
		return obdobiVypisuOd;
	}

	@Override
	public LocalDate getObdobiVypisuDo() {
		return obdobiVypisuDo;
	}

	@Override
	public BigDecimal getPocatecniZustatek() {
		return pocatecniZustatek;
	}

	@Override
	public BigDecimal getKonecnyZustatek() {
		return konecnyZustatek;
	}

	@Override
	public List<BankovniTransakce> getTransakce() {
		return seznamTransakci;
	}
	
	private static String getValue(Element element, String name) {
		Node node = element.selectSingleNode("*[@name='"+name+"']");
		if (node != null) {
			return node.getText();
		}
		return null;
	}
}
