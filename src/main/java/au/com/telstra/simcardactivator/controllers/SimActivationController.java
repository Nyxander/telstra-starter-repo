package au.com.telstra.simcardactivator.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/sim")
public class SimActivationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody Map<String, String> request) {
        String iccid = request.get("iccid");
        String customerEmail = request.get("customerEmail");

        if (iccid == null || customerEmail == null) {
            return ResponseEntity.badRequest().body("Missing ICCID or customer email");
        }


        Map<String, String> payload = Map.of("iccid", iccid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> actuatorResponse = restTemplate.postForEntity(
                    "http://localhost:8444/actuate", httpEntity, Map.class);

            Boolean success = (Boolean) actuatorResponse.getBody().get("success");
            return ResponseEntity.ok("Activation success: " + success);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to activate SIM: " + e.getMessage());
        }
    }
}
