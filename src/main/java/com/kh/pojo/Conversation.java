/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author congchuahiep
 */
@Entity
@Table(name = "conversations")
@NamedQueries({
    @NamedQuery(name = "Conversation.findAll", query = "SELECT c FROM Conversation c"),
    @NamedQuery(name = "Conversation.findById", query = "SELECT c FROM Conversation c WHERE c.id = :id"),
    @NamedQuery(name = "Conversation.findByCreatedAt", query = "SELECT c FROM Conversation c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Conversation.findByUpdatedAt", query = "SELECT c FROM Conversation c WHERE c.updatedAt = :updatedAt")})
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversationId")
    private Set<ChatMessage> chatMessageSet;
    @JoinColumn(name = "user1_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user1Id;
    @JoinColumn(name = "user2_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user2Id;

    public Conversation() {
    }

    public Conversation(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ChatMessage> getChatMessageSet() {
        return chatMessageSet;
    }

    public void setChatMessageSet(Set<ChatMessage> chatMessageSet) {
        this.chatMessageSet = chatMessageSet;
    }

    public User getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(User user1Id) {
        this.user1Id = user1Id;
    }

    public User getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(User user2Id) {
        this.user2Id = user2Id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Conversation)) {
            return false;
        }
        Conversation other = (Conversation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kh.pojo.Conversation[ id=" + id + " ]";
    }
    
}
