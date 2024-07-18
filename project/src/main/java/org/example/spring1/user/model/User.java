package org.example.spring1.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.spring1.tradeOrder.model.TradeOrder;
import org.example.spring1.userAssets.model.UserAssets;
import org.example.spring1.userFunds.model.UserFunds;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    })
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 20)
  private String username;

  @Column(nullable = false, length = 50)
  private String email;

  @Column(nullable = false, length = 120)
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<TradeOrder> tradeOrders = new HashSet<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<UserFunds> userFunds = new HashSet<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<UserAssets> userAssets = new HashSet<>();
}
