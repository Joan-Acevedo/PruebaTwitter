package com.example.twitter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stream")
public class Hilo {

    @Id
    private String id;

    private String titulo;
    private String creador;

    public Hilo() {
    }

    public Hilo(String id, String titulo, String creador) {
        this.id = id;
        this.titulo = titulo;
        this.creador = creador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    @Override
    public String toString() {
        return "Hilo{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", creador='" + creador + '\'' +
                '}';
    }
}
