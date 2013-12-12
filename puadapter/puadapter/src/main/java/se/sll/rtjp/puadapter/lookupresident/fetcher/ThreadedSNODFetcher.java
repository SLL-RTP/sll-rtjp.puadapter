/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package se.sll.rtjp.puadapter.lookupresident.fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.tree.DefaultElement;

import riv.population.residentmaster.v1.ResidentType;
import se.sll.rtjp.puadapter.lookupresident.transform.PKNODPLUSToResidentTypeTransformer;

/**
 * Handles fetching resident information from PU via SNOD in a threaded manner
 * in order to optimize performance.
 */
public class ThreadedSNODFetcher {
    
    /** The base URL to use for calling SNOD. */
    private String baseURL;
    /** The HTTP client to use for calling SNOD. */
    private HttpClient httpClient;
    
    public ThreadedSNODFetcher(String baseURL, String username, String password) {
        this.baseURL = baseURL;
        
        MultiThreadedHttpConnectionManager connectionManager = 
                new MultiThreadedHttpConnectionManager();
        httpClient = new HttpClient(connectionManager);
        httpClient.getParams().setAuthenticationPreemptive(true);
        Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
        httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
    }

    /**
     * Fetches information for all the personId in the incoming wsRequest.
     * 
     * @param wsRequest a {@link DefaultElement} object corresponding to the
     * LookupResidentForFullProfile tag from the request.
     * @return a list of transformed {@link ResidentType} objects.
     */
    public List<ResidentType> fetchAllFromRequest(DefaultElement wsRequest) {
        ExecutorService executor = Executors.newFixedThreadPool(15);
        List<Future<ResidentType>> list = new ArrayList<Future<ResidentType>>();
        for (Object currObj : wsRequest.content()) {
            if (currObj instanceof DefaultElement) {
                DefaultElement currentElement = (DefaultElement) currObj;
                if (!currentElement.getTextTrim().equals("")) {
                    Callable<ResidentType> worker = new SNODFetcherWorker(currentElement.getText(), baseURL, httpClient);
                    Future<ResidentType> submit = executor.submit(worker);
                    list.add(submit);
                }
            }
        }

        // now retrieve the result
        List<ResidentType> response = new ArrayList<ResidentType>();
        for (Future<ResidentType> future : list) {
            try {
                response.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * Callable implementation that fetches information for a Resident
     * from PU via SNOD and transforms the response into a {@link ResidentType}.
     */
    private class SNODFetcherWorker implements Callable<ResidentType> {
        /** The patient ID to use as the parameter. */
        private String pnr;
        private String baseURL;
        private HttpClient httpClient;

        public SNODFetcherWorker(String pnr, String baseURL, HttpClient httpClient) {
            this.pnr = pnr;
            this.baseURL = baseURL;
            this.httpClient = httpClient;
        }

        @Override
        public ResidentType call() throws Exception {

            GetMethod httpget = new GetMethod(this.baseURL + "?arg=" + pnr);
            ResidentType test = null;
            try { 
                httpClient.executeMethod(httpget);
                test = (ResidentType) (new PKNODPLUSToResidentTypeTransformer()).transform(httpget.getResponseBodyAsStream());
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpget.releaseConnection();
            }
            return test;
        }
    }
}
