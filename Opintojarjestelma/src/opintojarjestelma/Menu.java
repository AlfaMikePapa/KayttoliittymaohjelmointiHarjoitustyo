package opintojarjestelma;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Menu implements Initializable{
    
        public Connection conn;
        
        public void initialize(URL url, ResourceBundle rb) {
        conn = avaaYhteys("jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306?user=opiskelija&password=opiskelija");
		kaytaTietokantaa(conn, "1804211_kloh");
    }   
        private static void kaytaTietokantaa(Connection c, String tkanta) {
        try{
            Statement stmt = c.createStatement();
            
            stmt.executeQuery("USE " + tkanta);
            System.out.print("\t>> Käytetän tietokantaa " + tkanta);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Avaa yhteyden tietokantaan
    private static Connection avaaYhteys(String connString) {
        try {
            Connection yhteys = DriverManager.getConnection(connString);
            System.out.println("\t>> Yhteys ok");
            return yhteys;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }


//Ulos
    public void logout (ActionEvent event) throws IOException{
        
	Parent ikkuna = FXMLLoader.load(getClass().getResource("FXMLKirjautuminen.fxml"));
	Scene tableviewScene = new Scene(ikkuna);
        
	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
	window.setScene(tableviewScene);
	window.show();
    }
    
    //Metodi helpdeskin ikkunan avaamiseen
    public void helpdesk (ActionEvent event) throws IOException{
		
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	alert.setTitle("Apua");
	alert.setHeaderText("Ongelmatapauksissa voit tavoittaa helpdeskin oheisilla tavoilla");
	alert.setContentText("Puh: 000-0000000 \rMail: xxxxx.xxxx@helpdesk.fi");
	alert.showAndWait();	
        
    }
    @FXML
    //Muihin ikkunoihin siirtyminen
    public void menustaopiskelijoihin (ActionEvent event) throws IOException {

        Parent Opiskelija = FXMLLoader.load(getClass().getResource("FXMLOpiskelija.fmxl"));
        Scene tableviewScene = new Scene(Opiskelija);
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableviewScene);
        window.show();

    }
    @FXML
    public void menustakursseihin (ActionEvent event) throws IOException {
        
        Parent Kurssit = FXMLLoader.load(getClass().getResource("FXMLKurssit.fmxl"));
        Scene tableviewScene = new Scene(Kurssit);
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableviewScene);
        window.show();
    
    }
    @FXML
    public void menustasuorituksiin (ActionEvent event) throws IOException {
        
        Parent Suoritukset = FXMLLoader.load(getClass().getResource("FXMLSuoritukset.fmxl"));
        Scene tableviewScene = new Scene(Suoritukset);
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableviewScene);
        window.show();
    
    
    }

    
}
