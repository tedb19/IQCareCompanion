/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.dao;

import hapimodule.core.constants.IdentifierType;
import hapimodule.core.entities.Person;
import hapimodule.core.entities.PersonIdentifier;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ted
 */
public class PersonDaoTest extends AbstractDaoTest {
    
    public PersonDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void SetPersonIdentifier_IdentifierValueSupplied_ValidPersonIdentifierReturned() throws Exception {
        String identifierValue = "IQ-TEST-1234";
        PersonDao dao = new PersonDao(getConnection(), "");
        PersonIdentifier personIdentifier = dao.setPersonIdentifier(IdentifierType.CCC_NUMBER, identifierValue);
        assertThat(personIdentifier.getIdentifier(), is(equalTo(identifierValue)));
    }
    
    @Test
    public void SetPersonIdentifier_NullIdentifierValueSupplied_NullReturned() throws Exception {
        PersonDao dao = new PersonDao(getConnection(), "");
        PersonIdentifier personIdentifier = dao.setPersonIdentifier(IdentifierType.CCC_NUMBER, null);
        assertThat(personIdentifier, is(nullValue()));
    }
    
}
