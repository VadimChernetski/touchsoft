package by.touchsoft.agent;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadMessageAgent extends Thread{

    private BufferedReader in;

    public ReadMessageAgent(BufferedReader in){
        this.in = in;
    }

    @Override
    public void run() {
        String message;
        while(true){
            try {
                message = in.readLine();
                if(message.equals("/exit")) {
                    break;
                }
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
