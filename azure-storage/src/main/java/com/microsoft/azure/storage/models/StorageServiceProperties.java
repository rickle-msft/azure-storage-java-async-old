/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 * Changes may cause incorrect behavior and will be lost if the code is
 * regenerated.
 */

package com.microsoft.azure.storage.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

/**
 * Storage Service Properties.
 */
@JacksonXmlRootElement(localName = "StorageServiceProperties")
public class StorageServiceProperties {
    /**
     * Azure Analytics Logging settings.
     */
    @JsonProperty(value = "Logging", required = true)
    private Logging logging;

    /**
     * A summary of request statistics grouped by API in hourly aggregates for
     * blobs.
     */
    @JsonProperty(value = "HourMetrics", required = true)
    private Metrics hourMetrics;

    /**
     * a summary of request statistics grouped by API in minute aggregates for
     * blobs.
     */
    @JsonProperty(value = "MinuteMetrics", required = true)
    private Metrics minuteMetrics;

    /**
     * The set of CORS rules.
     */
    @JacksonXmlElementWrapper(localName = "Cors")
    private List<CorsRule> cors;

    /**
     * The default version to use for requests to the Blob service if an
     * incoming request's version is not specified. Possible values include
     * version 2008-10-27 and all more recent versions.
     */
    @JsonProperty(value = "DefaultServiceVersion", required = true)
    private String defaultServiceVersion;

    /**
     * Get the logging value.
     *
     * @return the logging value
     */
    public Logging logging() {
        return this.logging;
    }

    /**
     * Set the logging value.
     *
     * @param logging the logging value to set
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withLogging(Logging logging) {
        this.logging = logging;
        return this;
    }

    /**
     * Get the hourMetrics value.
     *
     * @return the hourMetrics value
     */
    public Metrics hourMetrics() {
        return this.hourMetrics;
    }

    /**
     * Set the hourMetrics value.
     *
     * @param hourMetrics the hourMetrics value to set
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withHourMetrics(Metrics hourMetrics) {
        this.hourMetrics = hourMetrics;
        return this;
    }

    /**
     * Get the minuteMetrics value.
     *
     * @return the minuteMetrics value
     */
    public Metrics minuteMetrics() {
        return this.minuteMetrics;
    }

    /**
     * Set the minuteMetrics value.
     *
     * @param minuteMetrics the minuteMetrics value to set
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withMinuteMetrics(Metrics minuteMetrics) {
        this.minuteMetrics = minuteMetrics;
        return this;
    }

    /**
     * Get the cors value.
     *
     * @return the cors value
     */
    public List<CorsRule> cors() {
        return this.cors;
    }

    /**
     * Set the cors value.
     *
     * @param cors the cors value to set
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withCors(List<CorsRule> cors) {
        this.cors = cors;
        return this;
    }

    /**
     * Get the defaultServiceVersion value.
     *
     * @return the defaultServiceVersion value
     */
    public String defaultServiceVersion() {
        return this.defaultServiceVersion;
    }

    /**
     * Set the defaultServiceVersion value.
     *
     * @param defaultServiceVersion the defaultServiceVersion value to set
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withDefaultServiceVersion(String defaultServiceVersion) {
        this.defaultServiceVersion = defaultServiceVersion;
        return this;
    }
}