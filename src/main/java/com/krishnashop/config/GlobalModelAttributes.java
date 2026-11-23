package com.krishnashop.config;

import com.krishnashop.entity.Category;
import com.krishnashop.entity.User;
import com.krishnashop.repository.CategoryRepository;
import com.krishnashop.repository.UserRepository;
import com.krishnashop.security.CustomUserDetails;
import com.krishnashop.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public GlobalModelAttributes(CategoryRepository categoryRepository, UserRepository userRepository, CartService cartService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @ModelAttribute("allCategories")
    public List<Category> populateCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @ModelAttribute
    public void populateUserAndCart(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            model.addAttribute("currentUser", userDetails);
            try {
                User user = userRepository.findById(userDetails.getId()).orElse(null);
                if (user != null) {
                    int cartCount = cartService.getCartItemCount(user);
                    model.addAttribute("cartItemCount", cartCount);
                } else {
                    model.addAttribute("cartItemCount", 0);
                }
            } catch (Exception e) {
                model.addAttribute("cartItemCount", 0);
            }
        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("cartItemCount", 0);
        }
    }
}
