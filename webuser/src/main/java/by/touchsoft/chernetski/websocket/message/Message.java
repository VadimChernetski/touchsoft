package by.touchsoft.chernetski.websocket.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Message class
 * @author Vadim Chernetski
 */
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    /** Message context */
    @Getter @Setter
    private String context;

    /** Name of user */
    @Getter @Setter
    private String name;

    /**
     * adding dispatch time
     */
    public void writeInTime(){
        LocalTime time = LocalTime.now();
        context = "(" + time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "): " + context;
    }

    @Override
    public String toString() {
        return name + " " + context;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (context == null) {
            if (other.context != null)
                return false;
        } else if (!context.equals(other.context))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
