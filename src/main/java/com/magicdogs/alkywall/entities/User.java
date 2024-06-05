package com.magicdogs.alkywall.entities;

import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="idUser", nullable = false)
    private Long idUser;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "birthDate", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "documentType", nullable = false)
    private DocumentType documentType;

    @Column(name = "documentNumber", nullable = false)
    private String documentNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idRole")
    private Role role;

    @Column(name = "updateDate")
    private LocalDateTime updateDate;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "softDelete")
    private Integer softDelete;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;
    /*@OneToMany(mappedBy = "thirdAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThirdAccount> thirdAccounts;*/

    public User(String firstName, String lastName, LocalDate birthDate, UserGender gender, DocumentType documentType, String documentNumber, String email, String password, Role role, Integer softDelete) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.email = email;
        this.password = password;
        this.role = role;
        this.softDelete = softDelete;
    }

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
    }

    public Account getAccountIn(AccountType accountType, CurrencyType currencyType) {
        for (Account account : accounts) {
            if (account.getAccountType().equals(accountType) && account.getCurrency().equals(currencyType)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Transaction findTransactionByIdInAccount(Long id){
        Transaction transactionFinded = null;
        for(Account account: accounts){
            transactionFinded = account.searchTransactionById(id);
            if(transactionFinded !=  null) break;
        }
        return transactionFinded;
    }
}
