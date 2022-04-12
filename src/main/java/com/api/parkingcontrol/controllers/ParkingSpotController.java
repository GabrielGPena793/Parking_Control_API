package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dto.ParkingSportDTO;
import com.api.parkingcontrol.model.ParkingSpot;
import com.api.parkingcontrol.services.ParkingSportService;
import net.bytebuddy.TypeCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    @Autowired
    private ParkingSportService parkingSportService;

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSportDTO parkingSportDTO){
        if (parkingSportService.existsByLicensePlateCar(parkingSportDTO.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License plate car already in use!");
        }
        if (parkingSportService.existsByParkingSpotNumber(parkingSportDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking sport already in use!");
        }
        if (parkingSportService.existsByApartmentAndBlock(parkingSportDTO.getApartment(), parkingSportDTO.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking sport already registered for this apartment/block");
        }

        var parkingSpotModel = new ParkingSpot();
        BeanUtils.copyProperties(parkingSportDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSportService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpot>> getAllParkingSpot(@PageableDefault(page = 0, size = 10, sort = "id",
                                                                direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSportService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable UUID id){
        Optional<ParkingSpot> parkingSpotModelOptional = parkingSportService.findById(id);
        if (parkingSpotModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable UUID id){
        Optional<ParkingSpot> parkingSpotModelOptional = parkingSportService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }

        parkingSportService.deleteById(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully");
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable UUID id, @RequestBody @Valid ParkingSportDTO parkingSportDTO) {
        Optional<ParkingSpot> parkingSpotModelOptional = parkingSportService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }

        //FORMA DE ATUALIZA OS DADOS CONVERTENDO OQ VEM PARA UM NOVO MODEL
        var parkingSpotModel = new ParkingSpot();
        BeanUtils.copyProperties(parkingSportDTO, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK).body(parkingSportService.save(parkingSpotModel));

    }


}
