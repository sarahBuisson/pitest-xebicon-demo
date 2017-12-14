package fr.perso.pitest.xebicon.demo.fizzbuzz;

import fr.perso.pitest.xebicon.demo.poker.Card;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * The type Fizz buzz test.
 */
public class ResourceTest {


    @Test
    public void getRessources() {

        Assert.assertTrue(new File( Card.class.getResource("./F1.txt").getFile()).exists());



    }


}
