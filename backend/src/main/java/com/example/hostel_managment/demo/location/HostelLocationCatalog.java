package com.example.hostel_managment.demo.location;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HostelLocationCatalog {
    private final Map<String, List<Integer>> floorsByBlock = Map.ofEntries(
            Map.entry("A", List.of(1, 2, 3, 4)),
            Map.entry("B", List.of(1, 2, 3, 4)),
            Map.entry("C", List.of(1, 2, 3, 4)),
            Map.entry("D", List.of(1, 2, 3, 4)),
            Map.entry("E", List.of(1, 2, 3, 4)),
            Map.entry("F", List.of(1, 2, 3, 4)),
            Map.entry("G", List.of(1, 2, 3, 4)),
            Map.entry("H", List.of(1, 2, 3, 4)),
            Map.entry("I", List.of(1, 2, 3, 4)),
            Map.entry("J", List.of(1, 2, 3, 4)),
            Map.entry("K", List.of(1, 2, 3, 4)),
            Map.entry("L", List.of(1, 2, 3, 4)),
            Map.entry("M", List.of(1, 2, 3, 4)),
            Map.entry("N", List.of(1, 2, 3, 4)));

    public Map<String, List<Integer>> floorsByBlock() {
        return floorsByBlock;
    }
}
