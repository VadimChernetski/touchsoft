package by.touchsoft.chernetski.websocket.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Getter @Setter
    private String context;
    @Getter @Setter
    private String name;

    public void writeInTime(){
        LocalTime time = LocalTime.now();
        context = "(" + time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "): " + context;
    }

    @Override
    public String toString() {
        return name + " " + context;
    }
}
