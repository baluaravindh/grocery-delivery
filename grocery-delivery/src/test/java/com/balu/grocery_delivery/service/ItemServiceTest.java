package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.dto.ItemDTO;
import com.balu.grocery_delivery.entity.Item;
import com.balu.grocery_delivery.exception.ResourceNotFoundException;
import com.balu.grocery_delivery.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private ItemDTO itemDTO;

    // SETUP
    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Aashirvaad Shudh Chakki Atta");
        item.setDescription("100% pure whole wheat flour, perfect for soft rotis.");
        item.setPrice(new BigDecimal("210.00"));
        item.setStockQuantity(150);
        item.setCategory("HOUSEHOLD");
        item.setUnit("PACKET");
        item.setImageUrl("https://aashirvaadshudhchakkiaata.com");

        itemDTO = new ItemDTO();
        itemDTO.setName("Aashirvaad Shudh Chakki Atta");
        itemDTO.setDescription("100% pure whole wheat flour, perfect for soft rotis.");
        itemDTO.setPrice(new BigDecimal("210.00"));
        itemDTO.setStockQuantity(150);
        itemDTO.setCategory("HOUSEHOLD");
        itemDTO.setUnit("PACKET");
        itemDTO.setImageUrl("https://aashirvaadshudhchakkiaata.com");
    }

    // ==================== CREATE ITEMS TESTS ====================

    @Test
    @DisplayName("Should create item successfully")
    public void createItem_Successfully() {

        // ARRANGE
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // ACT
        ItemDTO result = itemService.createItem(itemDTO);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Aashirvaad Shudh Chakki Atta");
        assertThat(result.getCategory()).isEqualTo("HOUSEHOLD");
        assertThat(result.getUnit()).isEqualTo("PACKET");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("210.00"));

        // VERIFY
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    // ==================== GET ALL ITEMS TESTS ====================

    @Test
    @DisplayName("Should return all items")
    public void getAllItems_Successfully() {

        // ARRANGE
        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Amul Gold Full Cream Milk");
        item1.setDescription("Pasteurized fresh milk with high cream content.");
        item1.setPrice(new BigDecimal("66.00"));
        item1.setStockQuantity(300);
        item1.setCategory("DAIRY");
        item1.setUnit("LITRE");
        item1.setImageUrl("https://amulgoldfullcreammilk.com");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item, item1));

        // ACT
        List<ItemDTO> result = itemService.getAllItems();

        // ASSERT
        assertThat(result).hasSize(2);
        assertThat(result).isNotNull();
        assertThat(result.get(0).getName()).isEqualTo("Aashirvaad Shudh Chakki Atta");
        assertThat(result.get(1).getName()).isEqualTo("Amul Gold Full Cream Milk");

        // VERIFY
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no items exist")
    public void getAllItems_Empty() {

        // ARRANGE
        when(itemRepository.findAll()).thenReturn(Arrays.asList());

        // ACT
        List<ItemDTO> result = itemService.getAllItems();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    // ==================== GET ITEMS BY ID TESTS ====================

    @Test
    @DisplayName("Should return item when valid id provided")
    void getItemsById_Successfully() {

        // ARRANGE
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // ACT
        ItemDTO result = itemService.getItemById(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Aashirvaad Shudh Chakki Atta");
        assertThat(result.getId()).isEqualTo(1L);

        // VERIFY
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when item id not found.")
    void getItemsById_NotFound() {

        // ARRANGE
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> itemService.getItemById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with id: 99");

        // VERIFY
        verify(itemRepository, times(1)).findById(99L);
    }

    // ==================== UPDATE ITEM TESTS ====================

    @Test
    @DisplayName("Should update item successfully")
    void updateItem_Successfully() {
        itemDTO = new ItemDTO();
        item.setName("Aashirvaad Shudh Chakki Atta");
        item.setDescription("100% pure whole wheat flour, perfect for soft rotis.");
        item.setPrice(new BigDecimal("210.00"));
        item.setStockQuantity(145);
        item.setCategory("HOUSEHOLD");
        item.setUnit("PACKET");
        item.setImageUrl("https://aashirvaadshudhchakkiaata.com");

        // ARRANGE
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // ACT
        ItemDTO updated = itemService.updateItem(1L, itemDTO);

        // ASSERT
        assertThat(updated).isNotNull();

        // VERIFY
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent item")
    void updateItem_NotFound() {

        // ARRANGE
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> itemService.updateItem(99L, itemDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with id: 99");
    }

    // ==================== DELETE PRODUCT TESTS ====================

    @Test
    @DisplayName("Should delete item successfully")
    void deleteItemById_Successfully() {

        // ARRANGE
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(item);

        // ACT
        itemService.deleteItem(1L);

        // VERIFY
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent item")
    void deleteItemById_NotFound() {

        // ARRANGE
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> itemService.deleteItem(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with id: 99");

        // VERIFY
        verify(itemRepository, never()).delete(any(Item.class));
    }
}
