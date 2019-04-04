// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package admanager.axis.v201811.baserateservice;

import static com.google.api.ads.common.lib.utils.Builder.DEFAULT_CONFIGURATION_FILENAME;

import com.beust.jcommander.Parameter;
import com.google.api.ads.admanager.axis.factory.AdManagerServices;
import com.google.api.ads.admanager.axis.v201811.ApiError;
import com.google.api.ads.admanager.axis.v201811.ApiException;
import com.google.api.ads.admanager.axis.v201811.BaseRate;
import com.google.api.ads.admanager.axis.v201811.BaseRateServiceInterface;
import com.google.api.ads.admanager.axis.v201811.Money;
import com.google.api.ads.admanager.axis.v201811.ProductTemplateBaseRate;
import com.google.api.ads.admanager.lib.client.AdManagerSession;
import com.google.api.ads.admanager.lib.utils.examples.ArgumentNames;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.ads.common.lib.utils.examples.CodeSampleParams;
import com.google.api.client.auth.oauth2.Credential;
import java.rmi.RemoteException;

/**
 * This example creates a product template base rate. To determine which base rates exist, run
 * GetAllBaseRates.java.
 *
 * <p>Credentials and properties in {@code fromFile()} are pulled from the "ads.properties" file.
 * See README for more info.
 */
public class CreateProductTemplateBaseRates {

  private static class CreateProductTemplateBaseRatesParams extends CodeSampleParams {
    @Parameter(
        names = ArgumentNames.RATE_CARD_ID,
        required = true,
        description = "The rate card ID to add the base rate to.")
    private Long rateCardId;

    @Parameter(
        names = ArgumentNames.PRODUCT_TEMPLATE_ID,
        required = true,
        description = "The product template to apply this base rate to.")
    private Long productTemplateId;
  }

  /**
   * Runs the example.
   *
   * @param adManagerServices the services factory.
   * @param session the session.
   * @param rateCardId the rate card ID to add the base rate to.
   * @param productTemplateId the product template to apply this base rate to.
   * @throws ApiException if the API request failed with one or more service errors.
   * @throws RemoteException if the API request failed due to other errors.
   */
  public static void runExample(
      AdManagerServices adManagerServices,
      AdManagerSession session,
      long rateCardId,
      long productTemplateId)
      throws RemoteException {
    // Get the BaseRateService.
    BaseRateServiceInterface baseRateService =
        adManagerServices.get(session, BaseRateServiceInterface.class);

    // Create a base rate for a product template.
    ProductTemplateBaseRate productTemplateBaseRate = new ProductTemplateBaseRate();

    // Set the rate card ID that the product template base rate belongs to.
    productTemplateBaseRate.setRateCardId(rateCardId);

    // Set the product template the base rate will be applied to.
    productTemplateBaseRate.setProductTemplateId(productTemplateId);

    // Create a rate worth $2 and set that on the product template base rate.
    Money rate = new Money();
    rate.setCurrencyCode("USD");
    rate.setMicroAmount(2000000L);
    productTemplateBaseRate.setRate(rate);

    // Create the product template base rate on the server.
    BaseRate[] baseRates =
        baseRateService.createBaseRates(new BaseRate[] {productTemplateBaseRate});

    for (BaseRate createdBaseRate : baseRates) {
      System.out.printf(
          "A product template base rate with ID %d and rate %.4f %s " + "was created.%n",
          createdBaseRate.getId(),
          (((ProductTemplateBaseRate) createdBaseRate).getRate().getMicroAmount() / 1000000f),
          ((ProductTemplateBaseRate) createdBaseRate).getRate().getCurrencyCode());
    }
  }

  public static void main(String[] args) {
    AdManagerSession session;
    try {
      // Generate a refreshable OAuth2 credential.
      Credential oAuth2Credential =
          new OfflineCredentials.Builder()
              .forApi(Api.AD_MANAGER)
              .fromFile()
              .build()
              .generateCredential();

      // Construct a AdManagerSession.
      session =
          new AdManagerSession.Builder().fromFile().withOAuth2Credential(oAuth2Credential).build();
    } catch (ConfigurationLoadException cle) {
      System.err.printf(
          "Failed to load configuration from the %s file. Exception: %s%n",
          DEFAULT_CONFIGURATION_FILENAME, cle);
      return;
    } catch (ValidationException ve) {
      System.err.printf(
          "Invalid configuration in the %s file. Exception: %s%n",
          DEFAULT_CONFIGURATION_FILENAME, ve);
      return;
    } catch (OAuthException oe) {
      System.err.printf(
          "Failed to create OAuth credentials. Check OAuth settings in the %s file. "
              + "Exception: %s%n",
          DEFAULT_CONFIGURATION_FILENAME, oe);
      return;
    }

    AdManagerServices adManagerServices = new AdManagerServices();

    CreateProductTemplateBaseRatesParams params = new CreateProductTemplateBaseRatesParams();
    if (!params.parseArguments(args)) {
      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.rateCardId = Long.parseLong("INSERT_RATE_CARD_ID_HERE");
      params.productTemplateId = Long.parseLong("INSERT_PRODUCT_TEMPLATE_ID_HERE");
    }

    try {
      runExample(adManagerServices, session, params.rateCardId, params.productTemplateId);
    } catch (ApiException apiException) {
      // ApiException is the base class for most exceptions thrown by an API request. Instances
      // of this exception have a message and a collection of ApiErrors that indicate the
      // type and underlying cause of the exception. Every exception object in the admanager.axis
      // packages will return a meaningful value from toString
      //
      // ApiException extends RemoteException, so this catch block must appear before the
      // catch block for RemoteException.
      System.err.println("Request failed due to ApiException. Underlying ApiErrors:");
      if (apiException.getErrors() != null) {
        int i = 0;
        for (ApiError apiError : apiException.getErrors()) {
          System.err.printf("  Error %d: %s%n", i++, apiError);
        }
      }
    } catch (RemoteException re) {
      System.err.printf("Request failed unexpectedly due to RemoteException: %s%n", re);
    }
  }
}
