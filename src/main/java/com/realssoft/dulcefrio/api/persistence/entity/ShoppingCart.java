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
@Table(name = "carrito_compras")
public class ShoppingCart implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_carrito_compra")
	private UUID id;

	@ManyToOne(
			targetEntity = Product.class,
			optional = false,
			cascade = CascadeType.REFRESH
	)
	@JoinColumn(name = "id_producto")
	private Product product;

	@ManyToOne(
			targetEntity = Employee.class,
			optional = false,
			cascade = CascadeType.REFRESH
	)
	@JoinColumn(name = "id_empleado")
	private Employee employee;

	@Column(name = "cantidad_producto")
	private Integer numberProduct;

	@Override
	public String toString()
	{
		return "ShoppingCart{" +
				"id=" + id +
				", product=" + product +
				", employee=" + employee +
				", numberProduct=" + numberProduct +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof ShoppingCart that)) return false;
		return Objects.equals(id, that.id)
				&& Objects.equals(product, that.product)
				&& Objects.equals(employee, that.employee)
				&& Objects.equals(numberProduct, that.numberProduct);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, product, employee, numberProduct);
	}

}
