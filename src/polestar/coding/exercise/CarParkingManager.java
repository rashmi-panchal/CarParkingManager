package polestar.coding.exercise;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class CarParkingManager {
	
	//A threadsafe int initialized to 4999 as we always increment the ticket number before using it.
	private AtomicInteger ticketNumber = new AtomicInteger(4999);
	private AtomicReferenceArray<Car> carParking = new AtomicReferenceArray<Car>(10);
	

	/**
	 * Parks the car in an available space
	 * @param carId - unique identifier for the car
	 * @return Parking ticket number if parking space is available, 0 otherwise.
	 */
	private synchronized int parkCar(String carId) {
		int assignedTicketNumber = 0;
		for(int carSpace = 0; carSpace < carParking.length(); carSpace++) {
			if(carParking.get(carSpace) == null) {
				assignedTicketNumber = ticketNumber.incrementAndGet();
				Car car = new Car(carId, assignedTicketNumber);
				carParking.set(carSpace, car);
				break;
			}
		}
		return assignedTicketNumber;
	}
	
	
	/**
	 * Unparks the car given its ticket number
	 * @param ticketNumber - the ticket number assigned to the car when it was parked
	 */
	private synchronized void unparkCar(int ticketNumber) {
		for(int carSpace = 0; carSpace < carParking.length(); carSpace++) {
			Car car = carParking.get(carSpace);
			if(car != null && car.getTicketNumber() == ticketNumber) {
				carParking.set(carSpace, null);
				break;
			}
		}
	}
	
	/**
	 * Moves all the cars towards the entrance of parking (or towards the beginning of the car park)
	 */
	private synchronized void compact() {
		int lastIndex = carParking.length()-1;
		int firstIndex = 0;

		while(firstIndex <= lastIndex) {
			Car car = carParking.get(firstIndex);
			if(car == null) {
				if(carParking.get(lastIndex) != null) {
					carParking.set(firstIndex,carParking.get(lastIndex));
					carParking.set(lastIndex, null);
					firstIndex++;
					lastIndex--;
				}
				else {
					lastIndex--;
				}
			}
			else {
				firstIndex++;
			}
		}	
	}
	
	/**
	 * Provides the current state of car park - which car is parked where
	 * @return a string denoting the current state of the car park
	 */
	public synchronized String getResult() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		while(i < carParking.length()-1) {
			Car car = carParking.get(i);
			result.append(car == null ? ",": car.getCarId()+",");
			i++;
		}
		
		Car lastCar = carParking.get(carParking.length()-1);
		if(lastCar != null) {
			result.append(lastCar);
		}

		return new String(result);
	}

	public static void main(String[] args) {
		if (ArrayUtils.isNotEmpty(args)) {
			Arrays.stream(args).filter(StringUtils::isNotBlank).forEach(actions -> {
				CarParkingManager carParkingManager = new CarParkingManager();
				Stream.of(StringUtils.split(actions, ',')).filter(StringUtils::isNotBlank).forEach(action -> {
					final char performAction = action.charAt(0);
					
					switch(performAction) {
						case 'p' : 
							final String carId = action.substring(1);
							carParkingManager.parkCar(carId);
							break;
						case 'u' : 
							final String ticketNumber = action.substring(1);
							carParkingManager.unparkCar(NumberUtils.toInt(ticketNumber));
							break;
						case 'c' :
							carParkingManager.compact();
							break;
						default :
							System.out.println("Invalid action: " + performAction);
					}
				});
				
				System.out.println("Current Car Park Status " + carParkingManager.getResult());
			});
		}
	}

}
