package com.example.juco;

public class Cotizacion {
    private String cotizacion_info_id;


    public Cotizacion(String cotizacion_info_id) {
        this.cotizacion_info_id = cotizacion_info_id;
    }

    public Cotizacion() {

    }

    public String getCotizacion_info_id() {
        return cotizacion_info_id;
    }

    public void setCotizacion_info_id(String cotizacion_info_id) {
        this.cotizacion_info_id = cotizacion_info_id;
    }
}
