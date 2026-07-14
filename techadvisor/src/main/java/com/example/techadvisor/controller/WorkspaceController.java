package com.example.techadvisor.controller;

import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        }
        return (String) authentication.getPrincipal();
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getMyWorkspace() {
        String userId = getCurrentUserId();
        List<ProductDTO> items = workspaceService.getMyWorkspace(userId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> addToWorkspace(@PathVariable String productId) {
        String userId = getCurrentUserId();
        workspaceService.addToWorkspace(userId, productId);
        return ResponseEntity.ok("Added to workspace successfully");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromWorkspace(@PathVariable String productId) {
        String userId = getCurrentUserId();
        workspaceService.removeFromWorkspace(userId, productId);
        return ResponseEntity.ok("Removed from workspace successfully");
    }
}
