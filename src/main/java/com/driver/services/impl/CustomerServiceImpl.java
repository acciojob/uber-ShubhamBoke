package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> driverList = driverRepository2.findAll();
		PriorityQueue<Driver> pq = new PriorityQueue<>((a,b) -> {
			return a.getDriverId() - b.getDriverId();
		});
		for(Driver d: driverList){
			if(d.getCab().isAvailable())
				pq.add(d);
		}
		if(pq.size() == 0) throw new Exception("No cab available!");

		Driver driver = pq.remove();
		Optional<Customer> customerOptional = customerRepository2.findById(customerId);
		Customer customer = customerOptional.get();

		TripBooking tripBooking = new TripBooking();

		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setDriver(driver);
		tripBooking.setCustomer(customer);

		driver.getCab().setAvailable(false);
		driver.getTripBookingList().add(tripBooking);

		customer.getTripBookingList().add(tripBooking);

		return tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBookingOptional = tripBookingRepository2.findById(tripId);
		TripBooking tripBooking = tripBookingOptional.get();

		Cab cab = tripBooking.getDriver().getCab();

		tripBooking.setStatus(TripStatus.CANCELED);
		cab.setAvailable(true);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBookingOptional = tripBookingRepository2.findById(tripId);
		TripBooking tripBooking = tripBookingOptional.get();

		Cab cab = tripBooking.getDriver().getCab();

		tripBooking.setStatus(TripStatus.COMPLETED);
		cab.setAvailable(true);
		tripBookingRepository2.save(tripBooking);


	}
}
