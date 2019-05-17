import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.*;

public class buy_drug_client {

    static public void show() {
        TextField drug_name_field = new TextField();
        Text drug_name_prompt = new Text("Drug: ");
        drug_name_prompt.setFont(Font.font("Helvetica", 20));

        drug_name_field.setPrefColumnCount(20);
        drug_name_field.setPromptText("Enter Drug Name");
        drug_name_field.setFont(Font.font("Helvetica", 20));

        /*TextField client_name_field = new TextField();
        Text client_name_prompt = new Text("Client ID: ");
        client_name_prompt.setFont(Font.font("Helvetica", 20));

        client_name_field.setPrefColumnCount(20);
        client_name_field.setPromptText("Enter your ID");
        client_name_field.setFont(Font.font("Helvetica", 20));*/

        TextField pharmacy_name_field = new TextField();
        Text pharmacy_name_prompt = new Text("Pharmacy: ");
        pharmacy_name_prompt.setFont(Font.font("Helvetica", 20));

        pharmacy_name_field.setPrefColumnCount(20);
        pharmacy_name_field.setPromptText("Enter Pharmacy Name");
        pharmacy_name_field.setFont(Font.font("Helvetica", 20));

        TextField quantity_field = new TextField();

        Text quantity_prompt = new Text("Quantity: ");
        quantity_prompt.setFont(Font.font("Helvetica", 20));

        quantity_field.setPrefColumnCount(20);
        quantity_field.setPromptText("Enter Quantity");
        quantity_field.setFont(Font.font("Helvetica", 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setDefaultButton(true);
        submit.setFont(Font.font("Helvetica", 17));
        submit.setMinWidth(80);

        HBox drug_name_pane = new HBox(52, drug_name_prompt, drug_name_field);
       // HBox client_name_pane = new HBox(40, client_name_prompt, client_name_field);
        HBox pharmacy_name_pane = new HBox(10, pharmacy_name_prompt, pharmacy_name_field);
        HBox quantity_pane = new HBox(20, quantity_prompt, quantity_field);
        HBox button_pane = new HBox(0, spacer, submit);

        submit.setOnAction(e -> {
            try {
                resolve_client_buy_click(drug_name_field, pharmacy_name_field, quantity_field);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        VBox pane = new VBox(30, drug_name_pane,pharmacy_name_pane, quantity_pane, button_pane);
        pane.setPadding(new Insets(30));

        home_window.field = pane;
        home_window.reset();

        /*primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("CLIENT BUY");
        primaryStage.show();*/
    }

    static void resolve_client_buy_click(TextField drug_name,TextField pharmacy_name,TextField quantity)throws Exception
    {

        String d_name = drug_name.getText();
        String p_name = pharmacy_name.getText();
        int amount = Integer.parseInt(quantity.getText());

        int d_id = 0, p_id = 0;

        String query =  "select id from medicine where lower(name) = lower('" + d_name + "');";
        ResultSet rs = PSQL.query(query);



        while (rs.next()) {
            d_id = rs.getInt("id");
        }

        query =  "select id from pharmacy where lower(name) = lower('" + p_name + "');";
        rs = PSQL.query(query);



        while (rs.next()) {
            p_id = rs.getInt("id");
        }

        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                            "postgres", "123");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement = connection.prepareStatement("select * from fix_client_buy(?,?,?,?)");
        preparedStatement.setInt(1,home_window.curr_id);
        preparedStatement.setInt(2,p_id);
        preparedStatement.setInt(3,d_id);
        preparedStatement.setInt(4,amount);

        ResultSet rss = preparedStatement.executeQuery();
        String s = "";

        while (rss.next())
            s += rss.getString("RES");

        message_box.show(s);

        connection.close();
    }
}
