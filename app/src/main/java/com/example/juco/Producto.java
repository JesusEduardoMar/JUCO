package com.example.juco;

import org.json.JSONException;
import org.json.JSONObject;

public class Producto {
    private String productName;
    private String color;
    private String modelo;
    private double large;
    private double width;
    private double squareMeter;
    private double pricePerSquareMeter;
    private double total;

    public Producto(String productName, String color, String modelo, double large, double width, double squareMeter, double pricePerSquareMeter, double total) {
        this.productName = productName;
        this.color = color;
        this.modelo = modelo;
        this.large = large;
        this.width = width;
        this.squareMeter = squareMeter;
        this.pricePerSquareMeter = pricePerSquareMeter;
        this.total = total;
    }

    // Getter methods

    public String getProductName() {
        return productName;
    }

    public String getColor() {
        return color;
    }

    public String getModelo() {
        return modelo;
    }

    public double getLarge() {
        return large;
    }

    public double getWidth() {
        return width;
    }

    public double getSquareMeter() {
        return squareMeter;
    }

    public double getPricePerSquareMeter() {
        return pricePerSquareMeter;
    }

    public double getTotal() {
        return total;
    }

    // Método para convertir el objeto Producto a un objeto JSONObject
    public JSONObject toJson() {
        JSONObject jsonProducto = new JSONObject();
        try {
            jsonProducto.put("productName", productName);
            jsonProducto.put("color", color);
            jsonProducto.put("modelo", modelo);
            jsonProducto.put("large", large);
            jsonProducto.put("width", width);
            jsonProducto.put("squareMeter", squareMeter);
            jsonProducto.put("pricePerSquareMeter", pricePerSquareMeter);
            jsonProducto.put("total", total);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonProducto;
    }
}
