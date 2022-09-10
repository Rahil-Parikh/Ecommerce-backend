package com.hub.ecommerce.models.admin.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sieve_size")
@Data
@NoArgsConstructor
public class SieveSize implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    @Id
    @Column(unique=true)
    private String range;
    @Column(precision = 3, scale = 2)
    private double upperLimit;
    @Column(precision = 3, scale = 1)
    private double lowerLimit;

    public SieveSize(String range){
        int i = range.indexOf('-');
        if(i!=-1){
            this.range = range;
            if(range.charAt(0)=='^') {
                lowerLimit = Double.parseDouble(range.substring(1, i));
            }
            else{
                lowerLimit = Double.parseDouble(range.substring(0, i));
            }
            upperLimit = Double.parseDouble(range.substring(i+1));
        }
    }

    public boolean belongsInRange(double sieveSize){
        if(sieveSize>lowerLimit && upperLimit<=upperLimit)// = sign ask if needed to be changed
            return true;
        else
            return false;
    }

}
