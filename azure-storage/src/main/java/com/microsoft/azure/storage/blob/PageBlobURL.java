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
import com.microsoft.rest.v2.http.UrlBuilder;
import io.reactivex.Single;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Represents a URL to a page blob.
 */
public final class PageBlobURL extends BlobURL {

    /**
     * Creates a new {@link PageBlobURL} object.
     *
     * @param url
     *      A {@code java.net.URL} to a page blob.
     * @param pipeline
     *      A {@link HttpPipeline} object representing the pipeline for requests.
     */
    public PageBlobURL(URL url, HttpPipeline pipeline) {
        super( url, pipeline);
    }

    /**
     * Creates a new {@link PageBlobURL} with the given pipeline.
     *
     * @param pipeline
     *      A {@link HttpPipeline} object to set.
     * @return
     *      A {@link PageBlobURL} object with the given pipeline.
     */
    public PageBlobURL withPipeline(HttpPipeline pipeline) {
        try {
            return new PageBlobURL(new URL(this.storageClient.url()), pipeline);
        } catch (MalformedURLException e) {
            // TODO: remove
        }
        return null;
    }

    /**
     * Creates a new {@link PageBlobURL} with the given snapshot.
     *
     * @param snapshot
     *      A {@code java.util.Date} to set.
     * @return
     *      A {@link PageBlobURL} object with the given pipeline.
     */
    public PageBlobURL withSnapshot(String snapshot) throws MalformedURLException, UnsupportedEncodingException {
        BlobURLParts blobURLParts = URLParser.ParseURL(new URL(this.storageClient.url()));
        blobURLParts.setSnapshot(snapshot);
        return new PageBlobURL(blobURLParts.toURL(), super.storageClient.httpPipeline());
    }

    /**
     * Create creates a page blob of the specified length. Call PutPage to upload data data to a page blob.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/put-blob.
     *
     * @param size
     *      Specifies the maximum size for the page blob, up to 8 TB. The page blob size must be aligned to a
     *      512-byte boundary.
     * @param sequenceNumber
     *      A user-controlled value that you can use to track requests. The value of the sequence number must be
     *      between 0 and 2^63 - 1.The default value is 0.
     * @param headers
     *      A {@link BlobHttpHeaders} object that specifies which properties to set on the blob.
     * @param metadata
     *      A {@link Metadata} object that specifies key value pairs to set on the blob.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *       The {@link Single &lt;RestResponse&lt;BlobsPutHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsPutHeaders, Void>> putBlobAsync(
            Long size, Long sequenceNumber, Metadata metadata, BlobHttpHeaders headers,
            BlobAccessConditions accessConditions) {
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException("SequenceNumber must be greater than or equal to 0.");
        }
        if(metadata == null) {
            metadata = Metadata.getDefault();
        }
        if(headers == null) {
            headers = BlobHttpHeaders.getDefault();
        }
        if(accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }

        return this.storageClient.blobs().putWithRestResponseAsync(BlobType.PAGE_BLOB, null,
                null, null, headers.getContentType(), headers.getContentEncoding(),
                headers.getContentLanguage(), headers.getContentMD5(), headers.getCacheControl(),
                metadata.toString(), accessConditions.getLeaseAccessConditions().toString(),
                headers.getContentDisposition(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                size, sequenceNumber, null);
    }

    /**
     * PutPages writes 1 or more pages to the page blob. The start and end offsets must be a multiple of 512.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/put-page.
     *
     * @param pageRange
     *      A {@link PageRange} object. Specifies the range of bytes to be written as a page.
     * @param body
     *      A {@link AsyncInputStream} that contains the content of the page.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      A {@link Single &lt;RestResponse&lt;PageBlobsPutPage, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<PageBlobsPutPageHeaders, Void>> putPagesAsync(
            PageRange pageRange, AsyncInputStream body, BlobAccessConditions accessConditions) {
        if(accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }
        String pageRangeStr;
        try {
            pageRangeStr = this.pageRangeToString(pageRange);
        }
        catch (IllegalArgumentException e) {
            return Single.error(e);
        }
        return this.storageClient.pageBlobs().putPageWithRestResponseAsync(PageWriteType.UPDATE, body,
                null, pageRangeStr, accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getPageBlobAccessConditions().getIfSequenceNumberLessThanOrEqual(),
                accessConditions.getPageBlobAccessConditions().getIfSequenceNumberLessThan(),
                accessConditions.getPageBlobAccessConditions().getIfSequenceNumberEqual(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(), null);
    }

    /**
     * ClearPages frees the specified pages from the page blob.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/put-page.
     *
     * @param pageRange
     *      A {@link PageRange} object. Specifies the range of bytes to be written as a page.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      A {@link Single &lt;RestResponse&lt;PageBlobsPutPage, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<PageBlobsPutPageHeaders, Void>> clearPagesAsync(
            PageRange pageRange, BlobAccessConditions accessConditions) {
     if(accessConditions == null) {
         accessConditions = BlobAccessConditions.getDefault();
     }
     String pageRangeStr;
     try {
         pageRangeStr = this.pageRangeToString(pageRange);
     }
     catch (IllegalArgumentException e) {
         return Single.error(e);
     }
     return this.storageClient.pageBlobs().putPageWithRestResponseAsync(PageWriteType.CLEAR, null,
             null, pageRangeStr, accessConditions.getLeaseAccessConditions().toString(),
             accessConditions.getPageBlobAccessConditions().getIfSequenceNumberLessThanOrEqual(),
             accessConditions.getPageBlobAccessConditions().getIfSequenceNumberLessThan(),
             accessConditions.getPageBlobAccessConditions().getIfSequenceNumberEqual(),
             accessConditions.getHttpAccessConditions().getIfModifiedSince(),
             accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
             accessConditions.getHttpAccessConditions().getIfMatch().toString(),
             accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(), null);
    }

    /**
     * GetPageRanges returns the list of valid page ranges for a page blob or snapshot of a page blob.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/get-page-ranges.
     *
     * @param blobRange
     *      A {@link BlobRange} object specifies the range of bytes over which to list ranges, inclusively. If
     *      omitted, then all ranges for the blob are returned.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      A {@link Single &lt;RestResponse&lt;PageBlobsPutPage, PageList&gt;&gt;} object if successful.
     */
    public Single<RestResponse<PageBlobsGetPageRangesHeaders, PageList>> getPageRangesAsync(
            BlobRange blobRange, BlobAccessConditions accessConditions) {
     if(accessConditions == null) {
         accessConditions = BlobAccessConditions.getDefault();
     }
     if(blobRange == null) {
         blobRange.getDefault();
     }
     return this.storageClient.pageBlobs().getPageRangesWithRestResponseAsync(null,
             null, null,
             blobRange.toString(), accessConditions.getLeaseAccessConditions().toString(),
             accessConditions.getHttpAccessConditions().getIfModifiedSince(),
             accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
             accessConditions.getHttpAccessConditions().getIfMatch().toString(),
             accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
             null);
    }

