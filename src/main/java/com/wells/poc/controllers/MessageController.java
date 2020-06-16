package com.wells.poc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wells.poc.models.Message;
import com.wells.poc.models.PaginationResponse;
import com.wells.poc.models.SuccessResponse;
import com.wells.poc.models.UpdateMessage;
import com.wells.poc.services.MessageService;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class MessageController {

	@Autowired
	private MessageService messageService;

	/**
	 * Bulk insertion of Document
	 * 
	 * @param file
	 * @return
	 */
	@PostMapping(value = "/messages/bulkupload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuccessResponse> sendMessage(@RequestParam("file") MultipartFile file) {
		SuccessResponse sendBulkMessages = messageService.sendBulkMessages(file);
		return ResponseEntity.ok(sendBulkMessages);
	}

	/**
	 * Fetch All Documents
	 * 
	 * @return
	 */
	@GetMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse<Message>> fetchMessages() {
		PaginationResponse<Message> messages = messageService.fetchAllMessages();
		return ResponseEntity.ok(messages);
	}

	/**
	 * Fetch Document information based on id.
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/messages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Message> fetchMessage(@PathVariable(value = "id") String id) {
		Message fetchUser = messageService.fetchMessage(id);
		return ResponseEntity.ok(fetchUser);
	}

	/**
	 * Updating the document based on id.
	 * 
	 * @param id
	 * @param message
	 * @return
	 */
	@PatchMapping(value = "/messages/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuccessResponse> updateMessage(@PathVariable(value = "id") String id,
			@RequestBody Message message) {
		message.setUniqueId(id);
		UpdateMessage updatedDocument = new UpdateMessage(message);
		SuccessResponse updateResponse = messageService.updatedMessage(updatedDocument);
		return ResponseEntity.ok(updateResponse);
	}

	/**
	 * TO Create a document
	 * 
	 * @param message
	 * @return
	 */
	@PostMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuccessResponse> createMessage(@RequestBody Message message) {
		messageService.sendMessage(message);
		return ResponseEntity.ok(new SuccessResponse("Created Successfully..!"));
	}

}
