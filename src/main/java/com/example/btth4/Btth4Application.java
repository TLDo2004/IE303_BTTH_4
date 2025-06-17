package com.example.btth4;

import com.example.btth4.model.Product;
import com.example.btth4.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class Btth4Application {

    public static void main(String[] args) {
        SpringApplication.run(Btth4Application.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream("product-info.txt");
                if (is == null) {
                    return;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                String name = null, brand = null, description = null, image = null;
                double price = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("image ")) {
                        image = "img" + line.replace("image ", "").replace(":", "") + ".png";
                    } else if (line.startsWith("- Name:")) {
                        name = line.replace("- Name:", "").trim();
                    } else if (line.startsWith("- Price:")) {
                        String priceStr = line.replace("- Price:", "").replace(".", "").replace(",", ".").trim();
                        try {
                            price = Double.parseDouble(priceStr);
                        } catch (Exception e) {
                            price = 0;
                        }
                    } else if (line.startsWith("- Brand:")) {
                        brand = line.replace("- Brand:", "").trim();
                    } else if (line.startsWith("- Description:")) {
                        description = line.replace("- Description:", "").trim();
                        // Khi đã đủ thông tin, thêm sản phẩm
                        if (name != null && brand != null && description != null && image != null) {
                            Product product = new Product(null, name, price, brand, description, image);
                            productRepository.save(product);
                            name = brand = description = image = null;
                            price = 0;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}
