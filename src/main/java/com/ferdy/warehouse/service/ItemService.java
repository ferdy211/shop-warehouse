package com.ferdy.warehouse.service;

import com.ferdy.warehouse.dto.ItemRequest;
import com.ferdy.warehouse.dto.ItemResponse;
import com.ferdy.warehouse.entity.Item;
import com.ferdy.warehouse.exception.NotFoundException;
import com.ferdy.warehouse.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<ItemResponse> findAll() {
        return itemRepository.findAll().stream().map(ItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse findById(Long id) {
        return ItemResponse.from(getItem(id));
    }

    public ItemResponse create(ItemRequest request) {
        Item item = new Item();
        apply(item, request);
        return ItemResponse.from(itemRepository.save(item));
    }

    public ItemResponse update(Long id, ItemRequest request) {
        Item item = getItem(id);
        apply(item, request);
        return ItemResponse.from(item);
    }

    public void delete(Long id) {
        itemRepository.delete(getItem(id));
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item " + id + " not found"));
    }

    private void apply(Item item, ItemRequest request) {
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setStock(request.stock());
    }
}
