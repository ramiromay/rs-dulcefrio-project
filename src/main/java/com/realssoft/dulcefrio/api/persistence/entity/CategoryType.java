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
@Table(name = "tipos_categoria")
public class CategoryType implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_tipo_categoria")
	private UUID id;

	@Column(name = "nombre")
	private String name;

	@Basic
	@Column(name = "descripcion", columnDefinition = "TEXT")
	private String description;

	@Override
	public String toString()
	{
		return "CategoryType{" +
				"id=" + id +
				", name='" + name +
				", description='" + description +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof CategoryType that)) return false;
		return Objects.equals(id, that.id)
				&& Objects.equals(name, that.name)
				&& Objects.equals(description, that.description);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, description);
	}

}
