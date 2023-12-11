import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class input_window2
{
    static void show()
    {
        TextField drug_name = new TextField();
        drug_name.setPrefColumnCount(20);
        drug_name.setFont(Font.font("Helvetica", 20));

        TextField quantity = new TextField();
        quantity.setPrefColumnCount(20);
        quantity.setFont(Font.font("Helvetica", 20));

        TextField longitude = new TextField();
        longitude.setPrefColumnCount(20);
        longitude.setFont(Font.font("Helvetica", 20));

        TextField latitude = new TextField();
        latitude.setPrefColumnCount(20);
        latitude.setFont(Font.font("Helvetica", 20));

        Text text_drug_name = new Text("Drug Name: ");
        text_drug_name.setFont(Font.font("Helvetica", 20));

        Text text_quantity = new Text("Quantity: ");
        text_quantity.setFont(Font.font("Helvetica", 20));

        Text text_longitude = new Text("Longitude: ");
        text_longitude.setFont(Font.font("Helvetica", 20));

        Text text_latitude = new Text("Latitude: ");
        text_latitude.setFont(Font.font("Helvetica", 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            try {
                handle(drug_name, quantity, longitude, latitude);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        submit.setFont(Font.font("Helvetica", 17));
        submit.setMinWidth(80);
        submit.setDefaultButton(true);

        HBox drug_name_pane = new HBox(8, text_drug_name, drug_name);
        HBox quantity_pane = new HBox(35, text_quantity, quantity);
        HBox longitude_pane = new HBox(20, text_longitude, longitude);
        HBox latitude_pane = new HBox(38, text_latitude, latitude);
        HBox submit_pane = new HBox(0, spacer, submit);

        VBox pane = new VBox(30, drug_name_pane, quantity_pane, longitude_pane, latitude_pane, submit_pane);
        pane.setPadding(new Insets(20));

        home_window.field = pane;
        home_window.reset();
    }

    static void handle(TextField drug_name, TextField quantity, TextField longitude, TextField latitude)throws Exception
    {
        int d_id = 0;

        String query =  "select id from medicine where lower(name) = lower('" + drug_name.getText() + "');";
        ResultSet rs = PSQL.query(query);


        try {
            while (rs.next()) {
                try {
                    d_id = rs.getInt("id");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                            "postgres", "123");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(d_id);
        System.out.println(longitude.getText() + latitude.getText() + quantity.getText());

        PreparedStatement preparedStatement = connection.prepareStatement("select * from get_nearest_pharmacy(?,?,?,?)");
        preparedStatement.setDouble(1,Double.parseDouble(longitude.getText()));
        preparedStatement.setDouble(2,Double.parseDouble(latitude.getText()));
        preparedStatement.setInt(3,d_id);
        preparedStatement.setInt(4,Integer.parseInt(quantity.getText()));

        ResultSet rss = preparedStatement.executeQuery();

        System.out.println(rss);

        String s = "";

        int t_id = 0 ,mn_id = -1;
        double mn_dist = Double.MAX_VALUE,temp = Double.MAX_VALUE;


        while (rss.next())
        {
            t_id = rss.getInt("id");
            temp = rss.getDouble("dist");

            if(temp < mn_dist)
            {
                mn_dist = temp;
                mn_id = t_id;

            }
        }

        connection.close();

        if(mn_id != -1)
        {
            System.out.println(mn_id);


            query =  "select name from pharmacy where id = " + mn_id + ";";
            rs = PSQL.query(query);


            try {
                while (rs.next()) {
                    try {
                        s += rs.getString("name");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            message_box.show(s);
        }
        else
        {
            System.out.println("hi");
            message_box.show("NO PHARMACY FOUND");
        }




    }
}
