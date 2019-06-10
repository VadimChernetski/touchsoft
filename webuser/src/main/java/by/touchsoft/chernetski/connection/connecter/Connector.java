package by.touchsoft.chernetski.connection.connecter;

import by.touchsoft.chernetski.connection.constants.ConnectionConstants;
import by.touchsoft.chernetski.websocket.endpoint.ChatEndPoint;
import by.touchsoft.chernetski.websocket.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connector extends Thread {

    private BufferedReader in;
    private BufferedWriter out;
    private ChatEndPoint endPoint;
    private Socket socket;

    public Connector(ChatEndPoint endPoint){
        this.endPoint = endPoint;
        try {
            this.socket = new Socket(ConnectionConstants.ID, ConnectionConstants.PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        String input;
        Matcher matcher;
        Pattern pattern = Pattern.compile("[A-z]+ (?=\\()");
        Message message;
        String name;
        while(true){
            try {
                input = in.readLine();
                if(input.equals("/exit")){
                    exit();
                    break;
                }
                matcher = pattern.matcher(input);
                if(matcher.find()) {
                    name = matcher.group();
                    message = new Message(input.replaceAll(name, ""), name);
                } else {
                    message = new Message(input, "server");
                }
                endPoint.sendMessage(message);
            } catch (IOException exception) {
                endPoint.sendMessage(new Message("some problems with server", "server"));
                exception.printStackTrace();
                break;
            }
        }
    }

    public void sendMessage(String message){
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void exit(){
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
