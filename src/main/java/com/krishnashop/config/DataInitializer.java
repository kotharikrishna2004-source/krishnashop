package com.krishnashop.config;

import com.krishnashop.entity.*;
import com.krishnashop.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           CartRepository cartRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCatalog();
    }

    private void seedUsers() {
        // 1. Seed Admin
        if (userRepository.findByEmail("admin@krishnashop.com").isEmpty()) {
            User admin = new User(
                    "Krishna Shop Admin",
                    "admin@krishnashop.com",
                    passwordEncoder.encode("admin123"),
                    "9876543210",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            cartRepository.save(new Cart(admin));
            System.out.println(">>> Initialized default Admin: admin@krishnashop.com / admin123");
        }

        // 2. Seed Default Demo Customer
        if (userRepository.findByEmail("user@krishnashop.com").isEmpty()) {
            User customer = new User(
                    "Rahul Sharma",
                    "user@krishnashop.com",
                    passwordEncoder.encode("user123"),
                    "9123456789",
                    Role.ROLE_USER
            );
            userRepository.save(customer);
            cartRepository.save(new Cart(customer));
            System.out.println(">>> Initialized default Demo User: user@krishnashop.com / user123");
        }
    }

    private void seedCatalog() {
        if (categoryRepository.count() > 0) {
            return; // Catalog already initialized
        }

        // Seed Categories
        Category electronics = new Category("Electronics", "Smartphones, laptops, headphones, smart devices & accessories",
                "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500&auto=format&fit=crop&q=80");
        Category fashion = new Category("Fashion & Apparel", "Trendy men's and women's apparel, footwear & accessories",
                "https://images.unsplash.com/photo-1445205170230-053b83016050?w=500&auto=format&fit=crop&q=80");
        Category home = new Category("Home & Kitchen", "Cookware, appliances, bedding, home decor & organization",
                "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500&auto=format&fit=crop&q=80");
        Category books = new Category("Books & Stationery", "Bestselling novels, self-help, business, academic & stationery",
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500&auto=format&fit=crop&q=80");
        Category beauty = new Category("Beauty & Personal Care", "Skincare, cosmetics, grooming products & wellness",
                "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500&auto=format&fit=crop&q=80");
        Category sports = new Category("Sports & Fitness", "Gym equipment, athletic wear, outdoor gear & sports accessories",
                "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=500&auto=format&fit=crop&q=80");

        categoryRepository.saveAll(List.of(electronics, fashion, home, books, beauty, sports));

        // Seed Rich Products
        Product p1 = new Product("Sony WH-1000XM5 Wireless Noise Cancelling Headphones",
                "Industry-leading noise cancellation with two processors and 8 microphones. Up to 30 hours battery life with quick charge. Ultra-comfortable lightweight design with soft fit leather.",
                29990.0, 34990.0, 25,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=700&auto=format&fit=crop&q=80",
                electronics);
        p1.setBestSeller(true);
        p1.setFeatured(true);
        p1.setRating(4.8);
        p1.setReviewCount(342);

        Product p2 = new Product("Apple iPhone 15 (128 GB) - Blue",
                "Dynamic Island bubbles up alerts. 48MP main camera for super-high-resolution photos. Durable color-infused glass and aluminum design with USB-C connector.",
                69900.0, 79900.0, 15,
                "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=700&auto=format&fit=crop&q=80",
                electronics);
        p2.setTrending(true);
        p2.setFeatured(true);
        p2.setRating(4.9);
        p2.setReviewCount(890);

        Product p3 = new Product("Apple MacBook Air 13.6-inch M3 Chip (16GB RAM, 512GB SSD)",
                "Strikingly thin and fast laptop with Apple M3 chip. Liquid Retina display, 1080p FaceTime HD camera, MagSafe 3 charging and up to 18 hours of battery life.",
                114900.0, 124900.0, 10,
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=700&auto=format&fit=crop&q=80",
                electronics);
        p3.setNewArrival(true);
        p3.setRating(4.9);
        p3.setReviewCount(142);

        Product p4 = new Product("Men's Premium Slim Fit Oxford Cotton Casual Shirt",
                "100% Breathable combed cotton casual button-down shirt. Perfect for everyday office wear, evening parties, and weekend outings. Machine washable.",
                1499.0, 2999.0, 60,
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=700&auto=format&fit=crop&q=80",
                fashion);
        p4.setBestSeller(true);
        p4.setRating(4.5);
        p4.setReviewCount(520);

        Product p5 = new Product("Women's Handcrafted Pure Silk Embroidered Kurta Set",
                "Exquisite festive ensemble featuring intricate Zari work, matched with flared palazzos and a lightweight organza dupatta. Ideal for weddings and festivals.",
                3299.0, 6599.0, 30,
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=700&auto=format&fit=crop&q=80",
                fashion);
        p5.setTrending(true);
        p5.setRating(4.7);
        p5.setReviewCount(215);

        Product p6 = new Product("Handcrafted Italian Leather Formal Derby Shoes",
                "Hand-finished genuine leather formal shoes with cushioned memory foam insole and slip-resistant rubber outsole for all-day comfort.",
                3999.0, 6999.0, 20,
                "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=700&auto=format&fit=crop&q=80",
                fashion);
        p6.setRating(4.6);
        p6.setReviewCount(88);

        Product p7 = new Product("Philips Digital Air Fryer HD9252 with Rapid Air Tech",
                "Fry with up to 90% less fat. Touch screen with 7 presets including keep-warm function. Dishwasher-safe removable non-stick basket. 4.1L capacity.",
                7499.0, 10995.0, 18,
                "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=700&auto=format&fit=crop&q=80",
                home);
        p7.setTrending(true);
        p7.setFeatured(true);
        p7.setRating(4.7);
        p7.setReviewCount(670);

        Product p8 = new Product("Prestige Royale Plus 3-Burner Toughened Glass Gas Stove",
                "Spill-proof design with German tech Schott glass top, tri-pin brass burners, and ergonomic knobs. Adds elegance and durability to your modern kitchen.",
                4899.0, 7295.0, 25,
                "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=700&auto=format&fit=crop&q=80",
                home);
        p8.setBestSeller(true);
        p8.setRating(4.4);
        p8.setReviewCount(430);

        Product p9 = new Product("Atomic Habits by James Clear (Hardcover Edition)",
                "An Easy & Proven Way to Build Good Habits & Break Bad Ones. Over 15 million copies sold worldwide. Transform your daily routine step by step.",
                599.0, 899.0, 100,
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=700&auto=format&fit=crop&q=80",
                books);
        p9.setBestSeller(true);
        p9.setFeatured(true);
        p9.setRating(4.9);
        p9.setReviewCount(2400);

        Product p10 = new Product("The Psychology of Money by Morgan Housel",
                "Timeless lessons on wealth, greed, and happiness doing well with money isn't necessarily about what you know. It's about how you behave.",
                349.0, 499.0, 80,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=700&auto=format&fit=crop&q=80",
                books);
        p10.setTrending(true);
        p10.setRating(4.8);
        p10.setReviewCount(1850);

        Product p11 = new Product("L'Oreal Paris Revitalift 1.5% Hyaluronic Acid Face Serum",
                "Intensive hydrating serum with macro and micro hyaluronic acid. Visibly plumps skin, reduces fine lines by 60% and adds instant radiant glow.",
                799.0, 999.0, 45,
                "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=700&auto=format&fit=crop&q=80",
                beauty);
        p11.setBestSeller(true);
        p11.setRating(4.6);
        p11.setReviewCount(940);

        Product p12 = new Product("Boldfit NBR Yoga Mat with Alignment Lines & Carrying Strap (10mm)",
                "High density non-slip cushioning protects joints and spine during floor workouts, pilates, and yoga poses. Waterproof, sweat-resistant, lightweight.",
                899.0, 1799.0, 40,
                "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=700&auto=format&fit=crop&q=80",
                sports);
        p12.setNewArrival(true);
        p12.setTrending(true);
        p12.setRating(4.6);
        p12.setReviewCount(310);

        productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12));
        System.out.println(">>> Initialized 6 categories and 12 rich products for Krishna Shop!");
    }
}
