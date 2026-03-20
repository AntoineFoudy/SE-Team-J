/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bookstoreapp;

import java.sql.*;

/**
 *
 * @author afoud
 */
public class DBClass {
    
    private Connection con;
    private boolean connected;
    
    private String url = "jdbc:mysql://localhost:3306/TeamJBookStore";
    private String user = "root";
    private String password = "password";
    
    
    
    public DBClass() {
        try{
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to DB");
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
    }
    
    // Retive the user by email and password and return the PK of userID to give the system the ability to change all the required
    // function the user requires
    public int loginUser(String email, String password) {
        int userID = 0;
        
        String findUserByEmailAndPassword = """
                                          SELECT userID FROM users
                                          WHERE email = ?
                                          AND password = ?
                                          """;
        
        try(PreparedStatement pstatement = con.prepareStatement(findUserByEmailAndPassword)) {
           pstatement.setString(1, email);
           pstatement.setString(2, password);
           
           try(ResultSet rs = pstatement.executeQuery()) {
               if (rs.next()) {
                   userID = rs.getInt("userID");
               }
           }
           
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
        return userID;        
    }
    
    // Create new User
    public int createUser(String name, String email, String password) {
        String emailExist = "This Email already in use";
        String accountCreated = "Accound has been created";
        
        String checkUserEmail = """
                                SELECT userID FROM users
                                WHERE email = ?
                                """;
        
        String createUserByEmailAndPassword = """
                                              INSERT INTO users (name, email, password)
                                              VALUES (
                                              ?,
                                              ?,
                                              ?
                                              )
                                              """;
        
        // Check if Email already Exists in the system
        try(PreparedStatement pstatement = con.prepareStatement(checkUserEmail)) {
            pstatement.setString(1, email);
            
            try(ResultSet rs = pstatement.executeQuery()) {
               if(rs.next()) {
                    return 0;
               }
            }
            
            // Create new user
            try(PreparedStatement pstatementCreate = con.prepareStatement(createUserByEmailAndPassword, Statement.RETURN_GENERATED_KEYS)) {
                pstatementCreate.setString(1, name);
                pstatementCreate.setString(2, email);
                pstatementCreate.setString(3, password);
                
                // Return the new userID
                int affectedRows = pstatementCreate.executeUpdate();
                
                if(affectedRows > 0) {
                    try(ResultSet keys = pstatementCreate.getGeneratedKeys()) {
                        if(keys.next()) {
                            return keys.getInt(1);
                        }
                    }
                }
            }           
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
        // Error
        return -1;
    }
    
    // Add to basket
    public boolean addToBasket(int userId, int bookId) {
        boolean success = false;
        
        String insertBook = """
                            INSERT INTO basket (userID, bookID)
                            VALUES (
                            ?,
                            ?
                            )
                            """;
        try(PreparedStatement pstatementCreate = con.prepareStatement(insertBook)){
            pstatementCreate.setInt(1, userId);
            pstatementCreate.setInt(2, bookId);
            
            pstatementCreate.executeUpdate();
            
            success = true;
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
        return success;
    }
        

}

