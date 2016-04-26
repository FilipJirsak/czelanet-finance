package net.czela.bank.service;

import net.czela.bank.dto.Banka;
import net.czela.bank.dto.UploadovanyVypis;
import net.czela.bank.rb.RbTextVypisParser;
import net.czela.bank.repository.UploadovaneVypisyRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by jirsakf on 26.4.2016.
 */
@Service
public class RbUploadService extends AbstractUploadService {

	public RbUploadService(UploadovaneVypisyRepository uploadovaneVypisyRepository, VypisyService vypisyService) {
		super(Banka.RAIFFEISENBANK, uploadovaneVypisyRepository, vypisyService);
	}

	@Override
	protected Parser createParser(String vypis) {
		return new RbTextVypisParser(vypis);
	}
}
