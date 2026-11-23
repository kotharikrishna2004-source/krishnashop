package com.krishnashop.entity;

public enum OrderStatus {
    PENDING("Pending", "warning"),
    CONFIRMED("Confirmed", "info"),
    SHIPPED("Shipped", "primary"),
    DELIVERED("Delivered", "success"),
    CANCELLED("Cancelled", "danger");

    private final String displayName;
    private final String badgeClass;

    OrderStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
