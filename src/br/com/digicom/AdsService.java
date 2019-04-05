package br.com.digicom;

import static com.google.api.ads.common.lib.utils.Builder.DEFAULT_CONFIGURATION_FILENAME;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.rmi.RemoteException;

import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.v201809.cm.ApiError;
import com.google.api.ads.adwords.axis.v201809.cm.ApiException;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.factory.AdWordsServicesInterface;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public abstract class AdsService {
	
	
	
	protected HttpTransport setProxy() {
		System.setProperty("https.proxyHost", "10.21.7.10");
		System.setProperty("https.proxyPort", "82");
		System.setProperty("https.proxyUser", "tr626987");
		System.setProperty("https.proxyPassword", "Jenlop01");

		System.setProperty("http.proxyHost", "10.21.7.10");
		System.setProperty("http.proxyPort", "82");
		System.setProperty("http.proxyUser", "tr626987");
		System.setProperty("http.proxyPassword", "Jenlop01");
		
		
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.21.7.10", 82));
		HttpTransport httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// check that the pasword-requesting site is the proxy server
				if (this.getRequestingHost().contains("10.21.7.10") && this.getRequestingPort() == 82
						&& this.getRequestorType().equals(RequestorType.PROXY)) {
					return new PasswordAuthentication("tr626987", "Jenlop01".toCharArray());
				}
				return super.getPasswordAuthentication();
			}
		});
		return httpTransport;
	}

	protected void executa() {
		AdWordsSession session;
		try {
			setProxy();
			// Generate a refreshable OAuth2 credential.
			Credential oAuth2Credential = new OfflineCredentials.Builder()
					.forApi(Api.ADWORDS).fromFile().build()
					.generateCredential();

			// Construct an AdWordsSession.
			session = new AdWordsSession.Builder().fromFile()
					.withOAuth2Credential(oAuth2Credential).build();
		} catch (ConfigurationLoadException cle) {
			System.err.printf("Failed to load configuration from the %s file. Exception: %s%n",	DEFAULT_CONFIGURATION_FILENAME, cle);
			return;
		} catch (ValidationException ve) {
			System.err.printf("Invalid configuration in the %s file. Exception: %s%n", DEFAULT_CONFIGURATION_FILENAME, ve);
			return;
		} catch (OAuthException oe) {
			System.err.printf("Failed to create OAuth credentials. Check OAuth settings in the %s file. Exception: %s%n", DEFAULT_CONFIGURATION_FILENAME, oe);
			oe.printStackTrace();
			return;
		}

		AdWordsServicesInterface adWordsServices = AdWordsServices.getInstance();

		try {
			runExample(adWordsServices, session);
		} catch (ApiException apiException) {

			apiException.printStackTrace();
			//System.err.println("Request failed due to ApiException. Underlying ApiErrors:");
			if (apiException.getErrors() != null) {
				int i = 0;
				for (ApiError apiError : apiException.getErrors()) {
					System.err.printf("  Error %d: %s%n", i++, apiError);
				}
			}
			System.exit(1);
		} catch (RemoteException re) {
			System.err.printf("Request failed unexpectedly due to RemoteException: %s%n",re);
		}
	}

	protected abstract void runExample(AdWordsServicesInterface adWordsServices, AdWordsSession session) throws RemoteException, ApiException;
	
}
