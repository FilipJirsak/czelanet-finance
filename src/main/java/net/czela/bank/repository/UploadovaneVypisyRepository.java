package net.czela.bank.repository;

import net.czela.bank.dto.UploadovanyVypis;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * Created by jirsakf on 18.4.2016.
 */
@Repository
public class UploadovaneVypisyRepository {
	private final Logger logger = LoggerFactory.getLogger(UploadovaneVypisyRepository.class);

	private final NamedParameterJdbcTemplate jdbc;

	private final RowMapper<VypisRaw> vypisRawRowMapper = new BeanPropertyRowMapper<>(VypisRaw.class);

	@Autowired
	public UploadovaneVypisyRepository(DataSource dataSource) {
		this.jdbc = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<VypisRaw> nacistNezpracovaneVypisy() {
		return jdbc.query("SELECT id, vypis, banka_id FROM uploadovane_vypisy WHERE exportovano IS NULL", vypisRawRowMapper);
	}

	public List<VypisRaw> nacistVypisy(int ids) {
		return jdbc.query("SELECT id, vypis, banka_id FROM uploadovane_vypisy WHERE id IN (:id)", new MapSqlParameterSource("id", ids), vypisRawRowMapper);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void zapsatVypis(UploadovanyVypis vypis) {
		jdbc.update("INSERT INTO uploadovane_vypisy (vypis, cislo_vypisu, obdobi_od, obdobi_do, pocatecni_zustatek, koncovy_zustatek, banka_id) VALUES(:vypis, :cisloVypisu, :obdobiOd, :obdobiDo, :pocatecniZustatek, :konecnyZustatek, :banka.id)", new BeanPropertySqlParameterSource(vypis));
	}

	public void vypisZpracovan(int id) {
		SqlParameterSource params = new MapSqlParameterSource("id", id);
		jdbc.update("UPDATE uploadovane_vypisy SET exportovano = 1 WHERE id = :id", params);
	}
}
