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

/**
 * @author Aleksi Putkonen
 */
public class FXMLSuorituksetController implements Initializable {
    public Connection conn;
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
    
    public void siirryMenuun (ActionEvent event) throws IOException{	
	Parent Toimipisteikkuna = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
	Scene tableviewScene = new Scene(Toimipisteikkuna);	
	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	window.setScene(tableviewScene);
	window.show();
	}
    public void kirjauduUlos (ActionEvent event) throws IOException{	
	Parent Toimipisteikkuna = FXMLLoader.load(getClass().getResource("Kirjautumisikkuna.fxml"));
	Scene tableviewScene = new Scene(Toimipisteikkuna);
	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	window.setScene(tableviewScene);
	window.show();
	}    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            conn = avaaYhteys("jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306?user=opiskelija&password=opiskelija");
		kaytaTietokantaa(conn, "1804211_kloh");
    }    

    //käytetään tietokantaa
    private static void kaytaTietokantaa(Connection c, String tkanta) {
        try {
            Statement stmt = c.createStatement();

            stmt.executeQuery("USE " + tkanta);
            System.out.println("\t>> Käytetän tietokantaa " + tkanta);

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
		lisaaSuoritus(conn, Integer.parseInt(tf1.getText()),  Integer.parseInt(tf2.getText()),  Integer.parseInt(tf3.getText()), tf4.getText());
	}
    }
private static void lisaaSuoritus(Connection c, int opiskelija_id, int kurssi_id, int arvosana, String suoritus_pvm) {
        try {
	//Etsitään tietokannasta onko ID jo tietokannassa
		PreparedStatement ps = c.prepareStatement
		("SELECT opiskelija_id FROM Opintosuoritus WHERE opiskelija_id = ?");
		ps.setInt (1, opiskelija_id);
		ResultSet rs = ps.executeQuery();

                //Jos ID on jo tietokannassa, ilmoitetaan virheellä
                if (rs.next()) {
                    System.out.println("Opiskelijan suoritus on jo olemassa!");// ID jo tietokannassa
                    
                } else {
                    PreparedStatement ps1 = c.prepareStatement (
				"INSERT INTO Opintosuoritus (opiskelija_id, kurssi_id, arvosana, suortitus_pvm) " + 
				" VALUES (?,?,?,?)"
				);
                                ps1.setInt(1, opiskelija_id);
                                ps1.setInt(2, kurssi_id);
                                ps1.setInt(3, arvosana);
                                ps1.setString(4, suoritus_pvm);
                                ps1.execute();
                                naytaPopup("", "Lisätty opsikelijan " + opiskelija_id + " suoritus tietokantaan");
                                System.out.println("\t>>Lisätty " + opiskelija_id); 
                        }
                
            }catch (SQLException e) {
                e.printStackTrace();
            }
    }
 public void haeKannasta() {
		int id = Integer.parseInt(tf1.getText());
		if (tf1.getText().equals("")) {
			System.out.println("Virhe, tarkista että ID kentässä on tietoa");
		} else { 
			try {
				//Etsitään tietokannasta onko ID jo tietokannassa
				PreparedStatement ps = conn.prepareStatement
				("SELECT opiskelija_id, kurssi_id, arvosana FROM Opintosuoritus WHERE opiskelija_id = ?");
				ps.setInt (1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next () == true){
				tf2.setText(rs.getString(1));
                tf3.setText(rs.getString(2));
                tf4.setText(rs.getString(3));
				}
			} catch (SQLException e) {
					e.printStackTrace();
			}
                }
 }
    public void poistaTietokannasta() {
        if (tf1.getText().equals("")){
            System.out.println("Virhe, tarkista että ID kentässä on tietoa");
        } else { 
			boolean paperinenLasku; 
			paperinenLasku = vahvistusikkuna("", "Poistetaanko suoritus?");
			if (paperinenLasku) {
				System.out.println("Poistetaan");
				poistaAsiakas(conn, Integer.parseInt(tf1.getText()));
			}       
        }
    }
    
    private void poistaAsiakas(Connection c, int opiskelia_id) {
        try {
            
            PreparedStatement ps = c.prepareStatement
            ("SELECT opiskelia_id FROM Opintosuoritus WHERE opiskelija_id = ?");
            ps.setInt (1, opiskelia_id);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                    //ID tietokannassa, joten poistetaan
                    PreparedStatement ps1 = c.prepareStatement(
                    "DELETE FROM Opintosuoritus WHERE opiskelia_id = ?");
                    ps1.setInt(1, opiskelia_id);
                    ps1.execute();
                    System.out.println("\t>> Poistettu " + opiskelia_id);
            } else {
                //ei ole tietokannassa
                System.out.println("ID ei ole tietokannassa");
            }
 
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
    }
 
	public void muutaTietokannassa() {
		if (tf1.getText().equals("") ||  tf2.getText().equals("") || tf3.getText().equals("") || tf4.getText().equals("")) {
			System.out.println("Virhe, tarkista että kaikissa kentissä on tietoa");
		} else { 
			muutaSuoritus(conn, Integer.parseInt(tf1.getText()),  Integer.parseInt(tf2.getText()), Integer.parseInt(tf3.getText()), tf4.getText());
		}
	}
	
	private void muutaSuoritus(Connection c, int opiskelija_id, int kurssi_id, int arvosana, String suoritus_pvm) {
        try {
            
            PreparedStatement ps = c.prepareStatement
            ("SELECT opiskelija_id FROM Opintosuoritus WHERE opiskelija_id = ?");
            ps.setInt (1, opiskelija_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                    //ID tietokannassa, joten muutetaan
                    PreparedStatement ps1 = c.prepareStatement(
					"REPLACE INTO Opintosuoritus (opiskelija_id, kurssi_id, arvosana, suoritus_pvm) " + 
					" VALUES (?,?,?,?)"
					);
					ps1.setInt(1, opiskelija_id);
					ps1.setInt(2, kurssi_id);
					ps1.setInt(3, arvosana);
					ps1.setString(4, suoritus_pvm);
					ps1.execute();
					naytaPopup("", "Muutettu opiskelijan " + opiskelija_id + " tietoja");
					System.out.println("\t>> Muutettu " + opiskelija_id);
            } else {
                //ei ole tietokannassa
                System.out.println("ID ei ole tietokannassa");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
}