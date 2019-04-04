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

package admanager.axis.v201902.reconciliationreportrowservice;

import static com.google.api.ads.common.lib.utils.Builder.DEFAULT_CONFIGURATION_FILENAME;

import com.beust.jcommander.Parameter;
import com.google.api.ads.admanager.axis.factory.AdManagerServices;
import com.google.api.ads.admanager.axis.utils.v201902.StatementBuilder;
import com.google.api.ads.admanager.axis.v201902.ApiError;
import com.google.api.ads.admanager.axis.v201902.ApiException;
import com.google.api.ads.admanager.axis.v201902.BillFrom;
import com.google.api.ads.admanager.axis.v201902.ReconciliationReportRow;
import com.google.api.ads.admanager.axis.v201902.ReconciliationReportRowPage;
import com.google.api.ads.admanager.axis.v201902.ReconciliationReportRowServiceInterface;
import com.google.api.ads.admanager.lib.client.AdManagerSession;
import com.google.api.ads.admanager.lib.utils.examples.ArgumentNames;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.ads.common.lib.utils.examples.CodeSampleParams;
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.collect.Iterables;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * This example updates a reconciliation report row.
 *
 * <p>To get reconciliation report rows for a reconciliation report, run
 * GetReconciliationReportRowsForReconciliationReport.java.
 *
 * <p>Credentials and properties in {@code fromFile()} are pulled from the "ads.properties" file.
 * See README for more info.
 */
public class UpdateReconciliationReportRows {

  private static class UpdateReconciliationReportRowsParams extends CodeSampleParams {
    @Parameter(
        names = ArgumentNames.RECONCILIATION_REPORT_ID,
        required = true,
        description = "The ID of the reconciliation report to retrieve rows for.")
    private Long reconciliationReportId;

    @Parameter(
        names = ArgumentNames.RECONCILIATION_REPORT_ROW_ID,
        required = true,
        description = "The ID of the reconciliation report row to retrieve.")
    private Long reconciliationReportRowId;
  }

  /**
   * Runs the example.
   *
   * @param adManagerServices the services factory.
   * @param session the session.
   * @param reconciliationReportId the ID of the reconciliation report to retrieve rows for.
   * @param reconciliationReportRowId the ID of the reconciliation report row to retrieve.
   * @throws ApiException if the API request failed with one or more service errors.
   * @throws RemoteException if the API request failed due to other errors.
   */
  public static void runExample(
      AdManagerServices adManagerServices,
      AdManagerSession session,
      long reconciliationReportId,
      long reconciliationReportRowId)
      throws RemoteException {
    // Get the ReconciliationReportRowService.
    ReconciliationReportRowServiceInterface reconciliationReportRowService =
        adManagerServices.get(session, ReconciliationReportRowServiceInterface.class);

    // Create a statement to select reconciliation report rows.
    StatementBuilder statementBuilder =
        new StatementBuilder()
            .where(
                "id = :reconciliationReportRowId AND "
                    + "reconciliationReportId = :reconciliationReportId")
            .orderBy("id ASC")
            .limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
            .withBindVariableValue("reconciliationReportRowId", reconciliationReportRowId)
            .withBindVariableValue("reconciliationReportId", reconciliationReportId);

    // Get reconciliation report rows by statement.
    ReconciliationReportRowPage page =
        reconciliationReportRowService.getReconciliationReportRowsByStatement(
            statementBuilder.toStatement());

    ReconciliationReportRow row = Iterables.getOnlyElement(Arrays.asList(page.getResults()));

    // Set a comment on the reconciliation report row.
    row.setComments(
        "Third party volume didn't match Ad Manager. " + "We agreed to split the difference.");

    // Set and use a manual volume for billing.
    row.setManualVolume((row.getDfpVolume() + row.getThirdPartyVolume()) / 2);
    row.setReconciliationSource(BillFrom.MANUAL);

    ReconciliationReportRow[] updatedRows =
        reconciliationReportRowService.updateReconciliationReportRows(
            new ReconciliationReportRow[] {row});

    for (ReconciliationReportRow updatedRow : updatedRows) {
      System.out.printf(
          "Reconciliation report row for line item ID %d and creative ID %d was "
              + "updated, with manual volume %d.%n",
          updatedRow.getLineItemId(), updatedRow.getCreativeId(), updatedRow.getManualVolume());
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

    UpdateReconciliationReportRowsParams params = new UpdateReconciliationReportRowsParams();
    if (!params.parseArguments(args)) {
      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.reconciliationReportId = Long.parseLong("INSERT_RECONCILIATION_REPORT_ID_HERE");
      params.reconciliationReportRowId = Long.parseLong("INSERT_RECONCILIATION_REPORT_ROW_ID_HERE");
    }

    try {
      runExample(
          adManagerServices,
          session,
          params.reconciliationReportId,
          params.reconciliationReportRowId);
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