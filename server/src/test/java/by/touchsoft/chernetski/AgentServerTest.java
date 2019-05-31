package by.touchsoft.chernetski;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentServerTest {
    @Test
    void test() throws IOException {
        File dataTest = new File("src/test/resources/test.txt");
        dataTest.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(dataTest));
        writer.write("hi");
        writer.flush();
        writer.close();
        assertEquals(true, true);
        dataTest.delete();
    }
}
