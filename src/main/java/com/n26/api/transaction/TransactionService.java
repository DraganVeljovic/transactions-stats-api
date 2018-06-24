package com.n26.api.transaction;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TransactionService implements Runnable {
	private class TQueueItem {
		public double amount;
		public long timestamp;
		public int count;

		public TQueueItem(double amount, long timestamp, int count) {
			this.amount = amount;
			this.timestamp = timestamp;
			this.count = count;
		}
	}

	/*
	 * Buffer for transactions
	 */
	private ConcurrentLinkedQueue<TQueueItem> transactions = new ConcurrentLinkedQueue<TQueueItem>();

	private TransactionStats stats = new TransactionStats(0, 0, 0, 0, 0);

	private long statisticsValidFor;
	private long checkInterval;

	public TransactionService(long statisticsValidFor, long checkInterval) {
		this.statisticsValidFor = statisticsValidFor;
		this.checkInterval = checkInterval;
	}

	public TransactionStats getStats() {
		synchronized (stats) {
			return stats;
		}
	}

	private void updateStats(TQueueItem tqItem) {
		synchronized (stats) {
			long count = stats.getCount() + tqItem.count;
			if (count == 0) {
				stats.resetAll();
			} else {
				double sum = stats.getSum() + tqItem.amount;
				double avg = count == 0 ? stats.getAvg() * stats.getCount() + tqItem.amount
						: (stats.getAvg() * stats.getCount() + tqItem.amount) / (double) (count);

				stats.setSum(sum);
				stats.setAvg(avg);
				stats.setCount(count);

				if (tqItem.count != -1) {
					/*
					 * count != -1 means that we are processing "real" transaction so we update min
					 * / max values
					 *
					 * if the fist transaction in statistics window min = max = amount
					 */
					if (count == 1) {
						stats.setMax(tqItem.amount);
						stats.setMin(tqItem.amount);
					} else {
						if (tqItem.amount > stats.getMax()) {
							stats.setMax(tqItem.amount);
						}
						if (tqItem.amount < stats.getMin()) {
							stats.setMin(tqItem.amount);
						}
					}
				}
			}
		}
	}

	public boolean isTransactionValid(Transaction transaction) {
		long diffTime = System.currentTimeMillis() - transaction.getTimestamp();
		if (diffTime > 0 && diffTime < statisticsValidFor) {
			return true;
		}
		return false;
	}

	public void addTransaction(Transaction transaction) {
		TQueueItem newItem = new TQueueItem(transaction.getAmount(), transaction.getTimestamp(), 1);
		/*
		 * annualItem is "magic" to annul transaction effect on statistics after 60s
		 * We schedule counter transaction (negative amount) to be "executed" in 60s
		 * and in that way keep the statistics in right shape
		 *
		 * These transactions have count = -1
		 */
		TQueueItem annulItem = new TQueueItem(-transaction.getAmount(), transaction.getTimestamp() + statisticsValidFor, -1);

		/*
		 * Here we can push transaction to another service which will store them
		 * and do some further processing
		 *
		 * pushTransaction(transaction)
		 */

		transactions.add(newItem);
		transactions.add(annulItem);
	}

	private void pushBack(TQueueItem tqItem) {
		transactions.add(tqItem);
	}

	@Override
	public void run() {
		/*
		 * In separate thread we are checking for the transactions in buffer
		 * and "execute" them to be able to offer the statistics
		 */
		while(true) {
			TQueueItem transaction = transactions.poll();
			if (transaction != null) {
				if (transaction.timestamp > System.currentTimeMillis()) {
					pushBack(transaction);
				} else {
					updateStats(transaction);
				}
			} else {
				try {
					Thread.sleep(this.checkInterval);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
