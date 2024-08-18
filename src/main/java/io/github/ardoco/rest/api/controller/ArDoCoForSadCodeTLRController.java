package io.github.ardoco.rest.api.controller;

//import io.github.ardoco.rest.api.repository.ArDoCoResultEntityRepository;
import io.github.ardoco.rest.api.service.ArDoCoForSadCodeTLRService;
import io.github.ardoco.rest.api.service.RunnerTLRService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.SortedMap;
import java.util.TreeMap;

@RestController
public class ArDoCoForSadCodeTLRController {

    private final RunnerTLRService sadSamCodeTLRService;

    public ArDoCoForSadCodeTLRController(ArDoCoForSadCodeTLRService sadSamCodeTLRService) {
        this.sadSamCodeTLRService = sadSamCodeTLRService;
    }


    @PostMapping("/api/sad/code/start")
    public ResponseEntity<?> runPipeline(@RequestBody String projectName, @RequestParam("inputText") MultipartFile inputText, @RequestParam("inputCode") MultipartFile inputCode) {
        try {
            //right now: no additional configs, they can later be added to the mapping as parameter.
            SortedMap<String, String> additionalConfigs = new TreeMap<>();
            long unique_id = sadSamCodeTLRService.runPipeline(projectName, inputText, inputCode, additionalConfigs);
            return ResponseEntity.ok(unique_id);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    @GetMapping("/api/sad/code/{id}")
    public ResponseEntity<String> getResult(@PathVariable("id") long id) {
        try {
            String result = sadSamCodeTLRService.getResult(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
