package com.realssoft.dulcefrio.api.persistence.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeRegistration;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "roles", schema = "public", catalog = "DulceFrio")
public class Role implements Serializable
{

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_rol")
    private UUID id;

    @Column(name = "nombre")
    private String name;

    @Basic
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Override
    public String toString()
    {
        return "Role{" +
                "id=" + id +
                ", name='" + name +
                ", description='" + description +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(id, role.id)
                && Objects.equals(name, role.name)
                && Objects.equals(description, role.description);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, description);
    }

}
