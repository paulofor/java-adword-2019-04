package br.com.digicom.adsservice;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.ads.adwords.axis.v201809.cm.ApiException;
import com.google.api.ads.adwords.axis.v201809.cm.Language;
import com.google.api.ads.adwords.axis.v201809.cm.Money;
import com.google.api.ads.adwords.axis.v201809.cm.NetworkSetting;
import com.google.api.ads.adwords.axis.v201809.cm.Paging;
import com.google.api.ads.adwords.axis.v201809.o.Attribute;
import com.google.api.ads.adwords.axis.v201809.o.AttributeType;
import com.google.api.ads.adwords.axis.v201809.o.DoubleAttribute;
import com.google.api.ads.adwords.axis.v201809.o.IdeaType;
import com.google.api.ads.adwords.axis.v201809.o.IntegerSetAttribute;
import com.google.api.ads.adwords.axis.v201809.o.LanguageSearchParameter;
import com.google.api.ads.adwords.axis.v201809.o.LongAttribute;
import com.google.api.ads.adwords.axis.v201809.o.MoneyAttribute;
import com.google.api.ads.adwords.axis.v201809.o.NetworkSearchParameter;
import com.google.api.ads.adwords.axis.v201809.o.RelatedToQuerySearchParameter;
import com.google.api.ads.adwords.axis.v201809.o.RequestType;
import com.google.api.ads.adwords.axis.v201809.o.SearchParameter;
import com.google.api.ads.adwords.axis.v201809.o.StringAttribute;
import com.google.api.ads.adwords.axis.v201809.o.TargetingIdea;
import com.google.api.ads.adwords.axis.v201809.o.TargetingIdeaPage;
import com.google.api.ads.adwords.axis.v201809.o.TargetingIdeaSelector;
import com.google.api.ads.adwords.axis.v201809.o.TargetingIdeaServiceInterface;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.factory.AdWordsServicesInterface;
import com.google.api.ads.common.lib.utils.Maps;
import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

import br.com.digicom.AdsService;
import br.com.digicom.modelo.PalavraChaveEstatistica;
import br.com.digicom.modelo.PalavraChaveRaiz;
import br.com.digicom.modelo.util.Util;

public class PalavraChaveIdeiaService extends AdsService {

	private PalavraChaveRaiz palavraRaiz = null;
	private List<PalavraChaveEstatistica> listaResultado = null;

