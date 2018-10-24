package com.demo.giantcli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static com.demo.giantcli.web.HomeController.ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Georgy Zheyts <gzheyts@gmail.com>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GiantCliDemoIntegrationTests {
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void testPageLoads() {
		ResponseEntity<String> entity = this.testRestTemplate.getForEntity(ENDPOINT, String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testPageRefreshesEvery5sec() {
		ResponseEntity<String> entity = this.testRestTemplate.getForEntity(ENDPOINT, String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getBody()).contains("<meta http-equiv=\"refresh\" content=\"300\"/>");
	}

	@Test
	public void testBlockchainInfoPresented() {
		ResponseEntity<String> entity = this.testRestTemplate.getForEntity(ENDPOINT, String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getBody())
				.containsPattern("Height: \\d+")
				.containsPattern("Difficulty: \\d\\.\\d{1,8}");
	}

	@Test
	public void testErrorPage() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = this.testRestTemplate
				.exchange("/error_page", HttpMethod.GET, requestEntity, String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody())
				.contains("Something went wrong: 404 Not Found");
	}
}
