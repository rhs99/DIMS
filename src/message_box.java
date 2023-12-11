import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class message_box
{
    public static void show(String message)
    {
        Button btnOK = new Button("OK");
        btnOK.setMinWidth(100);
        btnOK.setOnAction(e -> {home_window.field = new VBox(); home_window.reset();});

        btnOK.setFont(Font.font("Helvetica", 14));
        btnOK.setDefaultButton(true);

        Text txt = new Text();
        txt.setFont(Font.font("Helvetica", 20));
        txt.setText(message);

        VBox pane = new VBox(40);
        pane.getChildren().addAll(txt);
        //pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30));

        home_window.field = pane;
        home_window.reset();

        /*home_window.stage.setResizable(false);
        home_window.stage.setScene(new Scene(pane));
        home_window.stage.setTitle("Message");
        home_window.stage.show();*/
    }
}