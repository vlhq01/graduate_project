package com.example.techadvisor.service;

import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.entity.Product;
import com.example.techadvisor.entity.WorkspaceItem;
import com.example.techadvisor.mapper.ProductMapper;
import com.example.techadvisor.repository.ProductRepository;
import com.example.techadvisor.repository.WorkspaceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceItemRepository workspaceRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;


    public List<ProductDTO> getMyWorkspace(String userId) {
        List<WorkspaceItem> items = workspaceRepo.findByUserId(userId);
        List<ProductDTO> result = new ArrayList<>();

        for (WorkspaceItem item : items) {
            Optional<Product> productOpt = productRepo.findById(item.getProductId());
            if (productOpt.isPresent()) {
                result.add(productMapper.toDto(productOpt.get()));
            }
        }
        return result;
    }

    @Transactional
    public void addToWorkspace(String userId, String productId) {
        if (!workspaceRepo.existsByUserIdAndProductId(userId, productId)) {
            WorkspaceItem newItem = new WorkspaceItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setAddedAt(OffsetDateTime.now());
            workspaceRepo.save(newItem);
        }
    }

    @Transactional
    public void removeFromWorkspace(String userId, String productId) {
        workspaceRepo.deleteByUserIdAndProductId(userId, productId);
    }
}
