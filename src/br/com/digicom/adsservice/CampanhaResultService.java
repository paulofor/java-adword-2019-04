package br.com.digicom.adsservice;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.List;

import com.google.api.ads.adwords.axis.v201809.cm.ApiException;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.factory.AdWordsServicesInterface;
import com.google.api.ads.adwords.lib.jaxb.v201809.DownloadFormat;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;
import com.google.api.ads.adwords.lib.utils.v201809.ReportDownloaderInterface;
import com.google.common.base.Splitter;

import br.com.digicom.AdsService;
import br.com.digicom.modelo.CampanhaAds;

public class CampanhaResultService extends AdsService {
	
	private CampanhaAds campanhaResult = null;

	@Override
	protected void runExample(AdWordsServicesInterface adWordsServices, AdWordsSession session)
			throws RemoteException, ApiException {


		ReportingConfiguration reportingConfiguration = new ReportingConfiguration.Builder()
				.skipReportHeader(true).skipColumnHeader(true).skipReportSummary(true)
				.includeZeroImpressions(false).build();
		session.setReportingConfiguration(reportingConfiguration);

		ReportDownloaderInterface reportDownloader = adWordsServices.getUtility(session,ReportDownloaderInterface.class);

		
	    String query = "Select  Impressions , Clicks, Cost, CampaignStatus, EndDate, Ctr, AverageCpc , Conversions, ConversionRate, CostPerConversion "
	    		+ "FROM CAMPAIGN_PERFORMANCE_REPORT "
	    		+ "Where CampaignId = " + campanhaResult.getIdAds();
		BufferedReader reader = null;
		try {
			final ReportDownloadResponse response = reportDownloader.downloadReport(query, DownloadFormat.CSV);
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), UTF_8));
			String line;
			Splitter splitter = Splitter.on(',');
			while ((line = reader.readLine()) != null) {
				System.out.println("Linha:" + line);
				List<String> values = splitter.splitToList(line);
				Integer impressao = Integer.parseInt(values.get(0));
				Integer click = Integer.parseInt(values.get(1));
				Double custo = Double.parseDouble(values.get(2));
				custo = custo / 1000000;
				Double ctr = Double.parseDouble(values.get(5).replaceAll("%", ""));
				Double averageCpc = Double.parseDouble(values.get(6));
				averageCpc = averageCpc / 1000000;
				Double converions = Double.parseDouble(values.get(7));
				Double conversionRate = Double.parseDouble(values.get(8).replaceAll("%", ""));
				Double costPerConversion = Double.parseDouble(values.get(9));
				costPerConversion = costPerConversion / 1000000;
				campanhaResult.setOrcamentoTotalExecutado(custo);
				campanhaResult.setQuantidadeImpressao(impressao);
				campanhaResult.setQuantidadeClique(click);
				campanhaResult.setCtr(ctr);
				campanhaResult.setCpcMedio(averageCpc);
				campanhaResult.setConversao(converions);
				campanhaResult.setTaxaConversao(conversionRate);
				campanhaResult.setCustoConversao(costPerConversion);
			}

			} catch (ReportException e) {
			e.printStackTrace();
		} catch (ReportDownloadResponseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void atualizaResultado(CampanhaAds campanha) {
		this.campanhaResult = campanha;
		super.executa();
	}

}
