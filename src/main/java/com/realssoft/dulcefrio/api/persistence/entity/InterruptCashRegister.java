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
@Table(name = "cortes_caja")
public class InterruptCashRegister implements Serializable
{

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_corte_caja")
    private UUID id;

    @Column(name = "ventas_totales")
    private Integer salesTotal;

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


    @ManyToOne(
            targetEntity = CashRegisterOpening.class,
            optional = false,
            cascade = CascadeType.REFRESH
    )
    @JoinColumn(name = "id_apertura_caja")
    private CashRegisterOpening cashRegisterOpening;

    @ManyToOne(
            targetEntity = InterruptType.class,
            optional = false,
            cascade = CascadeType.REFRESH
    )
    @JoinColumn(name = "id_tipo_corte")
    private InterruptType interruptType;

    @Override
    public String toString()
    {
        return "CourtCashRegister{" +
                "id=" + id +
                ", salesTotal=" + salesTotal +
                ", timestamp=" + timestamp +
                ", employee=" + employee +
                ", cashRegisterOpening=" + cashRegisterOpening +
                ", interruptType=" + interruptType +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof InterruptCashRegister interruptCashRegister)) return false;
        return Objects.equals(id, interruptCashRegister.id)
                && Objects.equals(salesTotal, interruptCashRegister.salesTotal)
                && Objects.equals(timestamp, interruptCashRegister.timestamp)
                && Objects.equals(employee, interruptCashRegister.employee)
                && Objects.equals(cashRegisterOpening, interruptCashRegister.cashRegisterOpening)
                && Objects.equals(interruptType, interruptCashRegister.interruptType);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, salesTotal, timestamp, employee, cashRegisterOpening, interruptType);
    }
}
