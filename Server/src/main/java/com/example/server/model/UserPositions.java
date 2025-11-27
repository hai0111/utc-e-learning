package com.example.server.model;

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

import java.util.UUID;

@Entity
@Table(name = "UserPositions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPositions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "PositionId", referencedColumnName = "Id")
    private Positions positions;
}
