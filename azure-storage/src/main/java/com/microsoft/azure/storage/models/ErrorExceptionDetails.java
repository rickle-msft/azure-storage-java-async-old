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

/**
 * The ErrorExceptionDetails model.
 */
public class ErrorExceptionDetails {
    /**
     * The exceptionMessage property.
     */
    @JsonProperty(value = "ExceptionMessage")
    private String exceptionMessage;

    /**
     * The stackTrace property.
     */
    @JsonProperty(value = "StackTrace")
    private String stackTrace;

    /**
     * Get the exceptionMessage value.
     *
     * @return the exceptionMessage value
     */
    public String exceptionMessage() {
        return this.exceptionMessage;
    }

    /**
     * Set the exceptionMessage value.
     *
     * @param exceptionMessage the exceptionMessage value to set
     * @return the ErrorExceptionDetails object itself.
     */
    public ErrorExceptionDetails withExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    /**
     * Get the stackTrace value.
     *
     * @return the stackTrace value
     */
    public String stackTrace() {
        return this.stackTrace;
    }

    /**
     * Set the stackTrace value.
     *
     * @param stackTrace the stackTrace value to set
     * @return the ErrorExceptionDetails object itself.
     */
    public ErrorExceptionDetails withStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

}