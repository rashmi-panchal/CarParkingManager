package polestar.coding.exercise;

public class Car {

	private final String carId;
	private final int ticketNumber;
	
	public Car(String carId, int ticketNumber) {
		this.carId = carId;
		this.ticketNumber = ticketNumber;
	}
	
	public String getCarId() {
		return carId;
	}
	public int getTicketNumber() {
		return ticketNumber;
	}	
}
