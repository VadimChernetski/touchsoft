package by.touchsoft.chernetski.connection;

public class Connector extends Thread {

    private Users users;

    public Connector(Users users){
        this.users = users;
    }

    @Override
    public void run(){
        while(true){
            users.tryToConnect();
        }
    }
}
