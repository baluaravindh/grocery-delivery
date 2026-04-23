package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.dto.ItemDTO;
import com.balu.grocery_delivery.entity.Item;
import com.balu.grocery_delivery.exception.ResourceNotFoundException;
import com.balu.grocery_delivery.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemDTO createItem(ItemDTO dto) {
        Item item = mapToEntity(dto);
        Item saved = itemRepository.save(item);
        return mapToDto(saved);
    }

    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return mapToDto(item);
    }

    public List<ItemDTO> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDTO> getItemsByName(String keyword) {
        return itemRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ItemDTO updateItem(Long id, ItemDTO dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setStockQuantity(dto.getStockQuantity());
        item.setCategory(dto.getCategory());
        item.setUnit(dto.getUnit());
        item.setImageUrl(dto.getImageUrl());

        Item updated = itemRepository.save(item);
        return mapToDto(updated);
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        itemRepository.delete(item);
    }

    private ItemDTO mapToDto(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setStockQuantity(item.getStockQuantity());
        dto.setCategory(item.getCategory());
        dto.setUnit(item.getUnit());
        dto.setImageUrl(item.getImageUrl());
        return dto;
    }

    private Item mapToEntity(ItemDTO dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setStockQuantity(dto.getStockQuantity());
        item.setCategory(dto.getCategory());
        item.setUnit(dto.getUnit());
        item.setImageUrl(dto.getImageUrl());
        return item;
    }
}
