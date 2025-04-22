package com.example.bordados.model.Enums;

public enum Color {
    WHITE, BLACK, BLUE, RED, GREEN, YELLOW, ORANGE, PINK, PURPLE, GRAY, 
    CYAN, BEIGE;

    public String getColorCode() {
        return switch (this) {
            case WHITE -> "#FFFFFF";
            case BLACK -> "#000000";
            case BLUE -> "#0000FF";
            case RED -> "#FF0000";
            case GREEN -> "#00FF00";
            case YELLOW -> "#FFFF00";
            case ORANGE -> "#FFA500";
            case PINK -> "#FFC0CB";
            case PURPLE -> "#800080";
            case GRAY -> "#808080";
            case CYAN -> "#00FFFF";
            case BEIGE -> "#F5F5DC"; 
            default -> "#FFFFFF";
        };
    }
}
