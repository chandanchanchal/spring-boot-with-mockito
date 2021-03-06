package com.demo.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.demo.dto.Vehicle;
import com.demo.exceptions.VehicleNotFoundException;
import com.demo.services.VehicleService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/demo")
public class VehicleController {

	@Autowired
	VehicleService vehicleService;

	@ApiOperation(value = "Retrieves a list of all vehicle records")
	@GetMapping(value = "/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Vehicle>> getAllVehicles() {

		List<Vehicle> vehicles = vehicleService.getAllVehicles();
		if (vehicles.isEmpty()) {
			throw new VehicleNotFoundException("No vehicle records were found");
		}
		return new ResponseEntity<List<Vehicle>>(vehicles, HttpStatus.OK);
	}

	@ApiOperation(value = "Retrives a single vehicle record by its VIN")
	@GetMapping(value = "/vehicles/{vin}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vehicle> getVechileByVin(@PathVariable(value = "vin") String vin) {

		Optional<Vehicle> vehicleToUpdate = vehicleService.getVehicleByVin(vin);
		if (!vehicleToUpdate.isPresent()) {
			throw new VehicleNotFoundException("Vehicle with VIN (" + vin + ") not found!");
		}
		return new ResponseEntity<Vehicle>(vehicleToUpdate.get(), HttpStatus.OK);
	}

	@ApiOperation(value = "Create a new vehicle record. JSON payload will be validated")
	@PostMapping(value = "/create/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle createVehicle) {

		Optional<Vehicle> vehicle = vehicleService.getVehicleByVin(createVehicle.getVin());
		if (vehicle.isPresent()) {
			throw new HttpClientErrorException(HttpStatus.CONFLICT,
					"Vehicle with VIN" + "(" + createVehicle.getVin() + ") already exists");
		}
		return new ResponseEntity<Vehicle>(vehicleService.createVehicle(createVehicle), HttpStatus.CREATED);
	}

	@ApiOperation(value = "Update an existing vehicle record. Will not create new record if vehicle does not already exist")
	@PutMapping(value = "/update/vehicle/{vin}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vehicle> updateVehicle(@PathVariable String vin, @Valid @RequestBody Vehicle vehicle) {

		return new ResponseEntity<Vehicle>(vehicleService.updateVehicle(vin, vehicle), HttpStatus.ACCEPTED);
	}

	@ApiOperation(value = "Delete an existing vehicle record using its VIN")
	@DeleteMapping("/vehicles/{vin}")
	public ResponseEntity<Object> deleteVehicle(@PathVariable(value = "vin") String vin) {

		vehicleService.deleteVehicle(vin);
		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
	}

}