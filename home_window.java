import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;


import javafx.scene.layout.HBox;


public class home_window extends Application
{
    static HBox text_pane, center_pane;
    static VBox button_pane = new VBox(), pane, field = new VBox();
    
    public static Stage stage;
    public static Scene scene;

    static int curr_id = -1;
    static boolean is_client;
    
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        stage = primaryStage;
        run();
    }
    
    static void set_button_pane()
    {
        Button search = new Button("Search");
        search.setFont(Font.font("Helvetica", 16));
        search.setMinWidth(200);
        search.setOnAction(e->search_click());

        Button indication = new Button("Drug by Indication");
        indication.setFont(Font.font("Helvetica", 16));
        indication.setMinWidth(200);
        indication.setOnAction(e->indication_click());

        Button drug_class = new Button("Drug by Class");
        drug_class.setFont(Font.font("Helvetica", 16));
        drug_class.setMinWidth(200);
        drug_class.setOnAction(e->class_click());

        Button generic = new Button("Drug by Generic");
        generic.setFont(Font.font("Helvetica", 16));
        generic.setMinWidth(200);
        generic.setOnAction(e->generic_drug_click());

        Button clientBuy = new Button("Buy Drug");
        clientBuy.setFont(Font.font("Helvetica", 16));
        clientBuy.setMinWidth(200);
        clientBuy.setOnAction(e->client_buy_click());

        Button pharmacyBuy = new Button("Buy Drug");
        pharmacyBuy.setFont(Font.font("Helvetica", 16));
        pharmacyBuy.setMinWidth(200);
        pharmacyBuy.setOnAction(e->pharmacy_buy_click());

        Button mostSearchedMedicine = new Button("Most Searched Medicine");
        mostSearchedMedicine.setFont(Font.font("Helvetica", 16));
        mostSearchedMedicine.setMinWidth(200);
        mostSearchedMedicine.setOnAction(e->most_searched_medicine());

        Button mostSearchedIndication = new Button("Most Searched Indication");
        mostSearchedIndication.setFont(Font.font("Helvetica", 16));
        mostSearchedIndication.setMinWidth(200);
        mostSearchedIndication.setOnAction(e->most_searched_indication());
        
        button_pane = new VBox(30, search, indication, drug_class, generic);
        if (login_plus_register_window.is_client)
            button_pane.getChildren().addAll(clientBuy, mostSearchedIndication, mostSearchedMedicine);
        else button_pane.getChildren().addAll(pharmacyBuy, mostSearchedIndication, mostSearchedMedicine);
    }

    static void run()
    {
        Text text_dims = new Text("DIMS");
        text_dims.setFont(Font.font("Century Schoolbook", 50));

        Region[] spacer = new Region[20];
        for (int i = 0; i < 20; i++)
        {
            spacer[i] = new Region();
            HBox.setHgrow(spacer[i], Priority.SOMETIMES);
        }

        text_pane = new HBox(0, spacer[2], text_dims, spacer[3]);
        center_pane = new HBox(50, button_pane, field);
        pane = new VBox(30, text_pane, center_pane);
        pane.setPadding(new Insets(30));
        login_plus_register_window.show();
    }
    
    static void reset()
    {
        center_pane = new HBox(50, button_pane, field);
        pane = new VBox(30, text_pane, center_pane);
        pane.setPadding(new Insets(30));

        scene = new Scene(pane, 800, 610);

        stage.setScene(scene);
        stage.setTitle("Drug Information Management System");
        stage.show();
    }

    static void search_click()
    {
        search_window.show();
    }

    static void indication_click()
    {
        try {
            String query = "SELECT * FROM indication;";
            ResultSet rs = PSQL.query(query);

            indication_window.strings = new ArrayList<>();
            indication_window.id = new ArrayList<>();

            while (rs.next()) {

                indication_window.id.add(rs.getInt("id"));
                indication_window.strings.add(rs.getString("name"));
            }

            indication_window.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void class_click()
    {
        try {

            String query = "SELECT name FROM drug_class;";
            ResultSet rs = PSQL.query(query);

            drug_class_window.strings = new ArrayList<>();


            while (rs.next()) {

                drug_class_window.strings.add(rs.getString("name"));
            }

            drug_class_window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void generic_drug_click()
    {
        try {
            String query = "SELECT * FROM generic_drug;";
            ResultSet rs = PSQL.query(query);

            generic_drug_window.strings = new ArrayList<>();


            while (rs.next()) {
                generic_drug_window.strings.add(rs.getString("name"));
            }

            generic_drug_window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void client_buy_click()
    {
        buy_drug_client.show();
    }

    static void pharmacy_buy_click()
    {
        buy_drug_pharmacy.show();
    }

    static void most_searched_medicine()
    {
        try {
            String query = "select name from medicine where id in " +
                    "( select id from med_src_cnt where cnt = ( select MAX(cnt) from med_src_cnt) );";

            ResultSet rs = PSQL.query(query);

            String  msg = "";

            while (rs.next()) {
                 msg = msg + rs.getString("name") + "\n";
            }

            message_box.show(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void most_searched_indication()
    {
        try {

            String query =  "select name from indication where id in " +
                    "( select id from indication_src_cnt where cnt = ( select MAX(cnt) from indication_src_cnt) );";
            ResultSet rs = PSQL.query(query);

            String  msg = "";

            while (rs.next()) {
                msg = msg + rs.getString("name") + "\n";
            }

            message_box.show(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
