package com.demo.giantcli.integration.impl;

import com.demo.giantcli.integration.BlockchainInfo;
import com.demo.giantcli.integration.GiantCliService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.currentThread;

/**
 * @author Georgy Zheyts <gzheyts@gmail.com>
 */
@Slf4j
@Service
public class GiantCliServiceImpl implements GiantCliService {
	final ObjectMapper objectMapper;
	@Value("${giant-cli.command_name:./giant-cli}")
	private String COMMAND_NAME;
	@Value("${giant-cli.option_name_test_net:-testnet}")
	private String OPTION_NAME_TESTNET;
	@Value("${giant-cli.operation_name_getblockcount:getblockcount}")
	private String OPERATION_NAME_GETBLOCKCOUNT;
	@Value("${giant-cli.operation_name_getblockhash:getblockhash}")
	private String OPERATION_NAME_GETBLOCKHASH = "getblockhash";
	@Value("${giant-cli.operation_name_getblock:getblock}")
	private String OPERATION_NAME_GETBLOCK;
	@Value("${giant-cli.work_dir:/tmp/giant-1.2.2.1-linux64}")
	private String WORK_DIR;

	@Autowired
	public GiantCliServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	@Async
	public CompletableFuture<BlockchainInfo> queryLastBlockchainInfo() {
		return CompletableFuture
				.supplyAsync(this::requestBlockNumber)
				.thenApplyAsync(this::requestBlockHash)
				.thenApplyAsync(this::requestBlockInfo)
				.exceptionally(throwable -> BlockchainInfo.builder().error(throwable.getLocalizedMessage()).build());
	}

	private String requestBlockNumber() {
		log.debug(">>> thread: {}, operation: {}", currentThread(), OPERATION_NAME_GETBLOCK);
		String blockNum = executeOperation(OPERATION_NAME_GETBLOCKCOUNT
				, Collections.singletonList(OPTION_NAME_TESTNET), null).outputUTF8();
		log.debug("<<< thread: {}, operation: {},  blockNum: {}", currentThread(), OPERATION_NAME_GETBLOCK, blockNum);
		return blockNum;
	}

	private String requestBlockHash(String blockNum) {
		log.debug(">>> thread: {}, operation: {},  blockNum: {}", currentThread(), OPERATION_NAME_GETBLOCKHASH, blockNum);
		String blockHash = executeOperation(OPERATION_NAME_GETBLOCKHASH
				, Collections.singletonList(OPTION_NAME_TESTNET), blockNum).outputUTF8();
		log.debug("<<< thread: {}, operation: {},  blockHash: {}", currentThread(), OPERATION_NAME_GETBLOCKHASH, blockHash);
		return blockHash;
	}

	private BlockchainInfo requestBlockInfo(String blockHash) {
		log.debug(">>> thread: {}, operation: {},  blockHash: {}", currentThread(), OPERATION_NAME_GETBLOCKHASH, blockHash);
		ProcessResult processResult = executeOperation(OPERATION_NAME_GETBLOCK
				, Collections.singletonList(OPTION_NAME_TESTNET)
				, blockHash);
		BlockchainInfo parsed;
		try {
			parsed = objectMapper.readValue(processResult.outputUTF8(), BlockchainInfo.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		log.debug("<<< thread: {}, operation: {},  result: {}", currentThread(), OPERATION_NAME_GETBLOCKHASH, parsed);
		return parsed;
	}

	private ProcessResult executeOperation(String operationName, List<String> options, String value) {
		List<String> commands = new LinkedList<>();
		commands.add(COMMAND_NAME);
		commands.addAll(options);
		commands.add(operationName);
		if (value != null) {
			commands.add(value);
		}
		try {
			return new ProcessExecutor(commands).directory(new File(WORK_DIR)).readOutput(true)
					.timeout(6, TimeUnit.SECONDS).exitValueNormal().execute();
		} catch (IOException | InterruptedException | InvalidExitValueException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}
}
