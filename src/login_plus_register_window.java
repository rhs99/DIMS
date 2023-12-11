import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class login_plus_register_window {
    static boolean is_client;

    static void show() {
        ToggleGroup group = new ToggleGroup();

        RadioButton as_pharmacy = new RadioButton("Pharmacy");
        as_pharmacy.setFont(Font.font("Helvetica", 17));
        as_pharmacy.setToggleGroup(group);

        RadioButton as_client = new RadioButton("Client");
        as_client.setFont(Font.font("Helvetica", 17));
        as_client.setToggleGroup(group);

        Text title = new Text("Enter DIMS as");
        title.setFont(Font.font("Helvetica", 20));

        Button login = new Button("Log in");
        login.setFont(Font.font("Helvetica", 17));
        login.setMinWidth(100);

        Button register = new Button("Register");
        register.setFont(Font.font("Helvetica", 17));
        register.setMinWidth(100);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox button_pane = new HBox(20, login, spacer, register);
        button_pane.setPadding(new Insets(30, 0, 10, 0));

        VBox radios = new VBox(20, as_client, as_pharmacy);
        radios.setPadding(new Insets(10, 0, 0, 40));

        VBox pane = new VBox(20, title, radios, button_pane);
        pane.setPadding(new Insets(30, 30, 20, 30));

        login.setOnAction(e -> {
            try {
                login(as_client, as_pharmacy);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        register.setOnAction(e -> {
            try {
                register(as_client, as_pharmacy);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        home_window.field = pane;
        home_window.reset();
    }

    static void register(RadioButton as_client, RadioButton as_pharmacy) {
        if (as_client.isSelected()) {
            is_client = true;

            TextField name = new TextField();
            Text name_prompt = new Text("Name: ");
            name_prompt.setFont(Font.font("Helvetica", 20));

            name.setPrefColumnCount(20);
            name.setPromptText("Enter your name");
            name.setFont(Font.font("Helvetica", 20));

            TextField password = new TextField();
            Text password_prompt = new Text("Password: ");
            password_prompt.setFont(Font.font("Helvetica", 20));

            password.setPrefColumnCount(20);
            password.setPromptText("Enter your password");
            password.setFont(Font.font("Helvetica", 20));

            TextField contact = new TextField();
            Text contact_prompt = new Text("Contact: ");
            contact_prompt.setFont(Font.font("Helvetica", 20));

            contact.setPrefColumnCount(20);
            contact.setPromptText("Enter your phone number");
            contact.setFont(Font.font("Helvetica", 20));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button submit = new Button("Submit");
            submit.setDefaultButton(true);
            submit.setFont(Font.font("Helvetica", 17));
            submit.setMinWidth(80);

            submit.setOnAction(e -> {
                try {
                    client_register(name, password, contact);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            HBox name_pane = new HBox(50, name_prompt, name);
            HBox password_pane = new HBox(17, password_prompt, password);
            HBox contact_pane = new HBox(30, contact_prompt, contact);
            HBox button_pane = new HBox(0, spacer, submit);

            VBox pane = new VBox(30, name_pane, password_pane, contact_pane, button_pane);
            pane.setPadding(new Insets(30));

            home_window.field = pane;
            home_window.reset();
        } else if (as_pharmacy.isSelected()) {
            is_client = false;

            TextField name = new TextField();
            Text name_prompt = new Text("Name: ");
            name_prompt.setFont(Font.font("Helvetica", 20));

            name.setPrefColumnCount(20);
            name.setPromptText("Enter pharmacy name");
            name.setFont(Font.font("Helvetica", 20));

            TextField password = new TextField();
            Text password_prompt = new Text("Password: ");
            password_prompt.setFont(Font.font("Helvetica", 20));

            password.setPrefColumnCount(20);
            password.setPromptText("Enter pharmacy password");
            password.setFont(Font.font("Helvetica", 20));

            TextField longitude_field = new TextField();
            Text longitude_prompt = new Text("Longitude: ");
            longitude_prompt.setFont(Font.font("Helvetica", 20));

            longitude_field.setPrefColumnCount(20);
            longitude_field.setPromptText("Enter longitude");
            longitude_field.setFont(Font.font("Helvetica", 20));

            TextField latitude_field = new TextField();
            Text latitude_prompt = new Text("Latitude: ");
            latitude_prompt.setFont(Font.font("Helvetica", 20));

            latitude_field.setPrefColumnCount(20);
            latitude_field.setPromptText("Enter latitude");
            latitude_field.setFont(Font.font("Helvetica", 20));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button submit = new Button("Submit");
            submit.setDefaultButton(true);
            submit.setFont(Font.font("Helvetica", 17));
            submit.setMinWidth(80);

            submit.setOnAction(e -> {
                try {
                    pharmacy_register(name, password, longitude_field, latitude_field);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });


            HBox name_pane = new HBox(40, name_prompt, name);
            HBox password_pane = new HBox(10, password_prompt, password);
            HBox longitude_pane = new HBox(5, longitude_prompt, longitude_field);
            HBox latitude_pane = new HBox(20, latitude_prompt, latitude_field);
            HBox button_pane = new HBox(0, spacer, submit);

            VBox pane = new VBox(30, name_pane, password_pane, longitude_pane, latitude_pane, button_pane);
            pane.setPadding(new Insets(30));

            home_window.field = pane;
            home_window.reset();
        }
    }

    static void client_register(TextField userName, TextField password, TextField contactNo) throws Exception {

        String name = userName.getText();
        String pass = password.getText();
        String contact = contactNo.getText();

        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + "MYDIMS",
                    "postgres", "123");

        } catch (Exception e) {
            e.printStackTrace();
        }

        PreparedStatement st = conn.prepareStatement("INSERT INTO client (name, pass, contact) VALUES (?, ?, ?)");

        st.setString(1, name);
        st.setString(2, pass);
        st.setString(3, contact);

        try {
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        conn.close();


        String query =   "Select nextval(pg_get_serial_sequence('client', 'id')) as new_id";
        ResultSet rs = PSQL.query(query);

        int idd = -1;

        while (rs.next()) {
            idd = rs.getInt("new_id");
        }

        idd --;

        home_window.curr_id = idd;


        home_window.set_button_pane();
        home_window.field = new VBox();
        home_window.reset();
    }


    static void pharmacy_register(TextField pharmacyName, TextField password, TextField longitude, TextField latitude) throws Exception {

        String name = pharmacyName.getText();
        String pass = password.getText();
        Double Longitude = Double.parseDouble(longitude.getText());
        Double Latitude = Double.parseDouble(latitude.getText());

        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + "MYDIMS",
                    "postgres", "123");

        } catch (Exception e) {
            e.printStackTrace();
        }

        PreparedStatement st = conn.prepareStatement("INSERT INTO pharmacy (name,longitude,latitude,password) VALUES (?, ?, ? ,?)");

        st.setString(1, name);
        st.setDouble(2, Longitude);
        st.setDouble(3, Latitude);
        st.setString(4, pass);

        try {
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        conn.close();

        String query =   "Select nextval(pg_get_serial_sequence('pharmacy', 'id')) as new_id";
        ResultSet rs = PSQL.query(query);

        int idd = -1;

        while (rs.next()) {
            idd = rs.getInt("new_id");
        }

        idd --;

        home_window.curr_id = idd;

        home_window.set_button_pane();
        home_window.field = new VBox();
        home_window.reset();
    }

    static void handle_client_login(TextField name, TextField pass) throws Exception {

        String user_name = name.getText();
        String password = pass.getText();

        int check = -1;

        String query = "select id from client where name = '" + user_name + "' and pass = '" + password + "';";

        ResultSet rs = PSQL.query(query);

        while (rs.next()) {

            check = rs.getInt("id");

        }

        if (check != -1) {
            home_window.curr_id = check;
            home_window.is_client = is_client;
            home_window.set_button_pane();
            home_window.field = new VBox();
            home_window.reset();

        } else {

            show();

        }

    }

    static void handle_pharmacy_login(TextField name, TextField pass)throws Exception {

        String user_name = name.getText();
        String password = pass.getText();

        int check = -1;

        String query = "select id from pharmacy where name = '" + user_name + "' and password = '" + password + "';";

        ResultSet rs = PSQL.query(query);

        while (rs.next()) {

            check = rs.getInt("id");

        }

        if (check != -1) {

            home_window.curr_id = check;
            home_window.is_client = is_client;

            home_window.set_button_pane();
            home_window.field = new VBox();
            home_window.reset();

        } else {

            show();

        }

    }


    static void login(RadioButton as_client, RadioButton as_pharmacy) {
        if (as_client.isSelected()) {
            is_client = true;

            TextField name = new TextField();
            Text name_prompt = new Text("Name: ");
            name_prompt.setFont(Font.font("Helvetica", 20));

            name.setPrefColumnCount(20);
            name.setPromptText("Enter your name");
            name.setFont(Font.font("Helvetica", 20));

            TextField password = new TextField();
            Text password_prompt = new Text("Password: ");
            password_prompt.setFont(Font.font("Helvetica", 20));

            password.setPrefColumnCount(20);
            password.setPromptText("Enter your password");
            password.setFont(Font.font("Helvetica", 20));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button submit = new Button("Submit");
            submit.setDefaultButton(true);
            submit.setFont(Font.font("Helvetica", 17));
            submit.setMinWidth(80);

            submit.setOnAction(e -> {
                try {
                    handle_client_login(name, password);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            HBox name_pane = new HBox(40, name_prompt, name);
            HBox password_pane = new HBox(10, password_prompt, password);
            HBox button_pane = new HBox(0, spacer, submit);

            VBox pane = new VBox(30, name_pane, password_pane, button_pane);
            pane.setPadding(new Insets(30));

            home_window.field = pane;
            home_window.reset();
        } else if (as_pharmacy.isSelected()) {
            is_client = false;

            TextField name = new TextField();
            Text name_prompt = new Text("Name: ");
            name_prompt.setFont(Font.font("Helvetica", 20));

            name.setPrefColumnCount(20);
            name.setPromptText("Enter pharmacy name");
            name.setFont(Font.font("Helvetica", 20));

            TextField password = new TextField();
            Text password_prompt = new Text("Password: ");
            password_prompt.setFont(Font.font("Helvetica", 20));

            password.setPrefColumnCount(20);
            password.setPromptText("Enter pharmacy password");
            password.setFont(Font.font("Helvetica", 20));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button submit = new Button("Submit");
            submit.setDefaultButton(true);
            submit.setFont(Font.font("Helvetica", 17));
            submit.setMinWidth(80);

            submit.setOnAction(e -> {
                try {
                    handle_pharmacy_login(name, password);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            HBox name_pane = new HBox(40, name_prompt, name);
            HBox password_pane = new HBox(10, password_prompt, password);
            HBox button_pane = new HBox(0, spacer, submit);

            VBox pane = new VBox(30, name_pane, password_pane, button_pane);
            pane.setPadding(new Insets(30));

            home_window.field = pane;
            home_window.reset();
        }
    }
}