package com.novianto.antifraud.system.entity.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.novianto.antifraud.system.entity.Region;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long amount;
    private String number;
    private String ip;

    @Enumerated(EnumType.STRING)
    public Region region;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private TransactionResult result;

    @JsonIgnore
    @Transient
    private String info;

    @JsonIgnore
    private TransactionResult feedback;

    // custom JSON properties
    @JsonProperty("transactionId")
    public Long getId(){
        return id;
    }

    @JsonProperty("result")
    public String getResult() {
        return result.name();
    }

    @JsonProperty("feedback")
    public String getFeedback() {
        return feedback == null ? "" : feedback.name();
    }
}
