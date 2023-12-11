import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PSQL {
    public static ResultSet query(String queryLine)
    {
        Statement stmt = null;
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/"+"MYDIMS",
                            "postgres", "123");

            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery( queryLine );
            //stmt.close();
            //c.close();
            return rs;
            /*
            while ( rs.next() ) {
                String name = rs.getString("name");
                System.out.print(name);
                String location = rs.getString("location_of_hq");
                System.out.println(location);
            }
            */
            //rs.close();



        } catch (Exception e) {
            return null;
        }
    }
}
