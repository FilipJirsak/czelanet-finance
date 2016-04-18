package net.czela.bank.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * Created by jirsakf on 18.4.2016.
 */
@Repository
public class UploadovaneVypisyRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public UploadovaneVypisyRepository(DataSource dataSource) {
		this.jdbc = new NamedParameterJdbcTemplate(dataSource);
	}
}
