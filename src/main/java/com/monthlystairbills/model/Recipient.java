package com.monthlystairbills.model;

import java.math.BigDecimal;

public class Recipient {

    private final String name;
    private final int floor;
    private final String email;
    private final String concept;
    private final BigDecimal quantityInEuros;

    public Recipient(String name, int floor, String email, String concept, BigDecimal quantityInEuros) {
        this.name = name;
        this.floor = floor;
        this.email = email;
        this.concept = concept;
        this.quantityInEuros = quantityInEuros;
    }

    public String getName() { return name; }
    public int getFloor() { return floor; }
    public String getEmail() { return email; }
    public String getConcept() { return concept; }
    public BigDecimal getQuantityInEuros() { return quantityInEuros; }

    /** Returns a safe filename-friendly version of the name (spaces replaced by underscores) */
    public String getFilenameName() {
        return name.replace(" ", "_");
    }
}
