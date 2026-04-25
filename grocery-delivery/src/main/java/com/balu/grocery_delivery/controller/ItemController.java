package com.balu.grocery_delivery.controller;

import com.balu.grocery_delivery.dto.ItemDTO;
import com.balu.grocery_delivery.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // POST
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(dto));
    }

    // GET
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    // GET /api/items/id
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    // GET /api/items/category/{category}
    @GetMapping("/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> getAllItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    // GET /api/items/search?keyword=xyz — search by name
    @GetMapping("/name/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemDTO>> getItemsByName(@RequestParam String keyword) {
        return ResponseEntity.ok(itemService.getItemsByName(keyword));
    }

    // PUT /api/items/id
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.updateItem(id, dto));
    }

    // DELETE /api/items/id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted successfully");
    }
}
