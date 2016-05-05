package net.czela.bank.service;

import net.czela.bank.dto.Banka;
import net.czela.bank.dto.UploadovanyVypis;
import net.czela.bank.repository.UploadovaneVypisyRepository;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Created by jirsakf on 26.4.2016.
 */
public abstract class AbstractUploadService {

	private final Logger logger = LoggerFactory.getLogger(AbstractUploadService.class);

	private final Banka banka;
	private final UploadovaneVypisyRepository uploadovaneVypisyRepository;
	private final VypisyService vypisyService;

	public AbstractUploadService(Banka banka, UploadovaneVypisyRepository uploadovaneVypisyRepository, VypisyService vypisyService) {
		this.banka = banka;
		this.uploadovaneVypisyRepository = uploadovaneVypisyRepository;
		this.vypisyService = vypisyService;
	}

	@Transactional
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
			zpracovatVypisyyAPlatby();
		}
	}

	@Async
	protected void zpracovatVypisyyAPlatby() {
		try {
			vypisyService.zpracovatVypisy();
			vypisyService.zpracovatPlatby();
		} catch (IOException | DocumentException ex) {
			logger.error("Chyba při zpracování výpisů.", ex);
		}
	}

	protected abstract Parser createParser(String vypis) throws IOException, DocumentException;

}
