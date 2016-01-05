/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.RuntimeDirectory.createRuntimeDir;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ted
 */
public class RuntimeDirectoryTest {
    static File newFile;
    
    @BeforeClass
    public static void setUpClass(){
        newFile = new File(
            new StringBuilder().append(System.getProperty("user.home"))
                    .append(File.separator)
                    .append("IQCare-Companion")
                    .append(File.separator)
                    .append("Test").toString()
        );
    }
    
    @After
    public void tearDown() {
        if(newFile.exists()){
            newFile.delete();
        }
    }

    @Test
    public void CreateRuntimeDir_DirDoesNotExist_DirCreated() throws Exception {
        assertFalse("Test Directory exists", newFile.exists());
        createRuntimeDir(newFile, "test Directory");
        assertTrue("Test Directory does not exist",newFile.exists());
        assertTrue(newFile.getCanonicalPath()+" is not a directory",newFile.isDirectory());
    }
    
    @Test
    public void RuntimeDirectory_ConstructorIsPrivate() throws Exception{
      Constructor constructor = RuntimeDirectory.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      constructor.newInstance();
    }
    
}
