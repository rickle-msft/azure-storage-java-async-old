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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * The BlobPrefix model.
 */
@JacksonXmlRootElement(localName = "BlobPrefix")
public class BlobPrefix {
    /**
     * The name property.
     */
    @JsonProperty(value = "Name", required = true)
    private String name;

    /**
     * Get the name value.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name value.
     *
     * @param name the name value to set
     * @return the BlobPrefix object itself.
     */
    public BlobPrefix withName(String name) {
        this.name = name;
        return this;
    }
}
