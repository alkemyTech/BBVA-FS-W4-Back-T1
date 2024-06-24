package com.magicdogs.alkywall.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "thirdAccount")
public class ThirdAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idThirdAccount")
    private Long idThirdAccount;

    @Column(name = "nickname")
    private String nickname;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idDestinationAccount", nullable = false)
    private Account destinationAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idDestinationUser", nullable = false)
    private User destinationUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUser")
    private User user;
}