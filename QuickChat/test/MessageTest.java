import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Field;

// ============================================
// JUNIT 4 TESTS FOR Message CLASS
// ============================================

public class MessageTest {
    
    private Message message;
    private static final String TEST_FILE = "stored_messages.json";
    
    @Before
    public void setUp() {
        Message.reset();
        message = new Message();
        deleteTestFile();
    }
    
    @After
    public void tearDown() {
        Message.reset();
        deleteTestFile();
    }
    
    private void deleteTestFile() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    // ========== CONSTRUCTOR & MESSAGE ID TESTS ==========
    
    @Test
    public void testConstructorGeneratesMessageID() {
        assertNotNull(message.getMessageID());
        assertEquals(10, message.getMessageID().length());
    }
    
    @Test
    public void testMessageIDContainsOnlyDigits() {
        String id = message.getMessageID();
        assertTrue(id.matches("\\d{10}"));
    }
    
    @Test
    public void testCheckMessageIDValid() {
        assertTrue(message.checkMessageID());
    }
    
    @Test
    public void testCheckMessageIDInvalid() {
        message.setMessageID(null);
        assertFalse(message.checkMessageID());
        
        message.setMessageID("12345678901");
        assertFalse(message.checkMessageID());
    }
    
    @Test
    public void testUniqueMessageIDs() {
        Message msg1 = new Message();
        Message msg2 = new Message();
        assertNotEquals(msg1.getMessageID(), msg2.getMessageID());
    }
    
    // ========== RECIPIENT CELL VALIDATION TESTS ==========
    
    @Test
    public void testValidRecipientNumber() {
        String result = message.checkRecipientCell("+27123456789");
        assertEquals("Cell phone number successfully captured.", result);
        assertEquals("+27123456789", message.getRecipient());
    }
    
    @Test
    public void testNullRecipientNumber() {
        String result = message.checkRecipientCell(null);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }
    
    @Test
    public void testEmptyRecipientNumber() {
        String result = message.checkRecipientCell("");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }
    
    @Test
    public void testMissingPlusSign() {
        String result = message.checkRecipientCell("27123456789");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }
    
    @Test
    public void testNonNumericCharacters() {
        String result = message.checkRecipientCell("+27abc12345");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }
    
    @Test
    public void testPlusSignOnly() {
        String result = message.checkRecipientCell("+");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }
    
    // ========== MESSAGE HASH TESTS ==========
    
    @Test
    public void testCreateMessageHash() {
        message.setMessage("Hello world");
        String hash = message.createMessageHash();
        assertNotNull(hash);
        assertTrue(hash.contains(":"));
        assertTrue(hash.toUpperCase().equals(hash));
    }
    
    @Test
    public void testMessageHashFormat() {
        message.setMessage("Hello world");
        String hash = message.createMessageHash();
        String[] parts = hash.split(":");
        assertEquals(3, parts.length);
        assertEquals(2, parts[0].length());
    }
    
    @Test
    public void testCreateMessageHashEmptyMessage() {
        String hash = message.createMessageHash();
        assertEquals("", hash);
    }
    
    @Test
    public void testCreateMessageHashNullMessage() {
        message.setMessage(null);
        String hash = message.createMessageHash();
        assertEquals("", hash);
    }
    
    @Test
    public void testMessageHashWithPunctuation() {
        message.setMessage("Hello, world!");
        String hash = message.createMessageHash();
        assertNotNull(hash);
        assertFalse(hash.contains(","));
        assertFalse(hash.contains("!"));
    }
    
    // ========== SEND MESSAGE TESTS ==========
    
    @Test
    public void testSendMessageChoiceOne() {
        String result = message.SentMessage(1);
        assertEquals("Message successfully sent.", result);
        assertTrue(message.isSent());
        assertEquals(1, Message.returnTotalMessagess());
    }
    
    @Test
    public void testSendMessageChoiceTwo() {
        String result = message.SentMessage(2);
        assertEquals("Press 0 to delete the message.", result);
        assertFalse(message.isSent());
    }
    
    @Test
    public void testSendMessageChoiceThree() {
        String result = message.SentMessage(3);
        assertEquals("Message successfully stored.", result);
        assertTrue(message.isStored());
    }
    
    @Test
    public void testSendMessageInvalidChoice() {
        String result = message.SentMessage(5);
        assertEquals("Invalid choice.", result);
    }
    
    @Test
    public void testMultipleMessagesSent() {
        Message msg1 = new Message();
        Message msg2 = new Message();
        
        msg1.SentMessage(1);
        msg2.SentMessage(1);
        
        assertEquals(2, Message.returnTotalMessagess());
    }
    
    // ========== PRINT MESSAGES TESTS ==========
    
    @Test
    public void testPrintMessagesEmpty() {
        String result = Message.printMessages();
        assertEquals("No messages have been sent.", result);
    }
    
    @Test
    public void testPrintMessagesWithContent() {
        message.setSender("John");
        message.setRecipient("+27123456789");
        message.setMessage("Hello");
        message.SentMessage(1);
        
        String result = Message.printMessages();
        assertTrue(result.contains("Message 1"));
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Hello"));
    }
    
