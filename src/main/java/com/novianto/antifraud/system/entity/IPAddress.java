package com.novianto.antifraud.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@NoArgsConstructor
public class IPAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;
    @Column
    @NotEmpty
    private String ip;

    public IPAddress(String ip) {
        this.ip = ip;
    }
}