    /**
     * GetPageRangesDiff gets the collection of page ranges that differ between a specified snapshot and this page blob.
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/get-page-ranges.
     *
     * @param blobRange
     *     A {@link PageRange} object. Specifies the range of bytes to be written as a page.
     * @param prevSnapshot
     *     A {@code org.joda.time.DateTime} specifies that the response will contain only pages that were changed
     *     between target blob and previous snapshot. Changed pages include both updated and cleared pages. The target
     *     blob may be a snapshot, as long as the snapshot specified by prevsnapshot is the older of the two.
     * @param accessConditions
     *     A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *     complete.
     * @return
     *      The {@link Single&lt;RestResponse&lt;PageBlobsGetPageRangesHeaders, PageList&gt;&gt;} object if successful.
     */
    //TODO: Get rid of joda time (use java.util.Date?)
    public Single<RestResponse<PageBlobsGetPageRangesHeaders, PageList>> getPageRangesDiffAsync(
            BlobRange blobRange, String prevSnapshot, BlobAccessConditions accessConditions) {
        if(blobRange == null) {
            blobRange = BlobRange.getDefault();
        }
        if(accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }
        return this.storageClient.pageBlobs().getPageRangesWithRestResponseAsync(null,
                null, prevSnapshot,
                blobRange.toString(), accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                null);
    }

