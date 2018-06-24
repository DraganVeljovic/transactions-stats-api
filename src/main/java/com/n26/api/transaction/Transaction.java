package com.n26.api.transaction;

public class Transaction {
	private double amount;
	private long timestamp;

	public Transaction(double amount, long timestamp) {
		super();
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public Transaction() {
		super();
		this.amount = 0.0;
		this.timestamp = System.currentTimeMillis();
	}

	public double getAmount() {
		return amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
