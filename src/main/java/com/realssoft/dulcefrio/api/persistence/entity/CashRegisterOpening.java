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
@Table(name = "aperturas_caja")
public class CashRegisterOpening implements Serializable
{

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_apertura_caja")
    private UUID id;

    @Column(name = "monto_inicial")
    private Double initialAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_hora")
    private Date timestamp;

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
        return "CashRegisterOpening{" +
                "id=" + id +
                ", initialAmount=" + initialAmount +
                ", timestamp=" + timestamp +
                ", employee=" + employee +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof CashRegisterOpening that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(initialAmount, that.initialAmount)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(employee, that.employee);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, initialAmount, timestamp, employee);
    }

}
