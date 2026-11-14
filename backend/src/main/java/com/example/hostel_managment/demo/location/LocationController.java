package com.example.hostel_managment.demo.location;

import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final HostelLocationCatalog catalog;

    public LocationController(HostelLocationCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public Map<String, List<Integer>> locations() {
        return catalog.floorsByBlock();
    }
}
