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
@Table(name = "ventas")
public class Sale implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_ventas")
	private UUID id;

	@ManyToOne(
			targetEntity = Ticket.class,
			optional = false,
			cascade = CascadeType.REFRESH
	)
	@JoinColumn(name = "id_recibo")
	private Ticket ticket;

	@ManyToOne(
			targetEntity = Product.class,
			optional = false,
			cascade = CascadeType.REFRESH
	)
	@JoinColumn(name = "id_producto")
	private Product product;

	@Column(name = "cantidad_producto")
	private Integer productQuantity;

	@Override
	public String toString()
	{
		return "Sale{" +
				"id=" + id +
				", productQuantity=" + productQuantity +
				", ticket=" + ticket +
				", product=" + product +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Sale sale)) return false;
		return Objects.equals(id, sale.id)
				&& Objects.equals(productQuantity, sale.productQuantity)
				&& Objects.equals(ticket, sale.ticket)
				&& Objects.equals(product, sale.product);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, productQuantity, ticket, product);
	}

}
