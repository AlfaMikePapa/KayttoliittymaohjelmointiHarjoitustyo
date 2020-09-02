package opintojarjestelma;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLKurssitController implements Initializable {
    public Connection conn;
    //Textfieldit ja buttonit, joille annetty id scenebuilderissa
    public TextField tf1, tf2, tf3, tf4;
    public boolean vastaus;

	//näyttää popup ikkunnassa parametrina annetun viestin
    public static void naytaPopup(String ikkunanNimi, String viesti) {
        Stage window = new Stage();
        
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(ikkunanNimi);
        window.setMinWidth(300);
        window.setMinHeight(120);
        
        Label label = new Label();
        label.setText(viesti);
        Button closeButton = new Button("Sulje ikkuna");
        closeButton.setOnAction(e -> window.close());
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
	}
	//vahvistusikkuna
	public boolean vahvistusikkuna(String ikkunanNimi, String viesti) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(ikkunanNimi);
		window.setMinWidth(250);
		Label label = new Label();
		label.setText(viesti);
		
		Button yesButton = new Button("Kyllä");
		Button noButton = new Button("Peruuta");
		
		yesButton.setOnAction(e -> {
		vastaus = true;
		window.close();
		});
		
		noButton.setOnAction(e -> {
			vastaus = false;
			window.close();
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, yesButton, noButton);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		
		return vastaus;
	}
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conn = avaaYhteys("jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306?user=opiskelija&password=opiskelija");
		kaytaTietokantaa(conn, "1804211_kloh");
    }   
    
    //k�ytet��n tietokantaa
    private static void kaytaTietokantaa(Connection c, String tkanta) {
        try {
            Statement stmt = c.createStatement();

            stmt.executeQuery("USE " + tkanta);
            System.out.println("\t>> Käytetään tietokantaa " + tkanta);

        } catch (SQLException e) {
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
    
    
	public void vieTietokantaan() {
		//Tarkastetaan ovatko tektstikentät tyhjiä, jos ovat niin ei viedä tietokantaan mitään
		if (tf1.getText().equals("") ||  tf2.getText().equals("") || tf3.getText().equals("") || tf4.getText().equals("")) {
			System.out.println("Virhe, tarkista että kaikissa kentissä on tietoa");
		} else { 
			lisaaKurssi(conn, Integer.parseInt(tf1.getText()),  tf2.getText(),  Integer.parseInt(tf3.getText()), tf4.getText());
		}
	}
	private static void lisaaKurssi(Connection c, int kurssi_id, String nimi, int opintopisteet, String kuvaus) {
		try {
			//Etsitään tietokannasta onko ID jo tietokannassa
			PreparedStatement ps = c.prepareStatement
			("SELECT kurssi_id FROM Kurssi WHERE kurssi_id = ?");
			ps.setInt (1, kurssi_id);
			ResultSet rs = ps.executeQuery();

			//Jos ID on jo tietokannassa, ilmoitetaan virheellä
			if (rs.next()) {
				// ID jo tietokannassa
				System.out.println("Kurssi on jo tietokannassa");
			} else {
				// Asiakas ei ole tietokannassa, joten lisätään se sinne
				PreparedStatement ps1 = c.prepareStatement(
				"INSERT INTO asiakashallinta (kurssi_id, nimi, opintopisteet, kuvaus) " + 
				" VALUES (?,?,?,?)"
				);
				ps1.setInt(1, kurssi_id);
				ps1.setString(2, nimi);
				ps1.setInt(3, opintopisteet);
				ps1.setString(4, kuvaus);
				ps1.execute();
				naytaPopup("", "Lisätty kurssi " + nimi + " tietokantaan");
				System.out.println("\t>> Lisätty " + kurssi_id);
			}

		} catch (SQLException e) {
				e.printStackTrace();
		}
	}
 
    //Siirrytään takaisin menuun
    public void siirryMenuun (ActionEvent event) throws IOException{
		Parent Toimipisteikkuna = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
		Scene tableviewScene = new Scene(Toimipisteikkuna);
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(tableviewScene);
		window.show();
	}
    //Kirjaudutaan ulos
	public void kirjauduUlos (ActionEvent event) throws IOException{
		Parent Toimipisteikkuna = FXMLLoader.load(getClass().getResource("FXMLKirjautuminen.fxml"));
		Scene tableviewScene = new Scene(Toimipisteikkuna);
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(tableviewScene);
		window.show();
	}
}
