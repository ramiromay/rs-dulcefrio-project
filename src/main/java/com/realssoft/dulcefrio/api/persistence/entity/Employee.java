package com.realssoft.dulcefrio.api.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "empleados")
public class Employee implements Serializable
{

	@Serial
	private  static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_empleado")
	private UUID id;

	@Column(name = "nombre")
	private String name;

	@Column(name = "apellido")
	private String lastName;

	@Column(name = "numero_telefono")
	private Long phone;

	@Column(name = "correo")
	private String email;

	@Temporal(TemporalType.DATE)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fecha_ingreso")
	private Date admissionDate;

	@Override
	public String toString()
	{
		return "Employee{" +
				"id=" + id +
				", name='" + name +
				", lastName='" + lastName +
				", telephoneNumber=" + phone +
				", email='" + email +
				", admissionDate=" + admissionDate +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Employee employee)) return false;
		return Objects.equals(id, employee.id)
				&& Objects.equals(name, employee.name)
				&& Objects.equals(lastName, employee.lastName)
				&& Objects.equals(phone, employee.phone)
				&& Objects.equals(email, employee.email)
				&& Objects.equals(admissionDate, employee.admissionDate);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, lastName, phone, email, admissionDate);
	}

}
