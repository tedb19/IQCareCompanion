/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.entities;

/**
 *
 * @author Teddy
 */
public enum LabTest {
    CD4_COUNT("CD4_COUNT"),
    VIRAL_LOAD("VIRAL_LOAD"),
    CD4_PERCENT("CD4_PERCENT");
    
    private final String st;

    private LabTest(String st) {
        this.st = st;
    }

    public String getValue() {
        return st;
    }
}
