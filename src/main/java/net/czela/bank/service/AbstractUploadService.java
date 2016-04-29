package net.czela.bank.service;

import net.czela.bank.dto.Banka;
import net.czela.bank.dto.UploadovanyVypis;
import net.czela.bank.rb.RbTextVypisParser;
import net.czela.bank.repository.UploadovaneVypisyRepository;
import org.dom4j.DocumentException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Created by jirsakf on 26.4.2016.
 */
public abstract class AbstractUploadService {
	private final Banka banka;
	private final UploadovaneVypisyRepository uploadovaneVypisyRepository;
	private final VypisyService vypisyService;

	public AbstractUploadService(Banka banka, UploadovaneVypisyRepository uploadovaneVypisyRepository, VypisyService vypisyService) {
		this.banka = banka;
		this.uploadovaneVypisyRepository = uploadovaneVypisyRepository;
		this.vypisyService = vypisyService;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void zapsatVypis(String vypis) throws IOException, DocumentException {
		try (Parser parser = createParser(vypis)) {
			boolean notEmpty = parser.read();

			if (!notEmpty) {
				return;
			}

			UploadovanyVypis uploadovanyVypis = new UploadovanyVypis();
			uploadovanyVypis.setBanka(banka);
			uploadovanyVypis.setVypis(vypis);
			uploadovanyVypis.setCisloVypisu(parser.getCisloVypisu());
			uploadovanyVypis.setObdobiOd(parser.getObdobiVypisuOd());
			uploadovanyVypis.setObdobiDo(parser.getObdobiVypisuDo());
			uploadovanyVypis.setPocatecniZustatek(parser.getPocatecniZustatek());
			uploadovanyVypis.setKonecnyZustatek(parser.getKonecnyZustatek());

			uploadovaneVypisyRepository.zapsatVypis(uploadovanyVypis);

			vypisyService.zpracovatVypisy();
			vypisyService.zpracovatPlatby();
		}
	}

	protected abstract Parser createParser(String vypis) throws IOException, DocumentException;

}
