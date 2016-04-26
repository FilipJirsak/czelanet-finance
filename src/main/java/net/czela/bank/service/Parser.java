package net.czela.bank.service;

import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.BankovniUcet;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by jirsakf on 25.4.2016.
 */
public interface Parser extends Closeable {

	void read() throws IOException;

	BankovniUcet getBankovniUcet();

	String getCisloVypisu();

	LocalDate getObdobiVypisuOd();

	LocalDate getObdobiVypisuDo();

	BigDecimal getPocatecniZustatek();

	BigDecimal getKonecnyZustatek();

	List<BankovniTransakce> getTransakce();
}
