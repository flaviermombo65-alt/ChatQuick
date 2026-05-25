import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// ============================================
// JUNIT 4 TESTS FOR QuickChat
// ============================================

public class QuickChatAppTest {
    
    private QuickChatApp app;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @Before
    public void setUp() {
        app = new QuickChatApp();
        System.setOut(new PrintStream(outputStream));
    }
    
    @After
    public void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }
    
    // ========== USERNAME VALIDATION TESTS ==========
    
    @Test
    public void testValidUsernameWithNumber() {
        assertTrue(QuickChatApp.isValidUsername("user123"));
        assertTrue(QuickChatApp.isValidUsername("john5"));
        assertTrue(QuickChatApp.isValidUsername("1admin"));
    }
    
    @Test
    public void testInvalidUsernameNoNumber() {
        assertFalse(QuickChatApp.isValidUsername("username"));
        assertFalse(QuickChatApp.isValidUsername("john"));
        assertFalse(QuickChatApp.isValidUsername("admin"));
    }
    
    @Test
    public void testInvalidUsernameNullOrEmpty() {
        assertFalse(QuickChatApp.isValidUsername(null));
        assertFalse(QuickChatApp.isValidUsername(""));
    }
    
    // ========== PASSWORD VALIDATION TESTS ==========
    
    @Test
    public void testValidPassword() {
        assertTrue(QuickChatApp.isValidPassword("Password1!"));
        assertTrue(QuickChatApp.isValidPassword("HelloWorld9#"));
        assertTrue(QuickChatApp.isValidPassword("Test123$"));
    }
    
    @Test
    public void testInvalidPasswordTooShort() {
        assertFalse(QuickChatApp.isValidPassword("Pass1!"));
        assertFalse(QuickChatApp.isValidPassword("A1!"));
    }
    
    @Test
    public void testInvalidPasswordNoUppercase() {
        assertFalse(QuickChatApp.isValidPassword("password1!"));
        assertFalse(QuickChatApp.isValidPassword("hello123#"));
    }
    
    @Test
    public void testInvalidPasswordNoDigit() {
        assertFalse(QuickChatApp.isValidPassword("Password!!"));
        assertFalse(QuickChatApp.isValidPassword("HelloWorld#"));
    }
    
    @Test
    public void testInvalidPasswordNoSpecial() {
        assertFalse(QuickChatApp.isValidPassword("Password123"));
        assertFalse(QuickChatApp.isValidPassword("HelloWorld9"));
    }
    
    @Test
    public void testInvalidPasswordNull() {
        assertFalse(QuickChatApp.isValidPassword(null));
    }
    
    // ========== PHONE NUMBER VALIDATION TESTS ==========
    
    @Test
    public void testValidPhoneNumber() {
        assertTrue(QuickChatApp.isValidPhoneNumber("+27123456789"));
        assertTrue(QuickChatApp.isValidPhoneNumber("+27000000000"));
    }
    
    @Test
    public void testInvalidPhoneNumber() {
        assertFalse(QuickChatApp.isValidPhoneNumber("27123456789"));
        assertFalse(QuickChatApp.isValidPhoneNumber("+2712345678"));
        assertFalse(QuickChatApp.isValidPhoneNumber("+271234567890"));
        assertFalse(QuickChatApp.isValidPhoneNumber("+27abc12345"));
        assertFalse(QuickChatApp.isValidPhoneNumber(""));
        assertFalse(QuickChatApp.isValidPhoneNumber(null));
    }
    
    // ========== REGISTRATION TESTS ==========
    
    @Test
    public void testSuccessfulRegistration() {
        boolean result = app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        assertTrue(result);
        assertEquals("John", app.getFirstName());
        assertEquals("Doe", app.getLastName());
        assertEquals("john123", app.getUsername());
        assertEquals("+27123456789", app.getPhoneNumber());
    }
    
    @Test
    public void testRegistrationInvalidUsername() {
        boolean result = app.registerUser("John", "Doe", "johndoe", "Password1!", "+27123456789");
        assertFalse(result);
    }
    
    @Test
    public void testRegistrationInvalidPassword() {
        boolean result = app.registerUser("John", "Doe", "john123", "password", "+27123456789");
        assertFalse(result);
    }
    
    @Test
    public void testRegistrationInvalidPhone() {
        boolean result = app.registerUser("John", "Doe", "john123", "Password1!", "12345");
        assertFalse(result);
    }
    
    // ========== LOGIN TESTS ==========
    
    @Test
    public void testSuccessfulLogin() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        boolean result = app.login("john123", "Password1!");
        assertTrue(result);
        assertTrue(app.isLoggedIn());
    }
    
    @Test
    public void testLoginWrongUsername() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        boolean result = app.login("wronguser", "Password1!");
        assertFalse(result);
        assertFalse(app.isLoggedIn());
        assertEquals(1, app.getLoginAttempts());
    }
    
    @Test
    public void testLoginWrongPassword() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        boolean result = app.login("john123", "wrongpass");
        assertFalse(result);
        assertFalse(app.isLoggedIn());
    }
    
    @Test
    public void testAccountLockAfterThreeAttempts() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        
        app.login("wrong", "wrong");
        app.login("wrong", "wrong");
        app.login("wrong", "wrong");
        
        assertTrue(app.isAccountLocked());
        assertFalse(app.login("john123", "Password1!"));
    }
    
    @Test
    public void testLoginWithoutRegistration() {
        boolean result = app.login("anyuser", "anypass");
        assertFalse(result);
    }
    
    // ========== MESSAGE STORAGE TESTS ==========
    
    @Test
    public void testInitializeMessageStorage() {
        boolean result = app.initializeMessageStorage(5);
        assertTrue(result);
        assertEquals(5, app.getMessageLimit());
        assertEquals(0, app.getMessageCount());
    }
    
    @Test
    public void testInitializeMessageStorageInvalid() {
        assertFalse(app.initializeMessageStorage(0));
        assertFalse(app.initializeMessageStorage(-1));
    }
    
    // ========== SEND MESSAGE TESTS ==========
    
    @Test
    public void testSendMessageWhenLoggedIn() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(3);
        
        boolean result = app.sendMessage("Hello!", "Alice", "John");
        assertTrue(result);
        assertEquals(1, app.getMessageCount());
    }
    
    @Test
    public void testSendMessageWhenNotLoggedIn() {
        app.initializeMessageStorage(3);
        boolean result = app.sendMessage("Hello!", "Alice", "John");
        assertFalse(result);
    }
    
    @Test
    public void testSendMessageLimitReached() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(2);
        
        app.sendMessage("Msg 1", "Alice", "John");
        app.sendMessage("Msg 2", "Bob", "John");
        
        assertTrue(app.hasReachedLimit());
        assertFalse(app.sendMessage("Msg 3", "Charlie", "John"));
    }
    
    @Test
    public void testSendEmptyMessageContent() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(3);
        
        assertFalse(app.sendMessage("", "Alice", "John"));
        assertFalse(app.sendMessage("   ", "Alice", "John"));
        assertFalse(app.sendMessage(null, "Alice", "John"));
    }
    
    @Test
    public void testSendEmptyRecipient() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(3);
        
        assertFalse(app.sendMessage("Hello", "", "John"));
        assertFalse(app.sendMessage("Hello", null, "John"));
    }
    
    @Test
    public void testSendEmptySender() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(3);
        
        assertFalse(app.sendMessage("Hello", "Alice", ""));
        assertFalse(app.sendMessage("Hello", "Alice", null));
    }
    
    // ========== GET MESSAGES TESTS ==========
    
    @Test
    public void testGetRecentMessages() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(3);
        
        app.sendMessage("Hello Alice", "Alice", "John");
        app.sendMessage("Hi Bob", "Bob", "John");
        
        String[][] messages = app.getRecentMessages();
        assertEquals(2, messages.length);
        assertEquals("John", messages[0][0]);
        assertEquals("Alice", messages[0][1]);
        assertEquals("Hello Alice", messages[0][2]);
        assertEquals("John", messages[1][0]);
        assertEquals("Bob", messages[1][1]);
        assertEquals("Hi Bob", messages[1][2]);
    }
    
    @Test
    public void testGetRecentMessagesEmpty() {
        app.initializeMessageStorage(3);
        String[][] messages = app.getRecentMessages();
        assertEquals(0, messages.length);
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testMultipleMessagesUpToLimit() {
        app.registerUser("John", "Doe", "john123", "Password1!", "+27123456789");
        app.login("john123", "Password1!");
        app.initializeMessageStorage(100);
        
        for (int i = 0; i < 100; i++) {
            assertTrue(app.sendMessage("Message " + i, "User" + i, "John"));
        }
        
        assertEquals(100, app.getMessageCount());
        assertTrue(app.hasReachedLimit());
    }
    
    @Test
    public void testPasswordExactlyEightChars() {
        assertTrue(QuickChatApp.isValidPassword("Pass1!ab"));
    }
    
    @Test
    public void testPasswordMinimumRequirements() {
        assertTrue(QuickChatApp.isValidPassword("A1!aaaaa"));
    }
    
    @Test
    public void testUsernameOnlyNumbers() {
        assertTrue(QuickChatApp.isValidUsername("12345"));
    }
    
    @Test
    public void testUsernameNumberAtEnd() {
        assertTrue(QuickChatApp.isValidUsername("user1"));
    }
    
    @Test
    public void testUsernameNumberAtStart() {
        assertTrue(QuickChatApp.isValidUsername("1user"));
    }
}