package br.com.digicom;

import java.util.List;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;

import br.com.digicom.adsservice.AnuncioResultService;
import br.com.digicom.adsservice.CampanhaResultService;
import br.com.digicom.adsservice.PalavraChaveResultService;
import br.com.digicom.modelo.CampanhaAds;
import br.com.digicom.modelo.CampanhaAnuncioResultado;
import br.com.digicom.modelo.CampanhaPalavraChaveResultado;
import br.com.digicom.modelo.repositorio.RepositorioBase;
import br.com.digicom.modelo.util.Util;

public class ObtemResultadoCampanha {
	
	private static RestAdapter adapter =  new RestAdapter("http://validacao.kinghost.net:21101/api");

	public static void main(String[] args) {
		processa();
	}
	
	private static void processa() {
		System.out.println("Ola Mundo");
		RepositorioBase.CampanhaAdRepository rep = adapter.createRepository(RepositorioBase.CampanhaAdRepository.class);
		rep.listaParaResultado(new ListCallback<CampanhaAds>() { 
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
			@Override
			public void onSuccess(List<CampanhaAds> objects) {
				System.out.println("Lista pra resultado contendo " + objects.size() + " campanhas.");
				for (CampanhaAds item : objects) {
					processaAnuncios(item);
					processaCampanha(item);
					processaPalavraChave(item);
				}
			} 
        });
	}
	
	private static void processaCampanha(CampanhaAds campanha) {
		System.out.println("Atualizar campanha " + campanha.getNome());
		CampanhaResultService srv = new CampanhaResultService();
		srv.atualizaResultado(campanha);
		campanha.setDataResultado(Util.getDataAtualLoopback());
		
		IntegracaoMundo facade = new IntegracaoMundo();
		facade.atualizaCampanha(campanha);
		
		
	}
	
	private static void processaAnuncios(CampanhaAds campanha) {
		RepositorioBase.CampanhaAnuncioResultadoRepository rep = adapter.createRepository(RepositorioBase.CampanhaAnuncioResultadoRepository.class);
		rep.listaParaResultadoPorIdCampanha((Integer) campanha.getId(), new ListCallback<CampanhaAnuncioResultado>() { 
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
			@Override
			public void onSuccess(List<CampanhaAnuncioResultado> objects) {
				System.out.println("Lista pra resultado contendo " + objects.size() + " anuncios.");
				for (CampanhaAnuncioResultado item : objects) {
					processaAnuncio(item);
				}
			}
			
        });
	}
	
	private static void processaPalavraChave(final CampanhaAds campanha) {
		RepositorioBase.CampanhaPalavraChaveResultadoRepository rep = adapter.createRepository(RepositorioBase.CampanhaPalavraChaveResultadoRepository.class);
		rep.listaParaResultadoPorIdCampanha((Integer) campanha.getId(), new ListCallback<CampanhaPalavraChaveResultado>() { 
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
			@Override
			public void onSuccess(List<CampanhaPalavraChaveResultado> objects) {
				System.out.println("Lista pra resultado contendo " + objects.size() + " palavras chaves.");
				for (CampanhaPalavraChaveResultado item : objects) {
					processaPalavraChave(item, campanha);
				}
			}
			
        });
	}

	
	private static void processaAnuncio(CampanhaAnuncioResultado item) {
		System.out.println("Atualizar anuncio " + item.getIdAds());
		AnuncioResultService srv = new AnuncioResultService();
		srv.atualizaResultado(item);
		

		IntegracaoMundo facade = new IntegracaoMundo();
		facade.atualizaAnuncio(item);
	} 
	
	private static void processaPalavraChave(CampanhaPalavraChaveResultado item, CampanhaAds campanha) {
		System.out.println("Atualizar palavra-chave " + item.getIdAds());
		PalavraChaveResultService srv = new PalavraChaveResultService();
		srv.atualizaResultado(item, campanha);
		

		IntegracaoMundo facade = new IntegracaoMundo();
		facade.atualizaPalavraChave(item);
	} 
}
