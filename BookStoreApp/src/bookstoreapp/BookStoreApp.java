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
    
    public static void main(String[] args) {
        BookStoreApp app = new BookStoreApp();
        app.controlProgram();
    }
    
    // This method will control the programs flow
    private void controlProgram(){
        while(!connected) {
            connected = getConnection();
        }
        System.out.println("");
    
    }
    
    /*
    Gets the connection
    Since this is only a prototype with no dedicated server it will also add all the schema and tables
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
                                     description varchar(255) NOT NULL,
                                     price DOUBLE NOT NULL,
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
                                            CREATE TABLE IF NOT EXISTS transaction (
                                            transactionID int AUTO_INCREMENT,
                                            userID int,
                                            bookIDs varchar(255),
                                            price DOUBLE NOT NULL,
                                            type varchar(255),
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
            
            return true;
            
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
    }
    
    
}
