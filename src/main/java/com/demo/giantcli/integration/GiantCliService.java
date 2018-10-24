package com.demo.giantcli.integration;

import java.util.concurrent.CompletableFuture;

/**
 * @author Georgy Zheyts <gzheyts@gmail.com>
 */
public interface GiantCliService {
	CompletableFuture<BlockchainInfo> queryLastBlockchainInfo();
}
