package br.com.digicom;

import java.util.List;

import br.com.digicom.modelo.CampanhaAds;
import br.com.digicom.modelo.repositorio.RepositorioBase;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

public class EstatisticaResultadoCampanha {

	
	private static RestAdapter adapter =  new RestAdapter("http://validacao.kinghost.net:21101/api");

	/**
	 * @param args
	 */
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
					processaEstatisticaCampanha(item);
					try {
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
			 
        });
	}
	
	private static void processaEstatisticaCampanha(final CampanhaAds item) {
		// TODO Auto-generated method stub
		RepositorioBase.CampanhaAdRepository rep = adapter.createRepository(RepositorioBase.CampanhaAdRepository.class);
		rep.calculaResultados((int)item.getId(), new VoidCallback() {
			@Override
			public void onError(Throwable t) {
				System.out.println("Falhou o insereLista de " + item.getId());
				t.printStackTrace();
				System.exit(-1);
			}

			@Override
			public void onSuccess() {
				System.out.println("Sucesso de campanh " + item.getId() + " com sucesso.");
				
			}
		});
	}
}
