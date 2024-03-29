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

/**
 * Defines options availble when calling ListBlobs.
 */
public class ListBlobsOptions {

    private static ListBlobsOptions defaultListBlobsOptions;

    private BlobListingDetails details;

    private String prefix;

    private String delimiter;

    private Integer maxResults;

    /**
     * A {@Link ListBlobsOptions} object.
     *
     * @param details
     *           A {@Link BlobListingDetails} object indicating what additional information the service should return
     *           with each blob.
     * @param prefix
     *           A {@code String} that filters the results to return only blobs whose names begin with the specified
     *           prefix.
     * @param delimiter
     *           A {@code String}. When the request includes this parameter, the operation returns a BlobPrefix element
     *           in the response body that acts as a placeholder for all blobs whose names begin with the same substring
     *           up to the appearance of the delimiter character. The delimiter may be a single character or a string.
     * @param maxResults
     *           Specifies the maximum number of blobs to return, including all BlobPrefix elements. If the request does
     *           not specify maxResults or specifies a value greater than 5,000, the server will return up to 5,000
     *           items.
     */
    public ListBlobsOptions(BlobListingDetails details, String prefix, String delimiter, Integer maxResults) {
        if (maxResults != null && maxResults < 0) {
            throw new IllegalArgumentException("MaxResults must be greater than 0.");
        }
        this.details = details;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.maxResults = maxResults;
    }

    /**
     * @return A {@link BlobListingDetails} object indicating what additional information the service should return
     *           with each blob.
     */
    public BlobListingDetails getDetails() {
        return details;
    }

    /**
     * @return A {@code String} that filters the results to return only blobs whose names begin with the specified
     *           prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return A {@code String}. When the request includes this parameter, the operation returns a BlobPrefix element
     *           in the response body that acts as a placeholder for all blobs whose names begin with the same substring
     *           up to the appearance of the delimiter character. The delimiter may be a single character or a string.
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * @return Specifies the maximum number of blobs to return, including all BlobPrefix elements. If the request does
     *           not specify maxresults or specifies a value greater than 5,000, the server will return up to 5,000
     *           items.
     */
    public Integer getMaxResults() {
        return maxResults;
    }

    public static ListBlobsOptions getDefualt() {
        if(defaultListBlobsOptions == null) {
            defaultListBlobsOptions = new ListBlobsOptions(
                    new BlobListingDetails(false, false, false, false),
                    null, null, null);
        }
        return defaultListBlobsOptions;
    }
}
