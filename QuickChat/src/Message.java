import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class Message {
    private String messageID;
    private String messageHash;
    private String recipient;
    private String sender;
    private String message;
    private static int numMessagesSent = 0;
    private static List<Message> allSentMessages = new ArrayList<>();
    private boolean isSent = false;
    private boolean isStored = false;

    public Message() {
        this.messageID = generateMessageID();
    }

    private String generateMessageID() {
        Random random = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell(String recipientNumber) {
        if (recipientNumber == null || recipientNumber.isEmpty()) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        
        if (!recipientNumber.startsWith("+")) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        
        String numberPart = recipientNumber.substring(1);
        if (!numberPart.matches("\\d+")) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        
        this.recipient = recipientNumber;
        return "Cell phone number successfully captured.";
    }

    public String createMessageHash() {
        if (message == null || message.isEmpty() || messageID == null || messageID.length() < 2) {
            return "";
        }
        
        String firstTwoOfID = messageID.substring(0, 2);
        String messageNum = String.valueOf(numMessagesSent);
        
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();
        
        messageHash = (firstTwoOfID + ":" + messageNum + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                isSent = true;
                numMessagesSent++;
                allSentMessages.add(this);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                isStored = true;
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    public static String printMessages() {
        if (allSentMessages.isEmpty()) {
            return "No messages have been sent.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allSentMessages.size(); i++) {
            sb.append("Message ").append(i + 1).append(":\n");
            sb.append(allSentMessages.get(i).displayFullDetails());
            if (i < allSentMessages.size() - 1) {
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    public static int returnTotalMessagess() {
        return numMessagesSent;
    }

    public String storeMessage() {
        if (message == null || recipient == null) {
            return "Cannot store incomplete message.";
        }
        
        String json = "{\n" +
                "  \"messageID\": \"" + messageID + "\",\n" +
                "  \"messageHash\": \"" + messageHash + "\",\n" +
                "  \"recipient\": \"" + recipient + "\",\n" +
                "  \"sender\": \"" + sender + "\",\n" +
                "  \"message\": \"" + message + "\"\n" +
                "}";
        
        try {
            FileWriter writer = new FileWriter("stored_messages.json", true);
            writer.write(json + ",\n");
            writer.close();
            isStored = true;
            return json;
        } catch (IOException e) {
            return "Error storing message: " + e.getMessage();
        }
    }

    public String validateMessage(String msg) {
        if (msg == null) {
            return "Please enter a message of less than 250 characters.";
        }
        
        if (msg.length() > 250) {
            int excess = msg.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
        
        this.message = msg;
        return "Message ready to send.";
    }

    // Getters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public boolean isSent() { return isSent; }
    public boolean isStored() { return isStored; }

    // Setters
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setSender(String sender) { this.sender = sender; }
    public void setMessage(String message) { this.message = message; }
    public void setMessageID(String messageID) { this.messageID = messageID; }
    public void setMessageHash(String hash) { this.messageHash = hash; }

    public String displayFullDetails() {
        return "Message ID: " + messageID + 
               "\nMessage Hash: " + messageHash + 
               "\nSender: " + sender +
               "\nRecipient: " + recipient + 
               "\nMessage: " + message;
    }
    
    public String getMessageIDGenerated() {
        return "Message ID generated: " + messageID;
    }
    
    public static void reset() {
        numMessagesSent = 0;
        allSentMessages.clear();
    }
}