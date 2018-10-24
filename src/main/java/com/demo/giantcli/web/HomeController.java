package com.demo.giantcli.web;

import com.demo.giantcli.integration.BlockchainInfo;
import com.demo.giantcli.integration.GiantCliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.demo.giantcli.web.HomeController.ENDPOINT;

/**
 * @author Georgy Zheyts <gzheyts@gmail.com>
 */
@Controller
@RequestMapping(ENDPOINT)
public class HomeController {
	public static final String ENDPOINT = "/";
	public static final String MODEL_KEY = "blockchain";
	private final GiantCliService giantCliService;

	@Autowired
	public HomeController(GiantCliService giantCliService) {
		this.giantCliService = giantCliService;
	}

	@GetMapping
	public CompletableFuture<String> index(Map<String, Object> model) {
		BlockchainInfo blockchainInfo = giantCliService
				.queryLastBlockchainInfo()
				.join();
		model.put(MODEL_KEY, blockchainInfo);
		return CompletableFuture.completedFuture("index");
	}
}
