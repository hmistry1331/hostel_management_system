package com.example.hostel_managment.demo.location;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HostelLocationCatalog {
    private final Map<String, List<Integer>> floorsByBlock = Map.of(
            "A", List.of(1, 2, 3, 4, 5),
            "B", List.of(1, 2, 3, 4, 5),
            "C", List.of(1, 2, 3, 4, 5),
            "D", List.of(1, 2, 3, 4, 5));

    public Map<String, List<Integer>> floorsByBlock() {
        return floorsByBlock;
    }
}
