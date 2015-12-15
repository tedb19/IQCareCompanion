
package iqcarecompanion.core.dao;

import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Teddy Odhiambo
 */
public class RegimenDaoTest extends AbstractDaoTest {

    private RegimenDao dao;
    
    public RegimenDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        createSchema();
    }
    
    @Before
    public void setUp() throws Exception {
        importDataSet();
        dao = new RegimenDao(getConnection(), "");
    }
    
    @Test
    public void GetCurrentRegimen_ExistingVisitIdParameter_RegimenReturned() throws Exception {
        String regimen = dao.getCurrentRegimen(1);
        assertThat(regimen, is(allOf(not(nullValue()),instanceOf(String.class), is("D4T/3TC/NVP"))));
    }
    
    @Test
    public void GetCurrentRegimen_NoneExistantVisitIdParameter_EmptyStringReturned() throws Exception {
        String regimen = dao.getCurrentRegimen(11);
        assertThat(regimen, is(allOf(not(nullValue()),instanceOf(String.class), is(""))));
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none(); 
    
    @Test
    public void GetCurrentRegimen_InvalidParams_SQLExceptionThrown() throws SQLException, ClassNotFoundException {
        thrown.expect(SQLException.class);
        thrown.expectMessage("Syntax error in SQL statement");
        RegimenDao badDao = new RegimenDao(getConnection(), "zzz");
        badDao.getCurrentRegimen(111);
    }
    
    
}
