package com.elastonias.container.database;

import com.elastonias.container.model.Controller;
import com.elastonias.container.model.DIDObject;
import com.elastonias.container.model.Product;
import com.elastonias.container.util.CustomUtil;

import java.io.File;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DatabaseSQLite implements DatabaseInterface{
    private String dbUrl;

    public DatabaseSQLite(String dbName){
        File databaseDir=CustomUtil.getDirectoryByName("database");
        File dbFile=new File(databaseDir, dbName);
        this.dbUrl="jdbc:sqlite:"+dbFile.getPath();
        this.createTables();
    }

    private Connection getConnection(){
        Connection connection=null;
        try{
            connection=DriverManager.getConnection(this.dbUrl);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private void createTables(){
        String createProductsTableSql="CREATE TABLE IF NOT EXISTS product_table("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "did TEXT NOT NULL,"+
                "privateKey TEXT NOT NULL,"+
                "publicKey TEXT NOT NULL,"+
                "visible TEXT NOT NULL"+
                ");";
        String createControllersTableSql="CREATE TABLE IF NOT EXISTS controller_table("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "userId TEXT NOT NULL,"+
                "connectionState TEXT NOT NULL,"+
                "active INTEGER NOT NULL"+
                ");";
        String createDidobjectsTableSql="CREATE TABLE IF NOT EXISTS didobject_table("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "did TEXT NOT NULL,"+
                "publicKey TEXT NOT NULL,"+
                "privateKey TEXT NOT NULL"+
                ");";
        try{
            Connection connection=this.getConnection();
            Statement stmt=connection.createStatement();
            stmt.execute(createProductsTableSql);
            stmt.execute(createControllersTableSql);
            stmt.execute(createDidobjectsTableSql);
            stmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Product saveProduct(Product product){
        Product newProduct=null;
        String insertSql="INSERT INTO product_table(did, privateKey, publicKey, visible) VALUES(?,?,?,?);";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, product.getDid());
            pstmt.setString(2, product.getPrivateKey());
            pstmt.setString(3, product.getPublicKey());
            pstmt.setBoolean(4, product.getVisible());
            pstmt.executeUpdate();

            ResultSet rs=pstmt.getGeneratedKeys();
            rs.next();
            int createdId=rs.getInt(1);
            newProduct=product;
            newProduct.setId(createdId);

            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            newProduct=null;
        }
        return newProduct;
    }

    public ArrayList<Product> getProductList(){
        ArrayList<Product> productList=new ArrayList<Product>();
        String selectSql="SELECT * FROM product_table;";
        try{
            Connection connection=this.getConnection();
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery(selectSql);
            while(rs.next()){
                Product product=new Product(
                        rs.getInt("id"),
                        rs.getString("did"),
                        rs.getString("privateKey"),
                        rs.getString("publicKey"),
                        rs.getBoolean("visible"));
                productList.add(product);
            }
            rs.close();
            stmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return productList;
    }

    public ArrayList<Product> getVisibleProductList(){
        ArrayList<Product> productList=new ArrayList<Product>();
        String selectSql="SELECT * FROM product_table WHERE visible=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
            pstmt.setBoolean(1, true);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next()){
                Product product=new Product(
                        rs.getInt("id"),
                        rs.getString("did"),
                        rs.getString("privateKey"),
                        rs.getString("publicKey"),
                        rs.getBoolean("visible"));
                productList.add(product);
            }
            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return productList;
    }

    public Product getProductByDid(String did){
        Product product=null;
        String selectSql="SELECT * FROM product_table WHERE did=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
            pstmt.setString(1, did);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()){
                product=new Product(
                        rs.getInt("id"),
                        rs.getString("did"),
                        rs.getString("privateKey"),
                        rs.getString("publicKey"),
                        rs.getBoolean("visible"));
            }
            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            product=null;
        }
        return product;
    }

    public boolean updateProduct(Product product){
        boolean response=true;
        String updateSql="UPDATE product_table SET did=?, privateKey=?, publicKey=?, visible=? WHERE id=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
            pstmt.setString(1, product.getDid());
            pstmt.setString(2, product.getPrivateKey());
            pstmt.setString(3, product.getPublicKey());
            pstmt.setBoolean(4, product.getVisible());
            pstmt.setInt(5, product.getId());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public boolean setProductListVisible(boolean visible){
        boolean response=true;
        String updateSql="UPDATE product_table SET visible=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
            pstmt.setBoolean(1, visible);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public boolean setControllerListDeactivated(){
        boolean response=true;
        String updateSql="UPDATE controller_table SET active=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
            pstmt.setBoolean(1, false);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public Controller getActiveController(){
        Controller controller=null;
        String selectSql="SELECT * FROM controller_table WHERE active=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
            pstmt.setBoolean(1, true);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()){
                controller=new Controller(rs.getInt("id"), rs.getString("userId"), rs.getString("connectionState"), rs.getBoolean("active"));
            }
            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            controller=null;
        }
        return controller;
    }

    public Controller saveController(Controller controller){
        Controller newController=null;
        String insertSql="INSERT INTO controller_table(userId, connectionState, active) VALUES(?,?,?);";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, controller.getUserId());
            pstmt.setString(2, controller.getConnectionState());
            pstmt.setBoolean(3, controller.getActive());
            pstmt.executeUpdate();

            ResultSet rs=pstmt.getGeneratedKeys();
            rs.next();
            int createdId=rs.getInt(1);
            newController=controller;
            newController.setId(createdId);

            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            newController=null;
        }
        return newController;
    }

    public Controller getControllerByUserId(String userId){
        Controller controller=null;
        String selectSql="SELECT * FROM controller_table WHERE userId=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
            pstmt.setString(1, userId);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()){
                controller=new Controller(rs.getInt("id"), rs.getString("userId"), rs.getString("connectionState"), rs.getBoolean("active"));
            }
            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            controller=null;
        }
        return controller;
    }

    public boolean updateController(Controller controller){
        boolean response=true;
        String updateSql="UPDATE controller_table SET userId=?, connectionState=?, active=? WHERE id=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
            pstmt.setString(1, controller.getUserId());
            pstmt.setString(2, controller.getConnectionState());
            pstmt.setBoolean(3, controller.getActive());
            pstmt.setInt(4, controller.getId());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public boolean setControllerListConnectionState(String connectionState){
        boolean response=true;
        String updateSql="UPDATE controller_table SET connectionState=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
            pstmt.setString(1, connectionState);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public boolean deleteControllerByUserId(String userId){
        boolean response=true;
        String deleteSql="DELETE FROM controller_table WHERE userId=?;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public boolean deleteProducts(){
        boolean response=true;
        String deleteSql="DELETE FROM product_table;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public DIDObject saveDIDObject(DIDObject didObject){
        DIDObject newDidobject=null;
        String insertSql="INSERT INTO didobject_table(did, publicKey, privateKey) VALUES(?,?,?);";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, didObject.getDid());
            pstmt.executeUpdate();

            ResultSet rs=pstmt.getGeneratedKeys();
            rs.next();
            int createdId=rs.getInt(1);
            newDidobject=didObject;
            newDidobject.setId(createdId);

            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            newDidobject=null;
        }
        return newDidobject;
    }

    public boolean deleteDIDObjects(){
        boolean response=true;
        String deleteSql="DELETE FROM didobject_table;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            response=false;
        }
        return response;
    }

    public DIDObject getDIDObject(){
        DIDObject didObject=null;
        String selectSql="SELECT * FROM didobject_table;";
        try{
            PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()){
                didObject=new DIDObject(rs.getInt("id"), rs.getString("did"), rs.getString("publicKey"), rs.getString("privateKey"));
            }
            rs.close();
            pstmt.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            didObject=null;
        }
        return didObject;
    }
}