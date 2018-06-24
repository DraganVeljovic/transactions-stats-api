package com.n26.api.transaction;

public class TransactionStats {
		private double sum;
		private double avg;
		private double max;
		private double min;
		private long count;

		public TransactionStats(double sum, double avg, double max, double min, long count) {
			super();
			this.sum = sum;
			this.avg = avg;
			this.max = max;
			this.min = min;
			this.count = count;
		}

		public void resetAll() {
			setSum(0);
			setAvg(0);
			setMax(0);
			setMin(0);
			setCount(0);
		}

		public double getSum() {
			return sum;
		}

		public double getAvg() {
			return avg;
		}

		public double getMax() {
			return max;
		}

		public double getMin() {
			return min;
		}

		public long getCount() {
			return count;
		}

		public void setSum(double sum) {
			this.sum = sum;
		}

		public void setAvg(double avg) {
			this.avg = avg;
		}

		public void setMax(double max) {
			this.max = max;
		}

		public void setMin(double min) {
			this.min = min;
		}

		public void setCount(long count) {
			this.count = count;
		}
	}