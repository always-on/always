package edu.wpi.always.weather;

public interface Almanac {

	public RecordTemp getRecordLow();
	
	public RecordTemp getRecordHigh();

	class RecordTemp{
		private int year;
		private int averageTemp;
		private int extremeTemp;

		public RecordTemp(int year, int averageTemp, int extremeTemp) {
			this.year = year;
			this.averageTemp = averageTemp;
			this.extremeTemp = extremeTemp;
		}

		public int getYear() {
			return year;
		}

		public int getAverageTemp() {
			return averageTemp;
		}

		public int getExtremeTemp() {
			return extremeTemp;
		}
		
		
	}
}
