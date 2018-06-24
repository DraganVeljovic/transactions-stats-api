package com.n26.api.transaction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	private Thread serviceThread;
	private TransactionService service = new TransactionService(
			60 * 1000 /* time in ms */,
			50 /* time in ms*/);

	@PostConstruct
	public void init() {
		serviceThread = new Thread(service);
		serviceThread.start();
	}

	@PreDestroy
	public void cleanUp() {
		serviceThread.interrupt();
	}

	@PostMapping("/transactions")
	public ResponseEntity<Void> transactions(@RequestBody Transaction transaction) {
		long now = System.currentTimeMillis();
		logger.debug("Incoming transaction,"
				+ " amount " + transaction.getAmount()
				+ " timestamp " + transaction.getTimestamp()
				+ " current timestamp " + now);

		if (service.isTransactionValid(transaction)) {
			logger.debug("Add transaction,"
					+ " amount " + transaction.getAmount()
					+ " timestamp " + transaction.getTimestamp()
					+ " current timestamp " + now);

			service.addTransaction(transaction);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/statistics")
	public ResponseEntity<TransactionStats> statistics() {
		TransactionStats stats = service.getStats();

		logger.debug("Get statitics,"
				+ " sum: " + stats.getSum()
				+ " avg: " + stats.getAvg()
				+ " max: " + stats.getMax()
				+ " min: " + stats.getMin()
				+ " cnt: " + stats.getCount());

		return new ResponseEntity<TransactionStats>(stats, HttpStatus.OK);
	}
}