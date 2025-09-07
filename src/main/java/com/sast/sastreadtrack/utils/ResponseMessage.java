package com.sast.sastreadtrack.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {
    public static ResponseEntity<Map<String, Object>> ok() {
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity<Map<String, Object>> ok(String message) {
        return of(HttpStatus.OK, message);
    }

    public static ResponseEntity<Map<String, Object>> ok(Map<String, Object> res) {
        res.put("status", 200);
        return ResponseEntity.ok().body(res);
    }

    public static ResponseEntity<Map<String, Object>> of(HttpStatus status) {
        return ResponseEntity.status(status).build();
    }

    public static ResponseEntity<Map<String, Object>> of(HttpStatus status, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", status.value());
        res.put("message", message);
        return ResponseEntity.status(status).body(res);
    }
}