	@Override
	protected void runExample(AdWordsServicesInterface adWordsServices, AdWordsSession session) throws RemoteException,
			ApiException {
		// TODO Auto-generated method stub

		TargetingIdeaServiceInterface targetingIdeaService = adWordsServices.get(session,
				TargetingIdeaServiceInterface.class);

		// Create selector.
		TargetingIdeaSelector selector = new TargetingIdeaSelector();
		selector.setRequestType(RequestType.IDEAS);
		selector.setIdeaType(IdeaType.KEYWORD);
		selector.setRequestedAttributeTypes(new AttributeType[] { AttributeType.KEYWORD_TEXT,
				AttributeType.SEARCH_VOLUME, AttributeType.AVERAGE_CPC, AttributeType.COMPETITION,
				AttributeType.CATEGORY_PRODUCTS_AND_SERVICES });

		// Set selector paging (required for targeting idea service).
		Paging paging = new Paging();
		paging.setStartIndex(0);
		paging.setNumberResults(350);
		selector.setPaging(paging);

		List<SearchParameter> searchParameters = new ArrayList<>();
		String[] listaPalavra = new String[1];
		listaPalavra[0] = palavraRaiz.getPalavra();
		// Create related to query search parameter.
		RelatedToQuerySearchParameter relatedToQuerySearchParameter = new RelatedToQuerySearchParameter();
		// relatedToQuerySearchParameter.setQueries(new String[] { "mais vendas"
		// });
		relatedToQuerySearchParameter.setQueries(listaPalavra);
		searchParameters.add(relatedToQuerySearchParameter);

		// CompetitionSearchParameter indice = new CompetitionSearchParameter();
		// indice.setLevels(1, CompetitionSearchParameterLevel.LOW);
		// CompetitionSearchParameter competitionSearchParameter = new
		// CompetitionSearchParameter();
		// CompetitionSearchParameterLevel[] levels =
		// {CompetitionSearchParameterLevel.MEDIUM,CompetitionSearchParameterLevel.HIGH};
		// competitionSearchParameter.setLevels(levels);
		// searchParameters.add(competitionSearchParameter);

		// Language setting (optional).
		// The ID can be found in the documentation:
		// https://developers.google.com/adwords/api/docs/appendix/languagecodes
		// See the documentation for limits on the number of allowed language
		// parameters:
		// https://developers.google.com/adwords/api/docs/reference/latest/TargetingIdeaService.LanguageSearchParameter
		LanguageSearchParameter languageParameter = new LanguageSearchParameter();
		Language english = new Language();
		english.setId(1014L);
		languageParameter.setLanguages(new Language[] { english });
		searchParameters.add(languageParameter);

		// Create network search parameter (optional).
		NetworkSetting networkSetting = new NetworkSetting();
		networkSetting.setTargetGoogleSearch(true);
		networkSetting.setTargetSearchNetwork(false);
		networkSetting.setTargetContentNetwork(false);
		networkSetting.setTargetPartnerSearchNetwork(false);

		NetworkSearchParameter networkSearchParameter = new NetworkSearchParameter();
		networkSearchParameter.setNetworkSetting(networkSetting);
		searchParameters.add(networkSearchParameter);

		selector.setSearchParameters(searchParameters.toArray(new SearchParameter[searchParameters.size()]));

		// Get keyword ideas.
		TargetingIdeaPage page = targetingIdeaService.get(selector);

		listaResultado = new ArrayList<PalavraChaveEstatistica>();
		PalavraChaveEstatistica estat = null;
		// Display keyword ideas.
		int cont = 0;
		if (page.getEntries() != null) {
			for (TargetingIdea targetingIdea : page.getEntries()) {
				cont++;
				estat = new PalavraChaveEstatistica();
				Map<AttributeType, Attribute> data = Maps.toMap(targetingIdea.getData());
				StringAttribute keyword = (StringAttribute) data.get(AttributeType.KEYWORD_TEXT);

				IntegerSetAttribute categories = (IntegerSetAttribute) data
						.get(AttributeType.CATEGORY_PRODUCTS_AND_SERVICES);
				String categoriesString = "(none)";
				if (categories != null && categories.getValue() != null) {
					categoriesString = Joiner.on(", ").join(Ints.asList(categories.getValue()));
				}
				Long averageMonthlySearches = ((LongAttribute) data.get(AttributeType.SEARCH_VOLUME)).getValue();
				Money averageCpc = ((MoneyAttribute) data.get(AttributeType.AVERAGE_CPC)).getValue();
				Double competition = ((DoubleAttribute) data.get(AttributeType.COMPETITION)).getValue();

				estat.setDataConsulta(Util.getDataAtualLoopback());
				estat.setIndiceCompeticao(competition);
				double mediaCpc = (averageCpc != null ? averageCpc.getMicroAmount().doubleValue() / 1000000 : 0D);
				estat.setMediaCpc(mediaCpc);
				estat.setPalavraChaveGoogleId(keyword.getValue());
				estat.setVolumePesquisa(averageMonthlySearches);
				estat.setPalavraChaveRaizId(((Integer) this.palavraRaiz.getId()).longValue());

				/*
				 * System.out.printf(
				 * "%d Keyword with text '%s', average monthly search volume %d, "
				 * + "average CPC  %.4f, and competition %.2f " +
				 * "was found with categories: %s%n", cont, keyword.getValue(),
				 * averageMonthlySearches, mediaCpc , competition,
				 * categoriesString);
				 */

				System.out.println(estat);

				listaResultado.add(estat);
			}
		}

	}

	public void executaColeta(PalavraChaveRaiz palavraChave) {
		this.palavraRaiz = palavraChave;
		super.executa();
	}

	public List<PalavraChaveEstatistica> getListaResultado() {
		return listaResultado;
	}

}
