package com.wells.poc.services;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.wells.poc.models.Document;
import com.wells.poc.models.ElasticSearchUpdateResponse;
import com.wells.poc.models.Index;
import com.wells.poc.models.UpdateMessage;

@Service
public class ElasticSearchService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${message.topic.name}")
	public String topic; 
	
	@Value("${spring.elasticsearch.rest.uris}")
	private String elasticSearchUrl;
	
	@Value("${message.index.type.name}")
	private String indexType;
	
	private String getMessagesRequestUrl() {
		StringBuffer request = new StringBuffer();
		request.append(elasticSearchUrl);
		request.append(File.separator);
		request.append(topic);
		request.append(File.separator);
		request.append("_search");
		return request.toString();
	}
	
	private String getMessageById(String id) {
		StringBuffer request = new StringBuffer();
		request.append(elasticSearchUrl);
		request.append(File.separator);
		request.append(topic);
		request.append(File.separator);
		request.append(indexType);
		request.append(File.separator);
		request.append(id);
		return request.toString();
	}
	
	private String getUpdateRequestUrl(String id) {
		StringBuffer request = new StringBuffer();
		request.append(elasticSearchUrl);
		request.append(File.separator);
		request.append(topic);
		request.append(File.separator);
		request.append(indexType);
		request.append(File.separator);
		request.append(id);
		request.append(File.separator);
		request.append("_update");
		return request.toString();
	}
	
	public Index fetchMessages() {
		try {
			URI messagesRequestUrl = new URI(getMessagesRequestUrl());
			ResponseEntity<Index> messages = restTemplate.getForEntity(messagesRequestUrl, Index.class);
			if(messages.getStatusCode() ==HttpStatus.OK) {
				return messages.getBody();
			}
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Document fetchMessage(String id) {
		try {
			URI getMessageByIdUrl = new URI(getMessageById(id));
			ResponseEntity<Document> documentById = restTemplate.getForEntity(getMessageByIdUrl, Document.class);
			if(documentById.getStatusCode() ==HttpStatus.OK) {
				return documentById.getBody();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public ElasticSearchUpdateResponse updateDocument(UpdateMessage document) {
		try {
			URI updateUrl = new URI(getUpdateRequestUrl(document.getDoc().getUniqueId()));
			ResponseEntity<ElasticSearchUpdateResponse> updatedDocument = restTemplate.postForEntity(updateUrl, document, ElasticSearchUpdateResponse.class);
			if(updatedDocument.getStatusCode()==HttpStatus.OK) {
				return updatedDocument.getBody();
			}
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	
	
}
