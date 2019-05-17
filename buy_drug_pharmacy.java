import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.*;

public class buy_drug_pharmacy {
    static public void show() {
        TextField drug_name_field = new TextField();
        Text drug_name_prompt = new Text("Drug: ");
        drug_name_prompt.setFont(Font.font("Helvetica", 20));

        drug_name_field.setPrefColumnCount(20);
        drug_name_field.setPromptText("Enter Drug Name");
        drug_name_field.setFont(Font.font("Helvetica", 20));

        TextField company_name_field = new TextField();
        Text company_name_prompt = new Text("Company: ");
        company_name_prompt.setFont(Font.font("Helvetica", 20));

        company_name_field.setPrefColumnCount(20);
        company_name_field.setPromptText("Enter Company Name");
        company_name_field.setFont(Font.font("Helvetica", 20));

        /*TextField pharmacy_name_field = new TextField();
        Text pharmacy_name_prompt = new Text("Pharmacy ID: ");
        pharmacy_name_prompt.setFont(Font.font("Helvetica", 20));

        pharmacy_name_field.setPrefColumnCount(20);
        pharmacy_name_field.setPromptText("Enter Pharmacy ID");
        pharmacy_name_field.setFont(Font.font("Helvetica", 20));*/

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

        HBox drug_name_pane = new HBox(50, drug_name_prompt, drug_name_field);
        HBox company_name_pane = new HBox(10, company_name_prompt, company_name_field);
        //HBox pharmacy_name_pane = new HBox(5, pharmacy_name_prompt, pharmacy_name_field);
        HBox quantity_pane = new HBox(20, quantity_prompt, quantity_field);
        HBox button_pane = new HBox(0, spacer, submit);

        submit.setOnAction(e -> {
            try {
                resolve_client_buy_click(company_name_field, drug_name_field, quantity_field);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        VBox pane = new VBox(30, drug_name_pane/*, pharmacy_name_pane*/, company_name_pane, quantity_pane, button_pane);
        pane.setPadding(new Insets(30));

        home_window.field = pane;
        home_window.reset();

        /*primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("CLIENT BUY");
        primaryStage.show();*/
    }

    static void resolve_client_buy_click(TextField company_name, TextField medicine_name, TextField quantity)throws Exception
    {
        String c_name = company_name.getText();
        String m_name = medicine_name.getText();
        int amount = Integer.parseInt(quantity.getText());

        int d_id = 0, c_id = 0;



        String query =  "select id from medicine where lower(name) = lower('" + m_name + "');";
        ResultSet rs = PSQL.query(query);



        while (rs.next()) {
            d_id = rs.getInt("id");
        }

        query =  "select id from company where lower(name) = lower('" + c_name + "');";
        rs = PSQL.query(query);



        while (rs.next()) {
            c_id = rs.getInt("id");
        }


        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                            "postgres", "123");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement = connection.prepareStatement("select * from fix_pharmacy_buy(?,?,?,?)");
        preparedStatement.setInt(1,home_window.curr_id);
        preparedStatement.setInt(2,c_id);
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

/*
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.*;

public class buy_drug_pharmacy extends Application {
    @Overrnamee
    public voname start(Stage primaryStage) throws Exception {
        TextField medicineId = new TextField();
        medicineId.setPromptText("medicine name");

        TextField pharmacyId  = new TextField();
        pharmacyId.setPromptText("pharmacy name");

        TextField companyId = new TextField();
        companyId.setPromptText("company name");

        TextField quantity = new TextField();
        quantity.setPromptText("quantity");

        Button buy = new Button("purchase now");

        buy.setOnAction(e -> {
            try {
                resolve_pharmacy_buy_click(pharmacyId,companyId,medicineId,quantity);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });


        HBox hBox = new HBox();
        hBox.getChildren().addAll(pharmacyId,companyId,medicineId,quantity,buy);
        BorderPane pane = new BorderPane(hBox);
        Scene scene = new Scene(pane, 600, 600);

        stage.setScene(scene);
        stage.setTitle("PHARMACY BUY");
        stage.show();
    }

    public voname  resolve_pharmacy_buy_click(TextField pharmacyId,TextField companyId,TextField medicineId,TextField quantity)throws Exception
    {
        int cname = Integer.parseInt(companyId.getText());
        int pname = Integer.parseInt(pharmacyId.getText());
        int mname = Integer.parseInt(medicineId.getText());
        int amount = Integer.parseInt(quantity.getText());

        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                            "postgres", "123");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement = connection.prepareStatement("select * from fix_pharmacy_buy(?,?,?,?)");
        preparedStatement.setInt(1,pname);
        preparedStatement.setInt(2,cname);
        preparedStatement.setInt(3,mname);
        preparedStatement.setInt(4,amount);

        ResultSet rss = preparedStatement.executeQuery();

        while (rss.next())
        {
            System.out.println(rss.getString("RES"));
        }

        connection.close();

        run();
    }
}
*/
