package br.com.digicom;

import java.util.List;

import br.com.digicom.modelo.CampanhaAds;
import br.com.digicom.modelo.repositorio.RepositorioBase;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.Repository;

public class CriaCampanhas {

	private static RestAdapter adapter;
	private static Repository testClass;

	public static void main(String[] args) {
		System.out.println("Ola Mundo");
		RestAdapter adapter = new RestAdapter("http://validacao.kinghost.net:21101/api");
		RepositorioBase.CampanhaAdRepository rep = adapter.createRepository(RepositorioBase.CampanhaAdRepository.class);
		
		
		rep.listaPendente(new ListCallback<CampanhaAds>() { 
            
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
			@Override
			public void onSuccess(List<CampanhaAds> objects) {
				
				IntegracaoMundo integra = new IntegracaoMundo();
				integra.criaCampanha(objects);
				
			} 
        });
                
		
	}


}
