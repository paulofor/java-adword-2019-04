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

public class GenericoService extends AdsService {
	
	

	@Override
	protected void runExample(AdWordsServicesInterface adWordsServices, AdWordsSession session)
			throws RemoteException, ApiException {


		ReportingConfiguration reportingConfiguration = new ReportingConfiguration.Builder()
				.skipReportHeader(true).skipColumnHeader(true).skipReportSummary(true)
				.includeZeroImpressions(false).build();
		session.setReportingConfiguration(reportingConfiguration);

		ReportDownloaderInterface reportDownloader = adWordsServices.getUtility(session,ReportDownloaderInterface.class);

		
	    //String query = "Select CampaignName, Date, CampaignId, Id, Cost, Headline, Ctr, Clicks "
	    //		+ "FROM AD_PERFORMANCE_REPORT ";
	    
	    String query = "Select Impressions , Clicks, Cost, Ctr, AverageCpc , Conversions, ConversionRate, CostPerConversion  "
	    		+ "FROM KEYWORDS_PERFORMANCE_REPORT where Id = 261010218 and  CampaignId = 1674137918";
		//String query = "Select Id, Labels, Criteria, Clicks, Cost, Impressions " +
		//		" FROM KEYWORDS_PERFORMANCE_REPORT where CampaignId = 1674137918";
		//query = "Select Query, KeywordTextMatchingQuery, QueryTargetingStatus, KeywordId " +
		//		" FROM SEARCH_QUERY_PERFORMANCE_REPORT where CampaignId = 1620493862";
		//query = "Select SearchQuery " +
		//		" FROM PAID_ORGANIC_QUERY_REPORT where CampaignId = 1620493862";
	    		
		BufferedReader reader = null;
		try {
			final ReportDownloadResponse response = reportDownloader.downloadReport(query, DownloadFormat.CSV);
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), UTF_8));
			String line;
			Splitter splitter = Splitter.on(',');
			int x = 0;
			while ((line = reader.readLine()) != null) {
				System.out.println((++x) + " = " + line);
				List<String> values = splitter.splitToList(line);

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
	
	public void executa() {
		super.executa();
	}

}
