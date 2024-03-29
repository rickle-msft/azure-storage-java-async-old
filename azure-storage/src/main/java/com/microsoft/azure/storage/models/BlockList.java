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
 * The BlockList model.
 */
@JacksonXmlRootElement(localName = "BlockList")
public class BlockList {
    /**
     * The committedBlocks property.
     */
    @JacksonXmlElementWrapper(localName = "CommittedBlocks")
    private List<Block> committedBlocks;

    /**
     * The uncommittedBlocks property.
     */
    @JacksonXmlElementWrapper(localName = "UncommittedBlocks")
    private List<Block> uncommittedBlocks;

    /**
     * Get the committedBlocks value.
     *
     * @return the committedBlocks value
     */
    public List<Block> committedBlocks() {
        return this.committedBlocks;
    }

    /**
     * Set the committedBlocks value.
     *
     * @param committedBlocks the committedBlocks value to set
     * @return the BlockList object itself.
     */
    public BlockList withCommittedBlocks(List<Block> committedBlocks) {
        this.committedBlocks = committedBlocks;
        return this;
    }

    /**
     * Get the uncommittedBlocks value.
     *
     * @return the uncommittedBlocks value
     */
    public List<Block> uncommittedBlocks() {
        return this.uncommittedBlocks;
    }

    /**
     * Set the uncommittedBlocks value.
     *
     * @param uncommittedBlocks the uncommittedBlocks value to set
     * @return the BlockList object itself.
     */
    public BlockList withUncommittedBlocks(List<Block> uncommittedBlocks) {
        this.uncommittedBlocks = uncommittedBlocks;
        return this;
    }
}
