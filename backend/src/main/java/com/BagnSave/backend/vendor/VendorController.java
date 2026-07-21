package com.BagnSave.backend.vendor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BagnSave.backend.vendor.dto.VendorDTO;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
//@CrossOrigin(origins = "*") // To-Do: tighten to vercel URL
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public List<VendorDTO> getAllVendors() {
        return vendorService.getAllVendors();
    }
}
