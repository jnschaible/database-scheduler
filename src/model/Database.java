/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import database.scheduler.DatabaseScheduler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBConnection;

/**
 *
 * @author jnsch
 */
public class Database {
    
    private static Connection connection;
    private static ObservableList<Appointment> myAppointments = FXCollections.observableArrayList();
    private static ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
    private static ObservableList<Customer> customers = FXCollections.observableArrayList();
    
    public Database() {
    }
    
    public void addAppointment(Appointment appointment) {
        connection = DBConnection.getConnection();
        /*
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO appointment(customerId, userId, title, description, location, contact, type, start, end) ");
            sql.append("VALUES(");
            sql.append
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        myAppointments.add(appointment);
    }
    
    public void deleteAppointment(Appointment appointment) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM appointment WHERE appointmentId = ?;");
            ps.setString(1, Integer.toString(appointment.getAppointmentID()));
            ResultSet rs = ps.executeQuery();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        myAppointments.remove(appointment);
    }
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
    
    public void deleteCustomer(Customer customer) {
        customers.remove(customer);
    }
    
    public ObservableList<Appointment> getAppointments() {
        return myAppointments;
    }
    
    public ObservableList<Customer> getCustomers() {
        return customers;
    }
    
    public ObservableList<String> getAppointmentTypes() {
        return appointmentTypes;
    }
    
    // These getters pull data from the SQL server
    public ObservableList<Appointment> getAppointmentsList() {
        connection = DBConnection.getConnection();
        
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM appointment;");
            //ps.setInt(1, 1);
            //System.out.println("sql query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int appointmentID = rs.getInt("appointmentId");
                Customer customer = this.getCustomer(rs.getInt("customerId"));
                User user = this.getUser(rs.getInt("userId"));
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String type = rs.getString("type");
                LocalDate date = rs.getTimestamp("start").toLocalDateTime().toLocalDate();
                LocalTime start = rs.getTimestamp("start").toLocalDateTime().toLocalTime();
                LocalTime end = rs.getTimestamp("end").toLocalDateTime().toLocalTime();
                myAppointments.add(new Appointment(appointmentID, customer, user, title, description,
                    location, contact, type, date, start, end));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myAppointments;
    }
    
    public ObservableList<String> getAppointmentTypesList() {
        connection = DBConnection.getConnection();
        
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT type FROM appointment;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointmentTypes.add(rs.getString("type"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return appointmentTypes;
    }
    
    public ObservableList<Customer> getCustomersList() {
        connection = DBConnection.getConnection();
        
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int customerID = rs.getInt("customerId");
                String customerName = rs.getString("customerName");
                Address address = this.getAddress(rs.getInt("addressId"));
                customers.add(new Customer(customerID, customerName, address));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return customers;
    }
    
    public Customer getCustomer(int customerID) {
        connection = DBConnection.getConnection();
        Customer customer = new Customer();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer WHERE customerId = ?;");
            ps.setInt(1, customerID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String customerName = rs.getString("customerName");
                Address address = this.getAddress(rs.getInt("addressId"));
                customer = new Customer(customerID, customerName, address);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return customer;
    }
    
    public Address getAddress(int addressID) {
        connection = DBConnection.getConnection();
        Address address = new Address();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM address WHERE addressId = ?;");
            ps.setInt(1, addressID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String addressLine1 = rs.getString("address");
                String addressLine2 = rs.getString("address2");
                City city = this.getCity(rs.getInt("cityId"));
                String postalCode = rs.getString("postalCode");
                String phoneNumber = rs.getString("phone");
                address = new Address(addressID, addressLine1, addressLine2, city, postalCode, phoneNumber);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }
    
    public City getCity(int cityID) {
        connection = DBConnection.getConnection();
        City city = new City();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM city WHERE cityId = ?;");
            ps.setInt(1, cityID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String cityName = rs.getString("city");
                Country country = this.getCountry(rs.getInt("countryId"));
                city = new City(cityID, cityName, country);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return city;
    }
    
    public Country getCountry(int countryID) {
        connection = DBConnection.getConnection();
        Country country = new Country();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM country WHERE countryId = ?;");
            ps.setInt(1, countryID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String countryName = rs.getString("country");
                country = new Country(countryID, countryName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return country;
    }
    
    public User getUser(int userID) {
        connection = DBConnection.getConnection();
        User user = new User();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE userId = ?;");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("userName");
                user = new User(userID, userName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
}
