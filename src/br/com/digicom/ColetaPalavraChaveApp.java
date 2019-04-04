package br.com.digicom;

import java.util.List;

import br.com.digicom.adsservice.PalavraChaveIdeiaService;
import br.com.digicom.modelo.PalavraChaveEstatistica;
import br.com.digicom.modelo.PalavraChaveRaiz;
import br.com.digicom.modelo.repositorio.RepositorioBase;
import br.com.digicom.modelo.util.Util;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

public class ColetaPalavraChaveApp {

	private static RestAdapter adapter = new RestAdapter("http://validacao.kinghost.net:21101/api");

	//private static RestAdapter adapter = new RestAdapter("http://localhost:21101/api");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Inicio Coleta PalavraChave");
		processa();
	}

	private static void processa() {
		RepositorioBase.PalavraChaveRaizRepository rep = adapter
				.createRepository(RepositorioBase.PalavraChaveRaizRepository.class);
		rep.listaParaConsulta(new ListCallback<PalavraChaveRaiz>() {
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void onSuccess(List<PalavraChaveRaiz> objects) {
				System.out.println("Lista pra resultado contendo " + objects.size() + " palavras raiz.");
				for (PalavraChaveRaiz item : objects) {
					processaPalavraChaveRaiz(item);
					try {
						Thread.sleep(1000* 60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private static void processaPalavraChaveRaiz(final PalavraChaveRaiz item) {
		System.out.println("Palavra: " + item.getPalavra());
		PalavraChaveIdeiaService servico = new PalavraChaveIdeiaService();
		servico.executaColeta(item);
		List<PalavraChaveEstatistica> lista = servico.getListaResultado();
		registraListaPalavra(lista, item);
		/*
		int primeiro = 0;
		int ultimo = primeiro + 99;
		while (primeiro < lista.size()) {
			System.out.println("de " + primeiro + " até " + ultimo);
			List<PalavraChaveEstatistica> sublista = lista.subList(primeiro, ultimo);
			registraListaPalavra(sublista);
			primeiro = ultimo + 1;
			ultimo = primeiro + 99;
			if (ultimo >= lista.size()) ultimo = lista.size() - 1;
		}
		*/
		item.setDataUltimaAtualizacao(Util.getDataAtualLoopback());
		item.save(new VoidCallback() {
			@Override
			public void onSuccess() {
				System.out.print("Atualizou palavra-chave " + item.getPalavra());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Falhou o save");
				t.printStackTrace();
				System.exit(-2);
			}

		});
	}

	private static void registraListaPalavra(final List<PalavraChaveEstatistica> lista, final PalavraChaveRaiz item) {
		RepositorioBase.PalavraChaveEstatisticaRepository rep = adapter
				.createRepository(RepositorioBase.PalavraChaveEstatisticaRepository.class);
			rep.insereLista(lista, new VoidCallback() {
				@Override
				public void onError(Throwable t) {
					System.out.println("Falhou o insereLista de " + item.getPalavra());
					t.printStackTrace();
					//System.exit(-1);
				}

				@Override
				public void onSuccess() {
					System.out.println("Lista de " + item.getPalavra() + " com com sucesso.");
				}
			});
	}


}
