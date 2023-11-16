package com.example.juco;

import org.json.JSONException;
import org.json.JSONObject;

public class Producto {
    private String cotizacion_info_id;
    private String productName;
    private String color;
    private String modelo;
    private double large;
    private double width;
    private double squareMeter;
    private double pricePerSquareMeter;
    private double total;
    private double discount;

    public Producto(String cotizacion_info_id, String productName, String color, String modelo, double large, double width, double squareMeter, double pricePerSquareMeter, double total, double discount) {
        this.cotizacion_info_id = cotizacion_info_id;
        this.productName = productName;
        this.color = color;
        this.modelo = modelo;
        this.large = large;
        this.width = width;
        this.squareMeter = squareMeter;
        this.pricePerSquareMeter = pricePerSquareMeter;
        this.total = total;
        this.discount = discount;
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

    public double getDiscount() { return discount;}

    public JSONObject toJson() {
        JSONObject jsonProducto = new JSONObject();
        try {
            jsonProducto.put("cotizacion_info_id", cotizacion_info_id);
            jsonProducto.put("productName", productName);
            jsonProducto.put("color", color);
            jsonProducto.put("modelo", modelo);
            jsonProducto.put("large", large);
            jsonProducto.put("width", width);
            jsonProducto.put("squareMeter", squareMeter);
            jsonProducto.put("pricePerSquareMeter", pricePerSquareMeter);
            jsonProducto.put("total", total);
            jsonProducto.put("discount", discount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonProducto;
    }

    public String getCotizacion_info_id() {
        return cotizacion_info_id;
    }

    // Método para convertir el objeto Producto a un objeto JSONObject

}
