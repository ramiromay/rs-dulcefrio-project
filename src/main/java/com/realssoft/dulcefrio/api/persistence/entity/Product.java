package com.realssoft.dulcefrio.api.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "productos")
public class Product implements Serializable
{

	@Serial
	private static final long  serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_producto")
	private UUID id;

	@Column(name = "nombre")
	private String name;

	@Column(name = "precio")
	private Double price;

	@Column(name = "cantidad_producto")
	private Integer stock;

	@ManyToOne(
			targetEntity = Category.class,
            optional = false,
			cascade = CascadeType.REFRESH
	)
	@JoinColumn(name = "id_categoria")
	private Category category;

	@Column(name = "disponible")
	private Boolean available;

	@Override
	public String toString()
	{
		return "Product{" +
				"id=" + id +
				", name='" + name +
				", price=" + price +
				", stock=" + stock +
				", category=" + category +
				", available=" + available +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Product product)) return false;
		return Objects.equals(id, product.id)
				&& Objects.equals(name, product.name)
				&& Objects.equals(price, product.price)
				&& Objects.equals(stock, product.stock)
				&& Objects.equals(category, product.category)
				&& Objects.equals(available, product.available);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, price, stock, category, available);
	}

}
