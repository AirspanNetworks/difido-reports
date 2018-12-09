package il.co.topq.report.business.archiver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArchiverHttpClient {

	private final Logger log = LoggerFactory.getLogger(ArchiverHttpClient.class);

	private final RestTemplate restTemplate;

	private final String host;

	public ArchiverHttpClient(String host) {
		this.host = host;
		this.restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

	}

	@SuppressWarnings("unchecked")
	public <T> T get(String path, @SuppressWarnings("rawtypes") TypeReference valueTypeRef) {
		ResponseEntity<String> response = restTemplate.getForEntity(host + path, String.class);
		try {
			return (T) new ObjectMapper().readValue(response.getBody(), valueTypeRef);
		} catch (Exception e) {
			log.error("Failed to read value from Difido server", e);
		}
		return null;
	}

	public File getFile(String path, String fileName) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<byte[]> response = restTemplate.exchange(host + path, HttpMethod.GET, entity, byte[].class, "1");
		File file = new File(fileName);
		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				Files.write(file.toPath(), response.getBody());
			} catch (IOException e) {
				log.error("Failed to download file", e);
				return null;
			}
		}
		return file;
	}

}
