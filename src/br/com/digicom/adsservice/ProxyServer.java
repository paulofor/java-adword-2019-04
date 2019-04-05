package br.com.digicom.adsservice;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Authenticator.RequestorType;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class ProxyServer {
	public static HttpTransport setProxy() {
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

}
