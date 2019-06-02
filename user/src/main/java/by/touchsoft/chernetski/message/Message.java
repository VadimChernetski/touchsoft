package by.touchsoft.chernetski.message;

import by.touchsoft.chernetski.UserConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Message {

    @Getter @Setter
    private List<String> packets;
    @Setter @Getter
    private boolean sendPacketStatus;

    public Message(){
        packets = new ArrayList<>();
        sendPacketStatus = false;
    }

    public void initMessage(String message) {
        Matcher matcher = UserConstants.PATTERN_PACKET.matcher(message);
        int packetNumber = 0;
        String packet;
        while (matcher.find()){
            packet = matcher.group();
            packets.add("$" + 3 + "$" + packet);
            message = message.replaceFirst(packet, "");
            packetNumber++;
        }
        if(!message.isEmpty()){
            packets.add("&" + message.length() + "$" + message);
        }
        packets.add(0, "$" + packetNumber + "$");
    }
}
