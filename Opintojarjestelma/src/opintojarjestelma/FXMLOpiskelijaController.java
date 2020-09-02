package opintojarjestelma;

import java.sql.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLOpiskelijaController implements Initializable {
    
    //Yhteys
    public Connection conn;
    //Tietojen textfieldit
    public TextField tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8;
    public TextArea txtArea;
    public boolean vastaus;

    
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
    //Takaisin päävalikkoon
    public void siirryMenuun (ActionEvent event) throws IOException{
        
	Parent Menu = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
	Scene tableviewScene = new Scene(Menu);	
        
	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();	
        
	window.setScene(tableviewScene);
	window.show();  
    }
    
    public void kirjauduUlos (ActionEvent event) throws IOException{
        
	Parent Toimipisteikkuna = FXMLLoader.load(getClass().getResource("FXMLKirjautuminen.fxml"));
	Scene tableviewScene = new Scene(Toimipisteikkuna);
        
	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();	
        
	window.setScene(tableviewScene);
	window.show();
	}
    
    //Avaa yhteys tietokantaan valikon avautuessa
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = avaaYhteys("jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306?user=opiskelija&password=opiskelija");
                kaytaTietokantaa(conn, "1804211_kloh"); 
    }
    //käytetään tietokantaa
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
    
    public void vieTietokantaan() {
                //Tarkastetaan ovatko tektstikentät tyhjiä, jos ovat niin ei viedä tietokantaan mitään
		if (tf1.getText().equals("") ||  tf2.getText().equals("") || tf3.getText().equals("") || tf4.getText().equals("") || tf5.getText().equals("") || tf6.getText().equals("") || tf7.getText().equals("") || tf8.getText().equals("")) {
			System.out.println("Virhe, tarkista että kaikissa kentissä on tietoa");
		} else { 
			lisaaopiskelija(conn, Integer.parseInt(tf1.getText()),  tf2.getText(),  tf3.getText(), tf4.getText(), tf5.getText(), (tf6.getText()),  tf7.getText(), tf8.getText());
		}
	}
    private static void lisaaopiskelija(Connection c, int opiskelija_id, String etunimi, String sukunimi, String lahiosoite, String postitoimipaikka, String postinro, String email, String puhelinnro) {
		try {
			//Etsitään tietokannasta onko ID jo tietokannassa
			PreparedStatement ps = c.prepareStatement
			("SELECT opiskelija_id FROM Opiskelija WHERE opiskelija_id = ?");
			ps.setInt (1, opiskelija_id);
			ResultSet rs = ps.executeQuery();

			//Jos ID on jo tietokannassa, ilmoitetaan virheellä
			if (rs.next()) {
				// ID jo tietokannassa
				System.out.println("Opiskelija on jo tietokannassa");
			} else {
				// Asiakas ei ole tietokannassa, joten lisätään se sinne
				PreparedStatement ps1 = c.prepareStatement(
				"INSERT INTO Opiskelija (opiskelija_id, etunimi, sukunimi, lahiosoite, postitoimipaikka, postinro, email, puhelinnro) " + 
				" VALUES (?,?,?,?,?,?,?,?)"
				);
				ps1.setInt(1, opiskelija_id);
				ps1.setString(2, etunimi);
				ps1.setString(3, sukunimi);
				ps1.setString(4, lahiosoite);
				ps1.setString(5, postitoimipaikka);
				ps1.setString(6, postinro);
				ps1.setString(7, email);
                                ps1.setString(8, puhelinnro);
				ps1.execute();
				naytaPopup("", "Lisätty opiskelija " + etunimi + sukunimi + " tietokantaan");
				System.out.println("\t>> Lisätty " + opiskelija_id);
			}

		} catch (SQLException e) {
				e.printStackTrace();
		}
    }

    public void poistaTietokannasta() {
        if (tf1.getText().equals("")){
            System.out.println("Virhe, tarkista että ID kentässä on tietoa");
        } else { 
			boolean opiskelija; 
			opiskelija = vahvistusikkuna("", "Poistetaanko opiskelija?");
			if (opiskelija) {
				System.out.println("Poistetaan");
				poistaOpiskelija(conn, Integer.parseInt(tf1.getText()));
			}       
        }
    } 
    
    private void poistaOpiskelija(Connection c, int opiskelija_id) {
        try {
            
            PreparedStatement ps = c.prepareStatement
            ("SELECT opiskelija_id FROM Opiskelija WHERE opiskelija_id = ?");
            ps.setInt (1, opiskelija_id);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                    //ID tietokannassa, joten poistetaan
                    PreparedStatement ps1 = c.prepareStatement(
                    "DELETE FROM Opiskelija WHERE opiskelija_id = ?");
                    ps1.setInt(1, opiskelija_id);
                    ps1.execute();
                    System.out.println("\t>> Poistettu " + opiskelija_id);
            } else {
                //ei ole tietokannassa
                System.out.println("ID ei ole tietokannassa");
            }
 
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	public void muutaTietokannasta() {
		if (tf1.getText().equals("") ||  tf2.getText().equals("") || tf3.getText().equals("") || tf4.getText().equals("") || tf5.getText().equals("") || tf6.getText().equals("") || tf7.getText().equals("") || tf8.getText().equals("")){
			System.out.println("Virhe, tarkista että kaikissa kentissä on tietoa");
		} else { 
			muutaOpiskelija(conn, Integer.parseInt(tf1.getText()),  tf2.getText(), tf3.getText(), tf4.getText(), tf5.getText(), (tf6.getText()), tf7.getText(), tf8.getText());
		}
	}
	
	private void muutaOpiskelija(Connection c, int opiskelija_id, String etunimi, String sukunimi, String lahiosoite, String postitoimipaikka, String postinro, String email, String puhelinnro) {
        try {
            
            PreparedStatement ps = c.prepareStatement
            ("SELECT opiskelija_id FROM Opiskelija WHERE opiskelija_id = ?");
            ps.setInt (1, opiskelija_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                    //ID tietokannassa, joten muutetaan
                    PreparedStatement ps1 = c.prepareStatement(
					"REPLACE INTO Opiskelija (opiskelija_id, etunimi, sukunimi, lahiosoite, postitoimipaikka, postinro, email, puhelinnro) " + 
                                        " VALUES (?,?,?,?,?,?,?,?)"
					);
					ps1.setInt(1, opiskelija_id);
					ps1.setString(2, etunimi);
					ps1.setString(3, sukunimi);
					ps1.setString(4, lahiosoite);
					ps1.setString(5, postitoimipaikka);
					ps1.setString(6, postinro);
					ps1.setString(7, email);
                                        ps1.setString(8, puhelinnro);
					ps1.execute();
					naytaPopup("", "Muutettu opiskelijan " + etunimi + sukunimi + " tietoja");
					System.out.println("\t>> Muutettu " + opiskelija_id);
            } else {
                //ei ole tietokannassa
                System.out.println("ID ei ole tietokannassa");
            }

        } catch (SQLException e) {
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
				("SELECT etunimi, sukunimi, lahiosoite, postitoimipaikka, postinro, email, puhelinnro FROM Opiskelija WHERE opiskelija_id = ?");
				ps.setInt (1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next () == true){
					tf2.setText(rs.getString(1));
					tf3.setText(rs.getString(2));
					tf4.setText(rs.getString(3));
					tf5.setText(rs.getString(4));
					tf6.setText(Integer.toString(rs.getInt(5)));
					tf7.setText(rs.getString(6));
				}
			} catch (SQLException e) {
					e.printStackTrace();
			}
		}
	}
}
