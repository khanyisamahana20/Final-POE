import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Message {
    // Fields for message details
    String myMessageID;
    int myNumMessagesSent;
    String myRecipient;
    String myMessageText;
    String myMessageHash;
    static int totalMessages = 0;
    static ArrayList<Message> sentMessages = new ArrayList<>();
    // PART 3 ARRAYS
    static ArrayList<Message> storedMessages = new ArrayList<>();
    static ArrayList<Message> disregardedMessages = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();
    static ArrayList<String> messageIDs = new ArrayList<>();

    // Constructor - generates random 10-digit ID
    public Message(String recipient, String messageText, int num) {
        long id = 1000000000L + (long) (Math.random() * 9000000000L);
        this.myMessageID = String.valueOf(id);
        this.myRecipient = recipient;
        this.myMessageText = messageText;
        this.myNumMessagesSent = num;
        this.myMessageHash = createMessageHash();
    }

    // Second constructor - used in unit tests so we can choose the ID
    public Message(String id, String recipient, String messageText, int num) {
        this.myMessageID = id;
        this.myRecipient = recipient;
        this.myMessageText = messageText;
        this.myNumMessagesSent = num;
        this.myMessageHash = createMessageHash();
    }

    // Check if ID is not more than 10 chars
    public boolean checkMessageID() {
        return myMessageID.length() <= 10;
    }

    // Check cell number - must have + and not too long
    public String checkRecipientCell() {
        if (myRecipient.length() <= 13 && myRecipient.startsWith("+")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Check message is not more than 250 characters
    public String checkMessageLength() {
        if (myMessageText.length() <= 250) {
            return "Message ready to send.";
        } else {
            return "Message exceeds 250 characters by " + (myMessageText.length() - 250) + "; please reduce the size.";
        }
    }

    // Create hash: first 2 digits of ID + message count + first and last word
    public String createMessageHash() {
        String[] words = myMessageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^A-Za-z]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z]", "");

        String firstTwoID = myMessageID.substring(0, 2);
        myMessageHash = firstTwoID + ":" + myNumMessagesSent + ":" + firstWord + lastWord;
        myMessageHash = myMessageHash.toUpperCase();
        return myMessageHash;
    }

    // Option for what to do with message
    public String SentMessage(String choice) {
        if (choice.equals("1")) {
            totalMessages++;
            sentMessages.add(this);
            return "Message successfully sent.";
        } else if (choice.equals("2")) {
            disregardedMessages.add(this);
            return "Press 0 to delete the message.";
        } else if (choice.equals("3")) {
            storeMessage();
            return "Message successfully stored.";
        } else {
            return "Invalid choice.";
        }
    }

    // Returns ALL sent messages
    public static String printMessages() {
        String result = "";
        for (Message m : sentMessages) {
            result += "Message ID: " + m.myMessageID + "\nMessage Hash: " + m.myMessageHash + "\nRecipient: " + m.myRecipient + "\nMessage: " + m.myMessageText + "\n\n";
        }
        return result;
    }

    public static int returnTotalMessages() {
        return totalMessages;
    }

    public void storeMessage() {
        if (myMessageHash == null) {
            createMessageHash();
        }
        for (int i = storedMessages.size() - 1; i >= 0; i--) {
            if (storedMessages.get(i).myMessageID.equals(myMessageID)) {
                storedMessages.remove(i);
                messageHashes.remove(i);
                messageIDs.remove(i);
            }
        }
        storedMessages.add(this);
        messageHashes.add(myMessageHash);
        messageIDs.add(myMessageID);
        try {
            FileWriter file = new FileWriter("storedMessages.json", true);
            file.write("{\"messageID\":\"" + myMessageID + "\",\"recipient\":\"" + myRecipient + "\",\"message\":\"" + myMessageText + "\"}\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessageID() {
        return myMessageID;

    }

    // PART 3 METHODS
    public static String displayLongestMessage() {
        String longest = "";
        for (Message m : storedMessages) {
            if (m.myMessageText.length() > longest.length()) longest = m.myMessageText;
        }
        return longest;
    }

    public static String searchMessagesByRecipient(String recipient) {
        String result = "";
        for (Message m : storedMessages) {
            if (m.myRecipient.equals(recipient)) {
                result = result + m.myMessageText + "\n";
            }
        }
        if (result.equals("")) return "No messages found for " + recipient;
        return result;
    }

    public static String displaySendersAndRecipients() {
        String result = "";
        for (Message m : storedMessages) {
            result = result + "Sender: Me Recipient: " + m.myRecipient + "\n";
        }
        if (result.equals("")) return "No stored messages";
        return result;
    }

    public static String searchByMessageID(String id) {
        for (Message m : storedMessages) {
            if (m.myMessageID.equals(id)) {
                return "Recipient: " + m.myRecipient + "\nMessage: \"" + m.myMessageText + "\"";
            }
        }
        return "No message found for ID: " + id;
    }

    public static String deleteByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            if (messageHashes.size() > i && messageHashes.get(i).equals(hash)) {
                String deleted = storedMessages.get(i).myMessageText;
                storedMessages.remove(i);
                messageHashes.remove(i);
                return "Message: \"" + deleted + "\" successfully deleted.";
            }
        }
        return "No message found for hash: " + hash;
    }

    public static String displayReport() {
        String result = "";
        for (Message m : storedMessages) {
            result = result + "Message ID: " + m.myMessageID + "\nHash: " + m.myMessageHash + "\nRecipient: " + m.myRecipient + "\nMessage: " + m.myMessageText + "\n\n";
        }
        if (result.equals("")) return "No stored messages to display";
        return result;
    }

    void main() {
    }

}