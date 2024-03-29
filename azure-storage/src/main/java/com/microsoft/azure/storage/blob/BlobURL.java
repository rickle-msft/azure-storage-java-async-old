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

import com.microsoft.azure.storage.models.*;
import com.microsoft.rest.v2.RestResponse;
import com.microsoft.rest.v2.http.AsyncInputStream;
import com.microsoft.rest.v2.http.HttpPipeline;
import io.reactivex.Single;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a URL to an Azure Storage blob; the blob may be a block blob, append blob, or page blob.
 */
public class BlobURL extends StorageURL {

    /**
     * Creates a new {@link BlobURL} object.
     *
     * @param url
     *      A {@code java.net.URL} to a blob.
     * @param pipeline
     *      An {@link HttpPipeline} representing a pipeline for requests.
     */
    public BlobURL(URL url, HttpPipeline pipeline) {
        super(url, pipeline);
    }

    /**
     * Creates a new {@link BlobURL} with the given pipeline.
     *
     * @param pipeline
     *      An {@link HttpPipeline} object to set.
     * @return
     *      A {@link BlobURL} object with the given pipeline.
     */
    public BlobURL withPipeline(HttpPipeline pipeline) {
        try {
            return new BlobURL(new URL(this.storageClient.url()), pipeline);
        } catch (MalformedURLException e) {
            // TODO: Remove
        }
        return null;
    }

    /**
     * Creates a new {@link BlobURL} with the given snapshot.
     *
     * @param snapshot
     *      A {@code java.util.Date} to set.
     * @return
     *      A {@link BlobURL} object with the given pipeline.
     */
    public BlobURL withSnapshot(String snapshot) throws MalformedURLException, UnsupportedEncodingException {
        BlobURLParts blobURLParts = URLParser.ParseURL(new URL(this.storageClient.url()));
        blobURLParts.setSnapshot(snapshot);
        return new BlobURL(blobURLParts.toURL(), super.storageClient.httpPipeline());
    }

    /**
     * Converts this BlobURL to a {@link BlockBlobURL} object.
     * @return
     *      A {@link BlockBlobURL} object.
     */
    public BlockBlobURL toBlockBlobURL() {
        try {
            return new BlockBlobURL(new URL(this.storageClient.url()), super.storageClient.httpPipeline());
        } catch (MalformedURLException e) {
            // TODO: remove.
        }
        return null;
    }

    /**
     * Converts this BlobURL to an {@link AppendBlobURL} object.
     *
     * @return
     *      An {@link AppendBlobURL} object.
     */
    public AppendBlobURL toAppendBlobURL() {
        try {
            return new AppendBlobURL(new URL(this.storageClient.url()), super.storageClient.httpPipeline());
        } catch (MalformedURLException e) {
            // TODO: remove
        }
        return null;
    }

    /**
     * Converts this BlobURL to a {@link PageBlobURL} object.
     *
     * @return
     *      A {@link PageBlobURL} object.
     */
    public PageBlobURL toPageBlobURL() {
        try {
            return new PageBlobURL(new URL(this.storageClient.url()), super.storageClient.httpPipeline());
        } catch (MalformedURLException e) {
            // TODO: remove
        }
        return null;
    }

