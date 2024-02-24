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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "recibos")
public class Ticket implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_recibo")
	private UUID id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_hora")
	private Date timestamp;

	@Column(name = "precio_total")
	private Double totalPrice;

	@Column(name = "abono")
	private Double pay;

	@ManyToOne(
			targetEntity = Employee.class,
			optional = false,
			cascade = CascadeType.REFRESH
	)
	@JoinColumn(name = "id_empleado")
	private Employee employee;

	@Override
	public String toString()
	{
		return "Ticket{" +
				"id=" + id +
				", timestamp=" + timestamp +
				", totalPrice=" + totalPrice +
				", pay=" + pay +
				", employee=" + employee +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Ticket ticket)) return false;
		return Objects.equals(id, ticket.id)
				&& Objects.equals(timestamp, ticket.timestamp)
				&& Objects.equals(totalPrice, ticket.totalPrice)
				&& Objects.equals(pay, ticket.pay)
				&& Objects.equals(employee, ticket.employee);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, timestamp, totalPrice, pay, employee);
	}

}
