package com.n26.api;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.n26.api.transaction.TransactionController;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private TransactionController transactionController;

	@Test
	public void getStatisticsBeforeTransaction() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/statistics");

		mvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	public void submitValidTransaction() throws Exception {
		long timestamp = System.currentTimeMillis();
		String json = "{\"amount\": 2.2, \"timestamp\": " + timestamp + "}";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
				"/transactions").contentType(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void submitValidTransactions() throws Exception {
		long timestamp = System.currentTimeMillis();
		String jsons[] = {
				"{\"amount\": 10.2, \"timestamp\": " + timestamp + "}",
				"{\"amount\": 30.3, \"timestamp\": " + timestamp + "}",
				"{\"amount\": -15.7, \"timestamp\": " + timestamp + "}",
				"{\"amount\": 0.25, \"timestamp\": " + timestamp + "}",
				"{\"amount\": -5.67, \"timestamp\": " + timestamp + "}",
				};

		for(String json: jsons) {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
					"/transactions").contentType(MediaType.APPLICATION_JSON).content(json);

			mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
		}

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/statistics");

		mvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andDo(print());
	}
}
