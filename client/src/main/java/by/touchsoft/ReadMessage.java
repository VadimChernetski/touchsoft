package by.touchsoft;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadMessage extends Thread{

    private BufferedReader in;

    public ReadMessage(BufferedReader in){
        this.in = in;
    }

    @Override
    public void run() {
        String message;
        while(true){
            try {
                message = in.readLine();
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("you: ");
        }
    }
}
