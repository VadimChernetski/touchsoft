package by.touchsoft.chernetski.connection;

/**
 * Class that connects agents and clients
 * @author Vadim Chernetski
 */
public class Connector extends Thread {

    /** Instance of Users class */
    private Users users;

    /**
     * Constructor
     * @param users - instance of Users class
     */
    public Connector(Users users){
        this.users = users;
    }

    /**
     * Method that starts a thread (invoke method users.tryToConnect() which connect agents and clients
     */
    @Override
    public void run(){
        while(true){
            users.tryToConnect();
        }
    }
}
