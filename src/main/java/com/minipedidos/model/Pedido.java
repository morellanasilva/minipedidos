package com.minipedidos.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Pedido {

    private Long pedidoId;

    @NotBlank(message = "El cliente es obligatorio")
    private String cliente;

    @NotBlank(message = "El producto es obligatorio")
    private String producto;

    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private int cantidad;

    private String estado;

    public Pedido() {
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
