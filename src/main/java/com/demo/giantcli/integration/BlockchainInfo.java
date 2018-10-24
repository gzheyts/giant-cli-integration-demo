package com.demo.giantcli.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Georgy Zheyts <gzheyts@gmail.com>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainInfo {
	Integer height;
	Double difficulty;
	String error;
}
