package com.oguz.demo.microwallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Base class definition far project domain entities.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode
abstract class BaseEntity implements Serializable {

    /**
     * Every time an entity is updated in the database the version field will be increased by one.
     * Every operation that updates the entity in the database will have appended WHERE version = VERSION_THAT_WAS_LOADED_FROM_DATABASE to its query.
     */
    @Version
    @Column(name = "version")
    @JsonIgnore
    private Long version = 0L;

    @Column(name = "created_at")
    @CreatedDate
    @Temporal(TIMESTAMP)
    @JsonIgnore
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @JsonIgnore
    private Date updatedAt;

    @CreatedBy
    @Column(name = "updated_by")
    @JsonIgnore
    private String updatedBy;

    private void setVersion(Long version) {
        this.version = version;
    }
}
