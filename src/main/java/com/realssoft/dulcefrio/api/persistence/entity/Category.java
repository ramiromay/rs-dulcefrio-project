package com.realssoft.dulcefrio.api.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Table(name = "categorias")
public class Category implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_categoria")
	private UUID id;

	@Column(name = "nombre")
	private String name;

	@ManyToOne(
			targetEntity = CategoryType.class,
            optional = false,
			cascade = CascadeType.ALL
	)
	@JoinColumn(name = "id_tipo_categoria")
	private CategoryType categoryType;

	@Override
	public String toString()
	{
		return "Category{" +
				"id=" + id +
				", name='" + name +
				", categoryType=" + categoryType +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Category category)) return false;
		return Objects.equals(id, category.id)
				&& Objects.equals(name, category.name)
				&& Objects.equals(categoryType, category.categoryType);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, categoryType);
	}

}
