package com.realssoft.dulcefrio.api.persistence.entity;

import jakarta.persistence.Basic;
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
@Table(name = "usuarios")
public class User implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_usuario")
	private UUID id;

	@Column(name = "usuario")
	private String username;

	@Column(name = "contrasenia")
	private String password;

	@OneToOne(
			targetEntity = Employee.class,
            optional = false,
			cascade = CascadeType.ALL
	)
	@JoinColumn(name = "id_empleado")
	private Employee employee;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_creacion")
	private Date creationDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_modificacion")
	private Date modificationDate;

	@ManyToOne(
			targetEntity = Role.class,
			optional = false,
			cascade = CascadeType.ALL
	)
	@JoinColumn(name = "id_rol")
	private Role role;

	@Column(name = "disponible")
	private Boolean available;

	@Column(name = "activo")
	private Boolean active;

	@Basic
	@Column(name = "token", columnDefinition = "TEXT")
	private String token;

	@Override
	public String toString()
	{
		return "User{" +
				"idUser=" + id +
				", username='" + username +
				", password='" + password +
				", employee=" + employee +
				", creationDate=" + creationDate +
				", modificationDate=" + modificationDate +
				", role=" + role +
				", available=" + available +
				", active=" + active +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof User user)) return false;
		return Objects.equals(id, user.id)
				&& Objects.equals(username, user.username)
				&& Objects.equals(password, user.password)
				&& Objects.equals(employee, user.employee)
				&& Objects.equals(creationDate, user.creationDate)
				&& Objects.equals(modificationDate, user.modificationDate)
				&& Objects.equals(role, user.role)
				&& Objects.equals(available, user.available)
				&& Objects.equals(active, user.active);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, username, password, employee,
				creationDate, modificationDate, role, available, active);
	}

}
