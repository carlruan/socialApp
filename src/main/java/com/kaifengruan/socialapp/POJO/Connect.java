package com.kaifengruan.socialapp.POJO;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name="connects")
@EntityListeners(AuditingEntityListener.class)
public class Connect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "connector_id")
    private String connectorId;

    public long getId() {
        return id;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connector_id) {
        this.connectorId = connector_id;
    }
}
