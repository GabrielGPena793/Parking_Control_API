package com.api.parkingcontrol.repositories;

import com.api.parkingcontrol.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID> {

    boolean existsByLicensePlateCar(String licensePlateCar);

    boolean existsByParkingSpotNumber(String parkingSPorNumber);

    boolean existsByApartmentAndBlock(String apartment, String block);
}
