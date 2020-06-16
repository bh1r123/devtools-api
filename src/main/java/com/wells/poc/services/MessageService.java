package com.wells.poc.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.kafka.common.record.TimestampType;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wells.poc.exceptions.BadRequestException;
import com.wells.poc.models.DevTools;
import com.wells.poc.models.Document;
import com.wells.poc.models.ElasticSearchUpdateResponse;
import com.wells.poc.models.Index;
import com.wells.poc.models.Message;
import com.wells.poc.models.PaginationResponse;
import com.wells.poc.models.SuccessResponse;
import com.wells.poc.models.UpdateMessage;

@Service
public class MessageService {

	private static Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	KafkaTemplate<String, com.wells.poc.models.Message> kafkaTemplate;

	@Value("${message.topic.name}")
	public String topic;


	/**
	 * Fetch the Message From Elastic Search using Id.
	 * 
	 * @param id
	 * @return
	 */
	public Message fetchMessage(String id) {
		Document fetchMessage = elasticSearchService.fetchMessage(id);
		if (fetchMessage != null) {
			Message message = new Message();
			message.setToolName(fetchMessage.get_source().getToolName());
			message.setMessage(fetchMessage.get_source().getMessage());
			return message;
		} else {
			throw new BadRequestException("Unable to fetch the message with given Id");
		}
	}

	/**
	 * Updating the message in ES.
	 * 
	 * @param document
	 * @return
	 */
	public SuccessResponse updatedMessage(UpdateMessage document) {
		ElasticSearchUpdateResponse updateDocument = elasticSearchService.updateDocument(document);
		if (updateDocument != null) {
			if (updateDocument.getResult().equalsIgnoreCase("updated")) {
				return new SuccessResponse("Updated successfully..!");
			}
		} 
		throw new BadRequestException("Unable to update the record");
	}

	/**
	 * Reading the messages information from the Excel sheet.
	 * 
	 * @param multipartFile
	 * @return
	 */
	public SuccessResponse sendBulkMessages(MultipartFile multipartFile) {
		SuccessResponse response = new SuccessResponse("Messages sent");
		List<Message> messages = new ArrayList<>();
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
			int numberOfSheets = workbook.getNumberOfSheets();
			if (numberOfSheets >= 1) {
				Sheet sheet = workbook.getSheetAt(0);
				int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
				if (physicalNumberOfRows == 1) {
					throw new BadRequestException("Empty Sheet");
				} else {
					IntStream.range(1, physicalNumberOfRows).forEach(position -> {
						Row row = sheet.getRow(position);
						if (!isRowEmpty(row)) {
							Message message = new Message();
							message.setMessage(row.getCell(DevTools.MESSAGE.ordinal()).getStringCellValue());
							message.setToolName(row.getCell(DevTools.NAME.ordinal()).getStringCellValue());
							messages.add(message);
						}
					});
					if (messages.isEmpty()) {
						throw new BadRequestException("Empty Sheet..!");
					} else {
						messages.stream().forEach(message -> {
							sendMessage(message);
						});
						response.setMessage(messages.size() + " records created..!");
					}
				}
			} else {
				throw new BadRequestException("Empty Sheet");
			}
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	/**
	 * Fetch all messages. It will return top 10 records only.
	 * 
	 * @return
	 */
	public PaginationResponse<com.wells.poc.models.Message> fetchAllMessages() {
		Index fetchMessages = elasticSearchService.fetchMessages();
		if (fetchMessages == null) {
			return new PaginationResponse<>(0, "", Collections.emptyList());
		}
		if (fetchMessages.getHits() != null) {
			List<Document> hits = fetchMessages.getHits().getHits();
			List<Message> collect = hits.stream().map(document -> {
				Message message = new Message();
				message.setUniqueId(document.get_id());
				message.setMessage(document.get_source().getMessage());
				message.setToolName(document.get_source().getToolName());
				return message;
			}).collect(Collectors.toList());
			return new PaginationResponse<>(fetchMessages.getHits().getTotal(), "", collect);
		} else {
			return new PaginationResponse<>(0, "", Collections.emptyList());
		}
	}

	/**
	 * Sending Message using kafka
	 * 
	 * @param message
	 */
	public void sendMessage(Message message) {
		LOGGER.info(topic);
		message.setUniqueId(UUID.randomUUID().toString());
		org.springframework.messaging.Message<Message> build = MessageBuilder.withPayload(message)
				.setHeader(KafkaHeaders.TOPIC, topic)
				.setHeader(KafkaHeaders.TIMESTAMP_TYPE, TimestampType.LOG_APPEND_TIME).build();
		kafkaTemplate.send(build);
	}

	/**
	 * Checks whether the row is empty or not.
	 * 
	 * @param row
	 * @return
	 */
	private boolean isRowEmpty(Row row) {
		boolean isEmpty = true;
		DataFormatter dataFormatter = new DataFormatter();
		if (row != null) {
			for (Cell cell : row) {
				if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
					isEmpty = false;
					break;
				}
			}
		}
		return isEmpty;
	}

}
