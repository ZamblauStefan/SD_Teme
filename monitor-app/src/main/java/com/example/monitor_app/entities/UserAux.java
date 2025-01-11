package com.example.monitor_app.entities;

import jakarta.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "user_aux")
public class UserAux implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    public UserAux() {}

    public UserAux(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
