package net.czela.bank;

import net.czela.bank.fio.FioAPIFormatTransakci;
import net.czela.bank.fio.FioAPIKlient;
import net.czela.bank.repository.UploadovaneVypisyRepository;
import net.czela.bank.service.VypisyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Created by jirsakf on 25.4.2016.
 */
@Component
public class Temp implements CommandLineRunner{
	private final VypisyService vypisyService;
	private final FioAPIKlient fioAPIKlient;

	@Autowired
	public Temp(VypisyService vypisyService, FioAPIKlient fioAPIKlient) {
		this.vypisyService = vypisyService;
		this.fioAPIKlient = fioAPIKlient;
	}


	@Override
	public void run(String... strings) throws Exception {
//		System.out.println(fioAPIKlient.nacistPohybyZaObdobi(LocalDate.of(2016, 4, 21), FioAPIFormatTransakci.XML));
//		vypisyService.zpracovatVypisy(3136);
//		System.out.println(fioAPIKlient.nacistPohybyZaObdobi(LocalDate.of(2016, 4, 22), FioAPIFormatTransakci.XML));
//		vypisyService.zpracovatVypisy(3137);
//		System.out.println(fioAPIKlient.nacistPohybyZaObdobi(LocalDate.of(2016, 4, 23), FioAPIFormatTransakci.XML));
//		vypisyService.zpracovatVypisy(3139);
//		System.out.println(fioAPIKlient.nacistPohybyZaObdobi(LocalDate.of(2016, 4, 24), FioAPIFormatTransakci.XML));
//		vypisyService.zpracovatVypisy(3140);
//		fioAPIKlient.nastavitZarazku(9289753378L);
//		System.out.println(fioAPIKlient.nacistPohybyOdMinule(FioAPIFormatTransakci.XML));
		vypisyService.zpracovatVypisy(3141);
	}
}
