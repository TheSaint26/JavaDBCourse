package hospital_db_04.entity;

import hospital_db_04.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "medicaments")
public class Medicament extends BaseEntity {
    private String name;

    public Medicament(){}

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
