/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bookstoreapp;

/**
 *
 * @author afoud
 */

import java.sql.*;

public class BookStoreApp {
    
    // JDBC dependent Variables
    private Connection con;
    private boolean connected;
    private LoginGUI lgui;
    
    public static void main(String[] args) {
        BookStoreApp app = new BookStoreApp();
        app.controlProgram();
    }
    
    // This method will control the programs flow
    private void controlProgram(){
        while(!connected) {
            connected = getConnection();
        }
        System.out.println("getConnection Method ran successfully");
        // If everything was successful open gui
        openGUI();
    
    }
    
    /*
    Gets the connection
    Since this is only a prototype with no dedicated server it will also add the schema and all the 
    tables and data for the books and a test user account
    */
    private boolean getConnection() {
        String schema = "TeamJBookStore";
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "password";
        
        try{
            con = DriverManager.getConnection(url, user, password);
            
            String createSchema = "CREATE DATABASE IF NOT EXISTS " + schema;
            try(Statement statement = con.createStatement()) {
                statement.executeUpdate(createSchema);
                System.out.println("Schema created if it did not exsits");
            }
            
            con.close();
            
            con = DriverManager.getConnection(url+schema, user, password);
            System.out.println("Connected to DB");
            
            String createTableUsers = """
                                  CREATE TABLE IF NOT EXISTS users (
                                  userID int AUTO_INCREMENT,
                                  name varchar(255) NOT NULL,
                                  email varchar(255) NOT NULL UNIQUE,
                                  password varchar(255) NOT NULL,
                                  PRIMARY KEY (userID)
                                  )
                                  """;
            String createTableBook = """
                                     CREATE TABLE IF NOT EXISTS book (
                                     bookID int AUTO_INCREMENT,
                                     name varchar(255) NOT NULL,
                                     isbn int NOT NULL UNIQUE,
                                     author varchar(255) NOT NULL,
                                     category varchar(255) NOT NULL,
                                     description varchar(255) NOT NULL,
                                     type varchar(255) NOT NULL,
                                     price DOUBLE(9,2) NOT NULL,
                                     stock int,
                                     PRIMARY KEY (bookID)
                                     )
                                     """;
            String createTableBasket = """
                                       CREATE TABLE IF NOT EXISTS basket (
                                       userID int,
                                       bookID int,
                                       FOREIGN KEY (userID) REFERENCES users(userID),
                                       FOREIGN KEY (bookID) REFERENCES book(bookID)
                                       )
                                       """;
            String createTableReview = """
                                       CREATE TABLE IF NOT EXISTS review (
                                       bookID int,
                                       review varchar (255),
                                       FOREIGN KEY (bookID) REFERENCES book(bookID)
                                       )
                                       """;
            String createTableTransaction = """
                                            CREATE TABLE IF NOT EXISTS transactions (
                                            transactionID int AUTO_INCREMENT,
                                            userID int NOT NULL,
                                            bookIDs varchar(255) NOT NULL,
                                            price DOUBLE DEFAULT(0) NOT NULL,
                                            type varchar(255) NOT NULL,
                                            dueDate DATE,
                                            PRIMARY KEY (transactionID),
                                            FOREIGN KEY (userID) REFERENCES users(userID)
                                            )
                                            """;
            
            try(Statement statement = con.createStatement()) {
                statement.executeUpdate(createTableUsers);
                statement.executeUpdate(createTableBook);
                statement.executeUpdate(createTableBasket);
                statement.executeUpdate(createTableReview);
                statement.executeUpdate(createTableTransaction);
            }
            
            String insertDataUsers = """
                                    INSERT IGNORE INTO users VALUES (
                                    1,
                                    "Test",
                                    "test@gmail.com",
                                    "password123")
                                    """;
            
            String insertDataBook1 = """
                                    INSERT IGNORE INTO book VALUES (
                                    1,
                                    "Harry Potter and the Philosipher's Stone",
                                    97814,
                                    "J.K. Rowling",
                                    "Fiction",
                                    "A story about a orphan wizard named Harry Potter",
                                    "normal",
                                    10,
                                    5
                                    )                                    
                                    """;
            String insertDataBook2 = """
                                    INSERT IGNORE INTO book VALUES (
                                    2,
                                    "Murder on the Orient Express",
                                    97800,
                                    "Agatha Christie",
                                    "Fiction",
                                    "Someone kills someone on a train",
                                    "normal",
                                    14.58,
                                    0
                                    )
                                    """;
            String insertDataBook3 = """
                                    INSERT IGNORE INTO book VALUES (
                                    3,
                                    "The Snowball",
                                    07475,
                                    "Alice Schroeder",
                                    "Biography",
                                    "The biography of Warren Buffet",
                                    "School",
                                    14.58,
                                    10
                                    )
                                    """;
            String insertDataBook4 = """
                                    INSERT IGNORE INTO book VALUES (
                                    4,
                                    "$100M Offers: How to Make Offers So Good People Feel Stupid Saying No",
                                    094273,
                                    "Alex Hormozi",
                                    "Business",
                                    "How to create offer so that people won't say no",
                                    "School",
                                    8.99,
                                    3
                                    )
                                    """;
            
            try(Statement statement = con.createStatement()) {
                statement.executeUpdate(insertDataUsers);
                statement.executeUpdate(insertDataBook1);
                statement.executeUpdate(insertDataBook2);
                statement.executeUpdate(insertDataBook3);
                statement.executeUpdate(insertDataBook4);
            }
            
            System.out.println("Added the data");
            
            return true;
            
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
    }
    
    private void openGUI() {
        this.lgui = new LoginGUI();
        lgui.setVisible(true);
    }
    
    
}
