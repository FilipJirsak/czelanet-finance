package net.czela.bank.service;

import net.czela.bank.dto.Banka;
import net.czela.bank.dto.UploadovanyVypis;
import net.czela.bank.fio.FioAPIFormatTransakci;
import net.czela.bank.fio.FioAPIKlient;
import net.czela.bank.fio.FioXmlVypisParser;
import net.czela.bank.repository.UploadovaneVypisyRepository;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jirsakf on 26.4.2016.
 */
@Service
public class FioService extends AbstractUploadService {

	private final FioAPIKlient fioAPIKlient;

	@Autowired
	public FioService(FioAPIKlient fioAPIKlient, UploadovaneVypisyRepository uploadovaneVypisyRepository, VypisyService vypisyService) {
		super(Banka.FIO, uploadovaneVypisyRepository, vypisyService);
		this.fioAPIKlient = fioAPIKlient;
	}

	@Scheduled(fixedDelay = 3_600_000L) //1× za hodinu
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void nacistNovePlatby() throws IOException, DocumentException {
		String vypis = fioAPIKlient.nacistPohybyOdMinule(FioAPIFormatTransakci.XML);
		zapsatVypis(vypis);
	}


	@Override
	protected Parser createParser(String vypis) throws IOException, DocumentException {
		return new FioXmlVypisParser(vypis);
	}
}
