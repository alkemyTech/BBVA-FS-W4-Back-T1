package com.magicdogs.alkywall.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fixed_term_deposits")
public class FixedTermDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDeposit")
    private Long idDeposit;

    @Column(name = "amount", nullable = false)
    private Double amount;

    //@Column (name="accountId", nullable = false)
    @ManyToOne
    @JoinColumn(name = "idAccount", nullable = false)
    private Account account;

    @Column(name = "interest", nullable = false)
    private Double interest;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "closingDate")
    private LocalDateTime closingDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }


    public double interestTotalWin(){
        if (this.creationDate == null || this.closingDate == null) {
            throw new IllegalStateException("Las fechas de creación y cierre no pueden ser nulas");
        }
        return (this.interesPerDay()) * Duration.between(this.creationDate, this.closingDate).toDays();
    }
    public double interestPartialWin(){
        if (this.creationDate == null || this.closingDate == null) {
            throw new IllegalStateException("Las fechas de creación y cierre no pueden ser nulas");
        }
        return (this.interesPerDay()) * Duration.between(this.creationDate, LocalDateTime.now()).toDays();
    }

    public double interesPerDay(){
        return (this.amount*interest/100);
    }

    public double getAmountTotalToReceive(){
        return this.amount + interestTotalWin();
    }
}