    /**
     * Resize resizes the page blob to the specified size (which must be a multiple of 512).
     * For more information, see https://docs.microsoft.com/rest/api/storageservices/set-blob-properties.
     *
     * @param length
     *      Resizes a page blob to the specified size. If the specified value is less than the current size of the
     *      blob, then all pages above the specified value are cleared.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      The {@link Single &lt;RestResponse&lt;BlobsSetPropertiesHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsSetPropertiesHeaders, Void>> resizeAsync(
            Long length, BlobAccessConditions accessConditions) {
        if (length%512 != 0) {
            throw new IllegalArgumentException("Length must be a multiple of a page size (512).");
        }
        if(accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }
        return this.storageClient.blobs().setPropertiesWithRestResponseAsync(null,
                null, null, null, null,
                null, accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                null, length, null, null, null);
    }

    /**
     * SetSequenceNumber sets the page blob's sequence number.
     *
     * @param action
     *      Indicates how the service should modify the blob's sequence number.
     * @param sequenceNumber
     *      The blob's sequence number. The sequence number is a user-controlled property that you can use to track
     *      requests and manage concurrency issues.
     * @param headers
     *      A {@link BlobHttpHeaders} object that specifies which properties to set on the blob.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      The {@link Single &lt;RestResponse&lt;BlobsSetPropertiesHeaders, Void&gt;&gt;} object if successful.
     */
    public Single<RestResponse<BlobsSetPropertiesHeaders, Void>> setSequenceNumber(
            SequenceNumberActionType action, Long sequenceNumber, BlobHttpHeaders headers,
            BlobAccessConditions accessConditions) {
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException("SequenceNumber must be greater than or equal to 0.");
        }
        if(headers == null) {
            headers = BlobHttpHeaders.getDefault();
        }
        if(accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }
        if(action == SequenceNumberActionType.INCREMENT) {
           sequenceNumber = null;
        }
        return this.storageClient.blobs().setPropertiesWithRestResponseAsync(null,
                headers.getCacheControl(), headers.getContentType(), headers.getContentMD5(),
                headers.getContentEncoding(), headers.getContentLanguage(),
                accessConditions.getLeaseAccessConditions().toString(),
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(),
                headers.getContentDisposition(),
                null, action, sequenceNumber, null);
    }

    /**
     * StartIncrementalCopy begins an operation to start an incremental copy from one page blob's snapshot to this page
     * blob. The snapshot is copied such that only the differential changes between the previously copied snapshot are
     * transferred to the destination. The copied snapshots are complete copies of the original snapshot and can be read
     * or copied from as usual. For more information, see
     * https://docs.microsoft.com/rest/api/storageservices/incremental-copy-blob and
     * https://docs.microsoft.com/en-us/azure/virtual-machines/windows/incremental-snapshots.
     * @param source
     *      A {@code java.net.URL} which specifies the name of the source page blob.
     * @param snapshot
     *      A {@code String} which specifies the snapshot on the copy source.
     * @param accessConditions
     *      A {@link BlobAccessConditions} object that specifies under which conditions the operation should
     *      complete.
     * @return
     *      A {@link Single &lt;RestResponse&lt;PageBlobsIncrementalCopyHeaders, Void&gt;&gt;} object if successful.
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public Single<RestResponse<PageBlobsIncrementalCopyHeaders, Void>> startIncrementalCopyAsync(
            URL source, String snapshot, BlobAccessConditions accessConditions) {
        if(accessConditions == null) {
            accessConditions = BlobAccessConditions.getDefault();
        }

        String query = source.getQuery();
        if(query == null) {
            query = "snapshot=" + snapshot;
        }
        else {
            query += "&snapshot=" + snapshot;
        }
        try {
            UrlBuilder builder = UrlBuilder.parse(source.toString());
            builder.addQueryParameter("snapshot", snapshot);
            source = new URL(builder.toString()); // TODO: update.
        } catch (MalformedURLException e) {
            return Single.error(e);
         }

        return this.storageClient.pageBlobs().incrementalCopyWithRestResponseAsync(source.toString(),
                null, null,
                accessConditions.getHttpAccessConditions().getIfModifiedSince(),
                accessConditions.getHttpAccessConditions().getIfUnmodifiedSince(),
                accessConditions.getHttpAccessConditions().getIfMatch().toString(),
                accessConditions.getHttpAccessConditions().getIfNoneMatch().toString(), null);
    }

    private String pageRangeToString(PageRange pageRange) throws IllegalArgumentException {
        if (pageRange.start() < 0 || pageRange.end() <= 0) {
            throw new IllegalArgumentException("PageRange's start and end values must be greater than or equal to " +
                    "0 if specified.");
        }
        if (pageRange.start()%512 != 0 ) {
            throw new IllegalArgumentException("PageRange's start value must be a multiple of 512.");
        }
        if (pageRange.end()%512 != 0) {
            throw new IllegalArgumentException("PageRange's end value must be 1 less than a multiple of 512.");
        }
        if (pageRange.end() <= pageRange.start()) {
            throw new IllegalArgumentException("PageRange's End value must be after the start.");
        }

        StringBuilder range = new StringBuilder("bytes=");
        range.append(pageRange.start());
        range.append('-');
        range.append(pageRange.end());
        return range.toString();
    }
}
