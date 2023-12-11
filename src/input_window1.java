import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class input_window1
{
    static TextField input_field;

    static void show(int choice)
    {
        input_field = new TextField();
        Text prompt = new Text("Enter Search String: ");
        prompt.setFont(Font.font("Helvetica", 20));

        input_field.setPrefColumnCount(20);
        input_field.setPromptText("Enter your search string");
        input_field.setFont(Font.font("Helvetica", 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setDefaultButton(true);
        submit.setOnAction(e -> {
            try {
                handle(choice);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        submit.setFont(Font.font("Helvetica", 17));
        submit.setMinWidth(80);

        HBox submit_pane = new HBox(0, spacer, submit);
        VBox pane = new VBox(20, prompt, input_field, submit_pane);
        pane.setPadding(new Insets(30));

        home_window.field = pane;
        home_window.reset();
    }

    private static void handle(int choice) throws SQLException
    {
        if(choice == 1) {

            String query =
                    "select med.id aa,med.name bb,gd.name cc,med.medicine_type dd,med.weight ee,com.name ff" +
                            " from (medicine med join company com on med.company_id = com.id)" +
                            " join generic_drug gd on gd.id = med.generic_id" +
                            " where lower(med.name) = lower('" + input_field.getText()  + "');";

            ResultSet rs = PSQL.query(query);

            String s = "";
            int ta = 0;

            while (rs.next()) {
                ta = rs.getInt("aa");
                s +="Product Name: " + rs.getString("bb") + "\nGeneric Name: ";
                s += rs.getString("cc") + "\nType: " + rs.getString("dd") + "\nWeight/Volume: ";
                s += rs.getString("ee") + "\nCompany: ";
                s += rs.getString("ff") + "\n";
            }



            query = "select * from description where medicine_id = " + ta + ";";

            rs = PSQL.query(query);

            while (rs.next()) {
                rs.getInt("medicine_id");

                s += "Adult Dose: " + rs.getString("adult_dose")+"\n";
                s += "Child Dose: " + rs.getString("child_dose")+"\n";
                s += "Renal Dose: " + rs.getString("renal_dose")+"\n";
                s += "Side Effects: " + rs.getString("side_effects")+"\n";;
                s += "Precautions and Warnings: " + rs.getString("precautions_and_warnings")+"\n";;
                s += "Pack Size and Prize: " + rs.getString("pack_size_and_prize")+"\n";;

            }
            System.out.println(s);


            int id = ta;

            Connection connection = null;

            try {
                connection = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                                "postgres", "123");

            } catch (SQLException e) {
                e.printStackTrace();
            }

            PreparedStatement preparedStatement = connection.prepareStatement("select * from medicine_search_report(?)");
            preparedStatement.setInt(1,id);
            ResultSet rss = preparedStatement.executeQuery();
            connection.close();

            message_box.show(s);
        }
        else if(choice == 2)
        {

            String query = "select x.name bb" +
                    " from (generic_drug x join cures y on x.id = y.generic_id) join indication z on y.indication_id = z.id" +
                    " where lower(z.name) = lower('" + input_field.getText() + "');";


            generic_drug_window.strings = new ArrayList<>();

            ResultSet rs = PSQL.query(query);

            while (rs.next()) {

                generic_drug_window.strings.add(rs.getString("bb"));

            }

            Connection connection = null;

            try {
                connection = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                                "postgres", "123");

            } catch (SQLException e) {
                e.printStackTrace();
            }

            PreparedStatement preparedStatement = connection.prepareStatement("select * from indication_search_report(?)");
            preparedStatement.setString(1,input_field.getText());
            ResultSet rss = preparedStatement.executeQuery();
            connection.close();

            generic_drug_window.show();
        }
    }
}
