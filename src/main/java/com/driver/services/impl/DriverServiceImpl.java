package com.driver.services.impl;

import com.driver.model.Cab;
import com.driver.repository.CabRepository;
import com.driver.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Driver;
import com.driver.repository.DriverRepository;

import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	DriverRepository driverRepository3;

	@Autowired
	CabRepository cabRepository3;

	@Override
	public void register(String mobile, String password){
		//Save a driver in the database having given details and a cab with ratePerKm as 10 and availability as True by default.
		Driver driver = new Driver();

		driver.setMobile(mobile);
		driver.setPassword(password);
		driver = driverRepository3.save(driver);

		Cab cab = new Cab();
		cab.setAvailable(true);
		cab.setPerKmRate(10);
		cab = cabRepository3.save(cab);

		cab.setDriver(driver);
		driver.setCab(cab);

		cabRepository3.save(cab);
		driverRepository3.save(driver);

	}

	@Override
	public void removeDriver(int driverId){
		// Delete driver without using deleteById function
		driverRepository3.deleteById(driverId);
	}

	@Override
	public void updateStatus(int driverId){
		//Set the status of respective car to unavailable
		Optional<Driver> driverOptional = driverRepository3.findById(driverId);
		if(!driverOptional.isPresent()) return;
		Driver driver = driverOptional.get();
		driver.getCab().setAvailable(false);
	}
}
