/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 * Changes may cause incorrect behavior and will be lost if the code is
 * regenerated.
 */

package com.microsoft.azure.storage.implementation;

import com.microsoft.azure.storage.AppendBlobs;
import com.microsoft.azure.storage.Blobs;
import com.microsoft.azure.storage.BlockBlobs;
import com.microsoft.azure.storage.Containers;
import com.microsoft.azure.storage.PageBlobs;
import com.microsoft.azure.storage.Services;
import com.microsoft.azure.storage.StorageClient;
import com.microsoft.rest.v2.RestProxy;
import com.microsoft.rest.v2.ServiceClient;
import com.microsoft.rest.v2.http.HttpPipeline;

/**
 * Initializes a new instance of the StorageClient type.
 */
public class StorageClientImpl extends ServiceClient implements StorageClient {
    /**
     * The URL of the service account, container, or blob that is the targe of the desired operation.
     */
    private String url;

    /**
     * Gets The URL of the service account, container, or blob that is the targe of the desired operation.
     *
     * @return the url value.
     */
    public String url() {
        return this.url;
    }

    /**
     * Sets The URL of the service account, container, or blob that is the targe of the desired operation.
     *
     * @param url the url value.
     * @return the service client itself
     */
    public StorageClientImpl withUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Specifies the version of the operation to use for this request.
     */
    private String version;

    /**
     * Gets Specifies the version of the operation to use for this request.
     *
     * @return the version value.
     */
    public String version() {
        return this.version;
    }

    /**
     * Sets Specifies the version of the operation to use for this request.
     *
     * @param version the version value.
     * @return the service client itself
     */
    public StorageClientImpl withVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * The Services object to access its operations.
     */
    private Services services;

    /**
     * Gets the Services object to access its operations.
     *
     * @return the Services object.
     */
    public Services services() {
        return this.services;
    }

    /**
     * The Containers object to access its operations.
     */
    private Containers containers;

    /**
     * Gets the Containers object to access its operations.
     *
     * @return the Containers object.
     */
    public Containers containers() {
        return this.containers;
    }

    /**
     * The Blobs object to access its operations.
     */
    private Blobs blobs;

    /**
     * Gets the Blobs object to access its operations.
     *
     * @return the Blobs object.
     */
    public Blobs blobs() {
        return this.blobs;
    }

    /**
     * The BlockBlobs object to access its operations.
     */
    private BlockBlobs blockBlobs;

    /**
     * Gets the BlockBlobs object to access its operations.
     *
     * @return the BlockBlobs object.
     */
    public BlockBlobs blockBlobs() {
        return this.blockBlobs;
    }

    /**
     * The PageBlobs object to access its operations.
     */
    private PageBlobs pageBlobs;

    /**
     * Gets the PageBlobs object to access its operations.
     *
     * @return the PageBlobs object.
     */
    public PageBlobs pageBlobs() {
        return this.pageBlobs;
    }

    /**
     * The AppendBlobs object to access its operations.
     */
    private AppendBlobs appendBlobs;

    /**
     * Gets the AppendBlobs object to access its operations.
     *
     * @return the AppendBlobs object.
     */
    public AppendBlobs appendBlobs() {
        return this.appendBlobs;
    }

    /**
     * Initializes an instance of StorageClient client.
     */
    public StorageClientImpl() {
        this(RestProxy.createDefaultPipeline());
    }

    /**
     * Initializes an instance of StorageClient client.
     *
     * @param httpPipeline The HTTP pipeline to send requests through.
     */
    public StorageClientImpl(HttpPipeline httpPipeline) {
        super(httpPipeline);
        this.services = new ServicesImpl(this);
        this.containers = new ContainersImpl(this);
        this.blobs = new BlobsImpl(this);
        this.blockBlobs = new BlockBlobsImpl(this);
        this.pageBlobs = new PageBlobsImpl(this);
        this.appendBlobs = new AppendBlobsImpl(this);
    }
}
