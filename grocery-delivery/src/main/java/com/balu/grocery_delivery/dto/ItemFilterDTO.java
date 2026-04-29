package com.balu.grocery_delivery.dto;

import lombok.Data;

@Data
public class ItemFilterDTO {

    // Pagination
    private int page = 0;
    private int size = 10;

    // Sorting
    private String sortBy = "id";
    private String direction = "asc";

    // Filtering
    private String keyword;
    private String category;
    private Double minPrice;
    private Double maxPrice;
}
