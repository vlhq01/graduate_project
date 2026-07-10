package com.example.techadvisor.config;

import com.example.techadvisor.client.AiServiceClient;
import com.example.techadvisor.entity.Product;
import com.example.techadvisor.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AiServiceClient aiServiceClient; // Tiêm Client vào đây

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            System.out.println("⏳ Đang bơm dữ liệu từ products_test.json vào Database...");

            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = TypeReference.class.getResourceAsStream("/products_test.json");

            try {
                List<Product> products = mapper.readValue(inputStream, new TypeReference<List<Product>>(){});
                productRepository.saveAll(products);
                System.out.println("✅✅✅ BƠM DATA THÀNH CÔNG! Đã lưu " + products.size() + " sản phẩm vào DB!");

                // --- ĐOẠN MA THUẬT MỚI THÊM VÀO ---
                System.out.println("🤖 Đang gọi AI Server để tạo Vector Embedding (Vui lòng chờ 1-2 phút)...");
                try {
                    Map<String, Object> aiResponse = aiServiceClient.syncEmbeddings();
                    System.out.println("🚀 AI Server trả lời: " + aiResponse.get("message"));
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi gọi AI nhúng Vector (Hãy chắc chắn Python đang chạy): " + e.getMessage());
                }
                // -----------------------------------

            } catch (Exception e) {
                System.err.println("❌ Lỗi khi bơm data: " + e.getMessage());
            }
        } else {
            System.out.println("👍 Database đã có hàng, bỏ qua bước bơm data.");
            aiServiceClient.syncEmbeddings();
        }
    }
}