package by.touchsoft.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ReadMessageClient extends Thread{

    private BufferedReader in;

    public ReadMessageClient(BufferedReader in){
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