    // ========== STORE MESSAGE TESTS ==========
    
    @Test
    public void testStoreMessageSuccess() throws IOException {
        message.setMessage("Test message");
        message.setRecipient("+27123456789");
        message.setSender("John");
        message.createMessageHash();
        
        String result = message.storeMessage();
        assertTrue(result.contains("\"messageID\""));
        assertTrue(result.contains("\"messageHash\""));
        assertTrue(result.contains("Test message"));
        assertTrue(message.isStored());
        
        File file = new File(TEST_FILE);
        assertTrue(file.exists());
    }
    
    @Test
    public void testStoreMessageIncomplete() {
        String result = message.storeMessage();
        assertEquals("Cannot store incomplete message.", result);
    }
    
    @Test
    public void testStoreMessageNullMessage() {
        message.setRecipient("+27123456789");
        String result = message.storeMessage();
        assertEquals("Cannot store incomplete message.", result);
    }
    
    @Test
    public void testStoreMessageNullRecipient() {
        message.setMessage("Test");
        String result = message.storeMessage();
        assertEquals("Cannot store incomplete message.", result);
    }
    
    // ========== VALIDATE MESSAGE TESTS ==========
    
    @Test
    public void testValidateMessageValid() {
        String result = message.validateMessage("Hello world");
        assertEquals("Message ready to send.", result);
        assertEquals("Hello world", message.getMessage());
    }
    
    @Test
    public void testValidateMessageNull() {
        String result = message.validateMessage(null);
        assertEquals("Please enter a message of less than 250 characters.", result);
    }
    
    @Test
    public void testValidateMessageTooLong() {
        String longMessage = "a".repeat(260);
        String result = message.validateMessage(longMessage);
        assertTrue(result.contains("exceeds 250 characters"));
        assertTrue(result.contains("10"));
    }
    
    @Test
    public void testValidateMessageExactly250() {
        String exactMessage = "a".repeat(250);
        String result = message.validateMessage(exactMessage);
        assertEquals("Message ready to send.", result);
    }
    
    @Test
    public void testValidateMessageEmpty() {
        String result = message.validateMessage("");
        assertEquals("Message ready to send.", result);
    }
    
    // ========== GETTERS AND SETTERS TESTS ==========
    
    @Test
    public void testSetAndGetRecipient() {
        message.setRecipient("+27123456789");
        assertEquals("+27123456789", message.getRecipient());
    }
    
    @Test
    public void testSetAndGetSender() {
        message.setSender("Alice");
        assertEquals("Alice", message.getSender());
    }
    
    @Test
    public void testSetAndGetMessage() {
        message.setMessage("Test content");
        assertEquals("Test content", message.getMessage());
    }
    
    @Test
    public void testSetAndGetMessageID() {
        message.setMessageID("1234567890");
        assertEquals("1234567890", message.getMessageID());
    }
    
    @Test
    public void testSetAndGetMessageHash() {
        message.setMessageHash("AB:1:HELLO");
        assertEquals("AB:1:HELLO", message.getMessageHash());
    }
    
    // ========== DISPLAY TESTS ==========
    
    @Test
    public void testDisplayFullDetails() {
        message.setSender("John");
        message.setRecipient("+27123456789");
        message.setMessage("Hello");
        message.setMessageHash("AB:1:HELLO");
        
        String details = message.displayFullDetails();
        assertTrue(details.contains("Message ID"));
        assertTrue(details.contains("John"));
        assertTrue(details.contains("+27123456789"));
        assertTrue(details.contains("Hello"));
    }
    
    @Test
    public void testGetMessageIDGenerated() {
        String result = message.getMessageIDGenerated();
        assertTrue(result.contains("Message ID generated:"));
        assertTrue(result.contains(message.getMessageID()));
    }
    
    // ========== RESET TESTS ==========
    
    @Test
    public void testResetClearsMessages() {
        message.SentMessage(1);
        assertEquals(1, Message.returnTotalMessagess());
        
        Message.reset();
        assertEquals(0, Message.returnTotalMessagess());
        assertEquals("No messages have been sent.", Message.printMessages());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testMessageHashSingleWord() {
        message.setMessage("Hello");
        String hash = message.createMessageHash();
        assertNotNull(hash);
        String[] parts = hash.split(":");
        assertEquals(3, parts.length);
    }
    
    @Test
    public void testMessageHashWithNumbers() {
        message.setMessage("Hello 123 world 456");
        String hash = message.createMessageHash();
        assertNotNull(hash);
    }
    
    @Test
    public void testMultipleStoreMessagesAppend() {
        message.setMessage("First");
        message.setRecipient("+27123456789");
        message.setSender("A");
        message.storeMessage();
        
        Message msg2 = new Message();
        msg2.setMessage("Second");
        msg2.setRecipient("+27987654321");
        msg2.setSender("B");
        msg2.storeMessage();
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
            assertTrue(content.contains("First"));
            assertTrue(content.contains("Second"));
        } catch (IOException e) {
            fail("Could not read test file");
        }
    }
}