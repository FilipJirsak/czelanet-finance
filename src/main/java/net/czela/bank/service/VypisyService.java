package net.czela.bank.service;

import net.czela.bank.dto.VypisRaw;
import net.czela.bank.fio.FioXmlVypisParser;
import net.czela.bank.rb.RbTextVypisParser;
import net.czela.bank.repository.ParsovaneVypisyRepository;
import net.czela.bank.repository.UploadovaneVypisyRepository;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;

/**
 * Created by jirsakf on 25.4.2016.
 */
@Service
public class VypisyService {

	private final UploadovaneVypisyRepository uploadovaneVypisyRepository;
	private final ParsovaneVypisyRepository parsovaneVypisyRepository;

	@Autowired
	public VypisyService(UploadovaneVypisyRepository uploadovaneVypisyRepository, ParsovaneVypisyRepository parsovaneVypisyRepository) {
		this.uploadovaneVypisyRepository = uploadovaneVypisyRepository;
		this.parsovaneVypisyRepository = parsovaneVypisyRepository;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void zpracovatVypisy() throws IOException, DocumentException {
		List<VypisRaw> vypisy = nacistNezpracovaneVypisy();
		zpracovatVypisy(vypisy);
	}

	@Transactional
	public void zpracovatVypisy(int id) throws IOException, DocumentException {
		List<VypisRaw> vypisy = nacistVypisy(id);
		zpracovatVypisy(vypisy);
	}

	@Async
	@Transactional
	public void zpracovatPlatby() {
		parsovaneVypisyRepository.zpracovatPlatby();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void zpracovatVypisy(List<VypisRaw> vypisy) throws IOException, DocumentException {
		for (VypisRaw vypis : vypisy) {
			switch (vypis.getBanka()) {
				case FIO:
					zpracujVypisFIO(vypis);
					break;
				case RAIFFEISENBANK:
					zpracujVypisRB(vypis);
					break;
				default:
					throw new RuntimeException(String.format("Neznámý typ banky: %s", vypis.getBanka()));
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	private List<VypisRaw> nacistNezpracovaneVypisy() {
		return uploadovaneVypisyRepository.nacistNezpracovaneVypisy();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	private List<VypisRaw> nacistVypisy(int ids) {
		return uploadovaneVypisyRepository.nacistVypisy(ids);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void zpracujVypisRB(VypisRaw vypis) throws IOException {
		try (RbTextVypisParser parser = new RbTextVypisParser(vypis.getVypis())) {
			parser.read();
			parsovaneVypisyRepository.zapsatTransakce(parser.getTransakce(), vypis.getId());
			uploadovaneVypisyRepository.vypisZpracovan(vypis.getId());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void zpracujVypisFIO(VypisRaw vypis) throws IOException, DocumentException {
		try (FioXmlVypisParser parser = new FioXmlVypisParser(vypis.getVypis())) {
			parser.read();
			parsovaneVypisyRepository.zapsatTransakce(parser.getTransakce(), vypis.getId());
			uploadovaneVypisyRepository.vypisZpracovan(vypis.getId());
		}
	}
}
