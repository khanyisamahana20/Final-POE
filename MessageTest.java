import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // ---------- Message length (success and failure) ----------

    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longText = "a".repeat(260);
        Message msg = new Message("0012345678", "+27718693002", longText, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                msg.checkMessageLength());
    }

    // ---------- Recipient number (success and failure) ----------

    @Test
    public void testRecipientNumberSuccess() {
        Message msg = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientNumberFailure() {
        Message msg = new Message("0012345678", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // ---------- Message hash (first one, then the rest in a loop) ----------

    @Test
    public void testMessageHashTestCase1() {
        Message msg = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", msg.createMessageHash());
    }

    @Test
    public void testMessageHashLoop() {
        String[] messages = {
                "Hi Mike, can you join us for dinner tonight?",
                "Hi Keegan, did you receive the payment?"
        };
        String[] expected = {
                "00:0:HITONIGHT",
                "00:1:HIPAYMENT"
        };
        for (int i = 0; i < messages.length; i++) {
            Message msg = new Message("0012345678", "+27718693002", messages[i], i);
            assertEquals(expected[i], msg.createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    public void testMessageIDCreated() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertTrue(msg.checkMessageID());
        assertEquals(10, msg.myMessageID.length());
    }

    // ---------- Message sent (send, disregard, store) ----------

    @Test
    public void testMessageSent() {
        Message msg = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message successfully sent.", msg.SentMessage("1"));
    }

    @Test
    public void testMessageDisregarded() {
        Message msg = new Message("0087654321", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Press 0 to delete the message.", msg.SentMessage("2"));
    }

    @Test
    public void testMessageStored() {
        Message msg = new Message("0011223344", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 2);
        assertEquals("Message successfully stored.", msg.SentMessage("3"));
    }

    // ---------- Total number of messages sent ----------

    @Test
    public void testTotalMessagesIncreases() {
        int before = Message.returnTotalMessages();
        Message msg = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        msg.SentMessage("1");
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    // ---------- Part 3: Stored messages ----------

    @Test
    public void testDisplaySendersAndRecipients() {
        Message msg = new Message("0012345678", "+27710000001",
                "Hi Mike, can you please call me.", 0);
        msg.SentMessage("3");
        String result = Message.displaySendersAndRecipients();
        assertTrue(result.contains("Sender: Me"));
        assertTrue(result.contains("Recipient: +27710000001"));
    }

    @Test
    public void testSearchMessagesByRecipient() {
        Message msg = new Message("0012345678", "+27710000002",
                "Are you having a party this Saturday?", 0);
        msg.SentMessage("3");
        String result = Message.searchMessagesByRecipient("+27710000002");
        assertTrue(result.contains("Are you having a party this Saturday?"));
    }

    @Test
    public void testSearchByMessageID() {
        Message msg = new Message("0012345678", "+27710000003",
                "Hi Mike, can you join us for dinner tonight?", 0);
        msg.SentMessage("3");
        String id = msg.myMessageID;
        String result = Message.searchByMessageID(id);
        assertTrue(result.contains("+27710000003"));
        assertTrue(result.contains("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testDeleteByHash() {
        Message msg = new Message("0012345678", "+27710000004",
                "Hi Keegan, did you receive the payment?", 0);
        msg.SentMessage("3");
        String hash = msg.myMessageHash;
        String result = Message.deleteByHash(hash);
        assertTrue(result.contains("successfully deleted"));
        assertTrue(Message.searchByMessageID(msg.myMessageID).contains("No message found"));
    }

    @Test
    public void testDisplayReport() {
        Message msg = new Message("0012345678", "+27710000005",
                "Hi Mike, can you please call me.", 0);
        msg.SentMessage("3");
        String result = Message.displayReport();
        assertTrue(result.contains("Message ID:"));
        assertTrue(result.contains("Hash:"));
        assertTrue(result.contains("Recipient: +27710000005"));
    }

    private void populateBriefTestData() {
        Message.storedMessages.clear();
        Message.sentMessages.clear();
        Message.disregardedMessages.clear();
        Message.messageHashes.clear();
        Message.messageIDs.clear();
        new Message("1111111111", "+27834557896", "Did you get the cake?", 0).SentMessage("1");
        new Message("2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", 1).SentMessage("3");
        new Message("3333333333", "+27834484567", "Yohooo, I am at your gate.", 2).SentMessage("2");
        new Message("4444444444", "0838884567", "It is dinner time!", 3).SentMessage("1");
        new Message("5555555555", "+27838884567", "Ok, I am leaving without you.", 4).SentMessage("3");
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        populateBriefTestData();
        assertEquals(2, Message.sentMessages.size());
    }

    @Test
    public void testDisplayLongestMessage() {
        populateBriefTestData();
        String longest = Message.displayLongestMessage();
        assertTrue(longest.contains("Where are you? You are late!"));
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        populateBriefTestData();
        String result = Message.searchMessagesByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late!"));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testSearchForMessageID_BriefMessage4() {
        Message.storedMessages.clear();
        Message.messageHashes.clear();
        Message.messageIDs.clear();
        Message msg4 = new Message("4444444444", "0838884567", "It is dinner time!", 3);
        msg4.SentMessage("3");
        String result = Message.searchByMessageID("4444444444");
        assertTrue(result.contains("It is dinner time!"));
    }

    @Test
    public void testDeleteMessageUsingHash_BriefMessage2() {
        Message.storedMessages.clear();
        Message.messageHashes.clear();
        Message.messageIDs.clear();
        Message msg2 = new Message("2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", 1);
        msg2.SentMessage("3");
        String result = Message.deleteByHash(msg2.myMessageHash);
        assertTrue(result.contains("successfully deleted"));
    }

    @Test
    public void testDisplayReport_BriefData() {
        populateBriefTestData();
        String result = Message.displayReport();
        assertTrue(result.contains("Recipient: +27838884567"));
        assertTrue(result.contains("Hash:"));
    }
}