    /**
     * StartCopy copies the data at the source URL to a blob.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/copy-blob.
     *
     * @param sourceURL
     *      A {@code String} representing the source URL to copy from.
     *      URLs outside of Azure may only be copied to block blobs.
     * @param metadata
     *      A {@link Metadata} object that specifies key value pairs to set on the blob.
     * @param sourceAccessConditions
     *      {@link BlobAccessConditions} object to check against the source
     * @param destAccessConditions
     *      {@link BlobAccessConditions} object to check against the destination
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsCopyHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsCopyHeaders, Void>> startCopyAsync(
            URL sourceURL, Metadata metadata, BlobAccessConditions sourceAccessConditions,
            BlobAccessConditions destAccessConditions) {
        if (sourceAccessConditions == null) {
            sourceAccessConditions = BlobAccessConditions.getDefault();
        }
        if (destAccessConditions == null) {
            destAccessConditions = BlobAccessConditions.getDefault();
        }
        if(metadata == null) {
            metadata = Metadata.getDefault();
        }

        return this.storageClient.blobs().copyWithRestResponseAsync(sourceURL.toString(), null, null,
                sourceAccessConditions.getHttpAccessConditions().getIfModifiedSince(),
                sourceAccessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                sourceAccessConditions.getHttpAccessConditions().getIfMatch().toString(),
                sourceAccessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                destAccessConditions.getHttpAccessConditions().getIfModifiedSince(),
                destAccessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                destAccessConditions.getHttpAccessConditions().getIfMatch().toString(),
                destAccessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                sourceAccessConditions.getLeaseAccessConditions().toString(),
                destAccessConditions.getLeaseAccessConditions().toString(), null);
    }

    /**
     * AbortCopy stops a pending copy that was previously started
     * and leaves a destination blob with 0 length and metadata.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/abort-copy-blob.
     *
     * @param copyId
     *      A {@code String} representing the copy identifier provided in the x-ms-copy-id header of
     *      the original Copy Blob operation.
     * @param leaseAccessConditions
     *      {@link LeaseAccessConditions} object representing lease access conditions
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsAbortCopyHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsAbortCopyHeaders, Void>> abortCopyAsync(
            String copyId, LeaseAccessConditions leaseAccessConditions) {
        if (leaseAccessConditions == null) {
            leaseAccessConditions = LeaseAccessConditions.getDefault();
        }

        return this.storageClient.blobs().abortCopyWithRestResponseAsync(
                copyId, null, leaseAccessConditions.toString(), null);
    }

    /**
     * GetBlob reads a range of bytes from a blob. The response also includes the blob's properties and metadata.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/get-blob.
     *
     * @param range
     *      A {@code Long} which represents the number of bytes to read or {@code null}.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that represents the access conditions for the blob.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsGetHeaders, AsyncInputStream&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsGetHeaders, AsyncInputStream>> getBlobAsync(
            BlobRange range, BlobAccessConditions accessConditions, boolean rangeGetContentMD5) {
        if (accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }


        return this.storageClient.blobs().getWithRestResponseAsync(null, null,
                range.toString(), accessConditions.getLeaseAccessConditions().toString(),
                rangeGetContentMD5, accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                null);
    }

    /**
     * Deletes the specified blob or snapshot.
     * Note that deleting a blob also deletes all its snapshots.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/delete-blob.
     *
     * @param deleteBlobSnapshotOptions
     *      A {@link DeleteBlobSnapshotOptions} which represents delete snapshot options.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that represents the access conditions for the blob.
     * @return
     *      A {@link Single&lt;RestResponse&lt;BlobsDeleteHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsDeleteHeaders, Void>> deleteAsync(
            DeleteSnapshotsOptionType deleteBlobSnapshotOptions, BlobAccessConditions accessConditions) {
        if (accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }

        return this.storageClient.blobs().deleteWithRestResponseAsync(null, null,
                accessConditions.getLeaseAccessConditions().toString(),
                deleteBlobSnapshotOptions,
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(), null);
    }

    /**
     * GetPropertiesAndMetadata returns the blob's metadata and properties.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/get-blob-properties.
     *
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that represents the access conditions for the blob.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsGetPropertiesHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsGetPropertiesHeaders, Void>> getPropertiesAndMetadataAsync(
            BlobAccessConditions accessConditions) {
        if (accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }

        return this.storageClient.blobs().getPropertiesWithRestResponseAsync(null, null,
                accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(), null);
    }

    /**
     * SetProperties changes a blob's HTTP header properties.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/set-blob-properties.
     *
     * @param headers
     *      A {@link BlobHttpHeaders} object that specifies which properties to set on the blob.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsSetPropertiesHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsSetPropertiesHeaders, Void>> setPropertiesAsync(
            BlobHttpHeaders headers, BlobAccessConditions accessConditions) {
        if (accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }

        return this.storageClient.blobs().setPropertiesWithRestResponseAsync(null,
                headers.getCacheControl(), headers.getContentType(), headers.getContentMD5(),
                headers.getContentEncoding(),
                headers.getContentLanguage(), accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                headers.getContentDisposition(),
                null, null, null, null);
    }

    /**
     * SetMetadata changes a blob's metadata.
     * https://docs.microsoft.com/rest/api/storageservices/set-blob-metadata.
     *
     * @param metadata
     *      A {@link Metadata} object that specifies key value pairs to set on the blob.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsSetMetadataHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsSetMetadataHeaders, Void>> setMetadaAsync(
            Metadata metadata, BlobAccessConditions accessConditions) {
        if (accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }
        if(metadata == null) {
            metadata = Metadata.getDefault();
        }

        return this.storageClient.blobs().setMetadataWithRestResponseAsync(null, metadata.toString(),
                accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                null);
    }

    /**
     * CreateSnapshot creates a read-only snapshot of a blob.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/snapshot-blob.
     *
     * @param metadata
     *      A {@link Metadata} object that specifies key value pairs to set on the blob.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that represents the access conditions for the blob.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsTakeSnapshotHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsTakeSnapshotHeaders, Void>> createSnapshotAsync(
            Metadata metadata, BlobAccessConditions accessConditions) {
        if (accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }
        if(metadata == null) {
            metadata = Metadata.getDefault();
        }

        return this.storageClient.blobs().takeSnapshotWithRestResponseAsync(null,
                metadata.toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                accessConditions.getLeaseAccessConditions().toString(), null);
    }

    /**
     * AcquireLease acquires a lease on the blob for write and delete operations. The lease duration must be between
     * 15 to 60 seconds, or infinite (-1).
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/lease-blob.
     *
     * @param proposedID
     *      A {@code String} in any valid GUID format.
     * @param duration
     *      A {@code Integer} specifies the duration of the lease, in seconds, or negative one (-1) for a lease that
     *      never expires. A non-infinite lease can be between 15 and 60 seconds.
     * @param httpAccessConditions
     *      A {@link HttpAccessConditions} object that represents HTTP access conditions.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsLeaseHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsLeaseHeaders, Void>> acquireLeaseAsync(
            String proposedID, Integer duration, HttpAccessConditions httpAccessConditions) {
        if (httpAccessConditions == null) {
            httpAccessConditions = HttpAccessConditions.getDefault();
        }

        return this.storageClient.blobs().leaseWithRestResponseAsync(LeaseActionType.ACQUIRE, null,
                null, null, duration, proposedID,
                httpAccessConditions.getIfModifiedSince(),
                httpAccessConditions.getIfUnmodifiedSince(),
                httpAccessConditions.getIfMatch().toString(),
                httpAccessConditions.getIfNoneMatch().toString(),
                null);
    }

    /**
     * RenewLease renews the blob's previously-acquired lease.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/lease-blob.
     * @param leaseID
     *      A {@code String} representing the lease on the blob.
     * @param httpAccessConditions
     *      A {@link HttpAccessConditions} object that represents HTTP access conditions.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsLeaseHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsLeaseHeaders, Void>> renewLeaseAsync(
            String leaseID, HttpAccessConditions httpAccessConditions) {
        if (httpAccessConditions == null) {
            httpAccessConditions = HttpAccessConditions.getDefault();
        }

        return this.storageClient.blobs().leaseWithRestResponseAsync(LeaseActionType.RENEW, null,
                leaseID, null, null, null,
                httpAccessConditions.getIfModifiedSince(),
                httpAccessConditions.getIfUnmodifiedSince(),
                httpAccessConditions.getIfMatch().toString(), httpAccessConditions.getIfNoneMatch().toString(),
                null);
    }

    /**
     * ReleaseLease releases the blob's previously-acquired lease. For more information, see
     * https://docs.microsoft.com/rest/api/storageservices/lease-blob.
     *
     * @param leaseID
     *      A {@code String} representing the lease on the blob.
     * @param httpAccessConditions
     *      A {@link HttpAccessConditions} object that represents HTTP access conditions.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsLeaseHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsLeaseHeaders, Void>> releaseLeaseAsync(
            String leaseID, HttpAccessConditions httpAccessConditions) {
        if (httpAccessConditions == null) {
            httpAccessConditions = HttpAccessConditions.getDefault();
        }

        return this.storageClient.blobs().leaseWithRestResponseAsync(LeaseActionType.RELEASE, null,
                leaseID, null, null, null,
                httpAccessConditions.getIfModifiedSince(),
                httpAccessConditions.getIfUnmodifiedSince(),
                httpAccessConditions.getIfMatch().toString(), httpAccessConditions.getIfNoneMatch().toString(),
                null);
    }

    /**
     * BreakLease breaks the blob's previously-acquired lease (if it exists). Pass the LeaseBreakDefault (-1) constant
     * to break a fixed-duration lease when it expires or an infinite lease immediately.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/lease-blob.
     *
     * @param leaseID
     *      A {@code String} representing the lease ID to break.
     * @param breakPeriodInSeconds
     *      An optional {@code Integer} representing the proposed duration of seconds that the lease should continue
     *      before it is broken, between 0 and 60 seconds. This break period is only used if it is shorter than the time
     *      remaining on the lease. If longer, the time remaining on the lease is used. A new lease will not be
     *      available before the break period has expired, but the lease may be held for longer than the break period
     * @param httpAccessConditions
     *      A {@link HttpAccessConditions} object that represents HTTP access conditions.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsLeaseHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsLeaseHeaders, Void>> breakLeaseAsync(String leaseID, Integer breakPeriodInSeconds,
                                        HttpAccessConditions httpAccessConditions) {
        if (httpAccessConditions == null) {
            httpAccessConditions = HttpAccessConditions.getDefault();
        }

        return this.storageClient.blobs().leaseWithRestResponseAsync(LeaseActionType.RENEW, null,
                leaseID, breakPeriodInSeconds, null, null,
                httpAccessConditions.getIfModifiedSince(),
                httpAccessConditions.getIfUnmodifiedSince(),
                httpAccessConditions.getIfMatch().toString(), httpAccessConditions.getIfNoneMatch().toString(),
                null);
    }

    /**
     * ChangeLease changes the blob's lease ID.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/lease-blob.
     *
     * @param leaseId
     *      A {@code String} representing the lease ID to change.
     * @param proposedID
     *      A {@code String} in any valid GUID format.
     * @param httpAccessConditions
     *      A {@link HttpAccessConditions} object that represents HTTP access conditions.
     * @return
     *      The {@link Single&lt;RestResponse&lt;BlobsLeaseHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsLeaseHeaders, Void>> changeLeaseAsync(
            String leaseId, String proposedID, HttpAccessConditions httpAccessConditions) {
        if (httpAccessConditions == null) {
            httpAccessConditions = HttpAccessConditions.getDefault();
        }

        return this.storageClient.blobs().leaseWithRestResponseAsync(LeaseActionType.RENEW, null,
                leaseId, null, null, proposedID,
                httpAccessConditions.getIfModifiedSince(),
                httpAccessConditions.getIfUnmodifiedSince(),
                httpAccessConditions.getIfMatch().toString(), httpAccessConditions.getIfNoneMatch().toString(),
                null);
    }
}
