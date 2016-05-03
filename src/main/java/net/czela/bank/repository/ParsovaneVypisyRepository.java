package net.czela.bank.repository;

import net.czela.bank.dto.BankovniTransakce;
import net.czela.bank.dto.VypisRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by jirsakf on 18.4.2016.
 */
@Repository
public class ParsovaneVypisyRepository {
	private final Logger logger = LoggerFactory.getLogger(ParsovaneVypisyRepository.class);

	private final NamedParameterJdbcTemplate jdbc;
	private final SimpleJdbcCall urcitTypyPlatebProcedure;

	@Autowired
	public ParsovaneVypisyRepository(DataSource dataSource) {
		this.jdbc = new NamedParameterJdbcTemplate(dataSource);
		this.urcitTypyPlatebProcedure = new SimpleJdbcCall(dataSource).withProcedureName("urcit_typy_plateb");
	}

	public void zapsatTransakce(List<BankovniTransakce> seznameTransakci, int vypisId) {
		SqlParameterSource[] params = new SqlParameterSource[seznameTransakci.size()];
		int i = 0;
		for (BankovniTransakce transakce : seznameTransakci) {
			MapSqlParameterSource param = new MapSqlParameterSource();
			param.addValue("idTransakce", transakce.getIdTransakce());
			param.addValue("datum", transakce.getDatum());
			if (transakce.getBankovniUcet() != null) {
				param.addValue("nazevProtiuctu", transakce.getBankovniUcet().getNazev());
				param.addValue("cisloProtiuctu", transakce.getBankovniUcet().getCeleCislo());
			} else {
				param.addValue("nazevProtiuctu", null);
				param.addValue("cisloProtiuctu", null);
			}
			param.addValue("vs", transakce.getVariabilniSymbol());
			param.addValue("ks", transakce.getKonstantniSymbol());
			param.addValue("ss", transakce.getSpecifickySymbol());
			param.addValue("castka", transakce.getCastka());
			param.addValue("poplatek", transakce.getPoplatek());
			param.addValue("poznamka", transakce.getKomentar());
			param.addValue("zprava", transakce.getZpravaProPrijemce());
			param.addValue("idVypisu", vypisId);
			params[i++] = param;
		}
		jdbc.batchUpdate("INSERT INTO parsovane_vypisy (porad_cislo, datum, nazev_protiuctu, cislo_protiuctu, vs, ks, ss, castka, poplatek, poznamka, zprava, id_vypisu)"
				+ " VALUES(:idTransakce, :datum, :nazevProtiuctu, :cisloProtiuctu, :vs, :ks, :ss, :castka, :poplatek, :poznamka, :zprava, :idVypisu)", params);
	}

	public void zpracovatPlatby() {
		jdbc.update("CALL urcit_typy_plateb()", (SqlParameterSource) null);
//		urcitTypyPlatebProcedure.execute();
	}
}
