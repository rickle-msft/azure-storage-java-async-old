/**
 * Copyright Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.azure.storage.blob;

import com.microsoft.azure.storage.implementation.StorageClientImpl;
import com.microsoft.rest.v2.http.HttpPipeline;
import com.microsoft.rest.v2.http.HttpRequest;
import com.microsoft.rest.v2.http.HttpResponse;
import com.microsoft.rest.v2.http.UrlBuilder;
import com.microsoft.rest.v2.policy.RequestPolicy;
import com.microsoft.rest.v2.policy.RequestPolicyFactory;
import com.microsoft.rest.v2.policy.RequestPolicyOptions;
import io.reactivex.Single;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import static com.microsoft.azure.storage.blob.Utility.getGMTTime;

public abstract class StorageURL {

    protected final StorageClientImpl storageClient;

    protected StorageURL(URL url, HttpPipeline pipeline) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null.");
        }
        if (pipeline == null) {
            throw new IllegalArgumentException("pipeline cannot be null.");
        }

        this.storageClient = new StorageClientImpl(pipeline).withVersion("2016-05-31");
        this.storageClient.withUrl(url.toString());
    }

    // TODO: ADD RETRY Factory
    public static HttpPipeline CreatePipeline(ICredentials credentials, PipelineOptions pipelineOptions) {
        LoggingFactory loggingFactory = new LoggingFactory(pipelineOptions.loggingOptions);
        RequestIDFactory requestIDFactory = new RequestIDFactory();
        //RequestRetryFactory requestRetryFactory = new RequestRetryFactory();
        TelemetryFactory telemetryFactory = new TelemetryFactory(pipelineOptions.telemetryOptions);
        AddDatePolicy addDate = new AddDatePolicy();
        return HttpPipeline.build(
                pipelineOptions.client, requestIDFactory, telemetryFactory, addDate, credentials, loggingFactory);
    }

    @Override
    public String toString() {
        return this.storageClient.url();
    }

    public URL toURL() {
        try {
            return new URL(this.storageClient.url());
        } catch (MalformedURLException e) {
            // TODO: remove and update toString.
        }
        return null;
    }

    /**
     * Appends a string to the end of a URL's path (prefixing the string with a '/' if required).
     * @param baseURL
     *      A {@code java.net.URL} to which the name should be appended.
     * @param name
     *      A {@code String} with the name to be appended.
     * @return
     *      A {@code String} with the name appended to the URL.
     */
    protected URL appendToURLPath(URL baseURL, String name) throws MalformedURLException {
        UrlBuilder url = UrlBuilder.parse(baseURL.toString());
        if(url.path() == null) {
            url.withPath("/"); // .path() will return null if it is empty, so we have to process separately from below.
        }
        else if (url.path().charAt(url.path().length() - 1) != '/') {
            url.withPath(url.path() + '/');
        }
        url.withPath(url.path() + name);
        return new URL(url.toString()); // TODO: modify when toURL is released.
    }

    static class AddDatePolicy implements RequestPolicyFactory {

        @Override
        public RequestPolicy create(RequestPolicy next, RequestPolicyOptions options) {
            return new AddDate(next);
        }

        public final class AddDate implements RequestPolicy {
            private final DateTimeFormatter format = DateTimeFormat
                    .forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                    .withZoneUTC()
                    .withLocale(Locale.US);

            private final RequestPolicy next;
            public AddDate(RequestPolicy next) {
                this.next = next;
            }

            @Override
            public Single<HttpResponse> sendAsync(HttpRequest request) {
                request.headers().set(Constants.HeaderConstants.DATE, getGMTTime(new Date()));
                return this.next.sendAsync(request);
            }
        }
    }
}
