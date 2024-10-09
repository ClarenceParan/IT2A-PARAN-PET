package petadoptionsystem;

import java.sql.*;
import java.util.Scanner;



public class PetAdoptionSystem {

    // CONNECTION METHOD TO SQLITE
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Pets.db"); // Use Pets.db
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
    }

    // ADD RECORD METHOD
    public void addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values[i]);
                } else {
                    pstmt.setString(i + 1, values[i].toString());
                }
            }

            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    // VIEW RECORDS METHOD
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No records found.");
                return;
            }

            StringBuilder headerLine = new StringBuilder();
            headerLine.append("--------------------------------------------------------------------------------\n| ");
            for (String header : columnHeaders) {
                headerLine.append(String.format("%-20s | ", header));
            }
            headerLine.append("\n--------------------------------------------------------------------------------");

            System.out.println(headerLine.toString());

            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-20s | ", value != null ? value : ""));
                }
                System.out.println(row.toString());
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    // ADD PET METHOD
    public void addPet() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Pet Name: ");
        String petName = sc.next();
        System.out.print("Animal Type: ");
        String animalType = sc.next();
        System.out.print("Sex: ");
        String sex = sc.next();
        System.out.print("Was it here before? (yes/no): ");
        String hereBefore = sc.next();

        String sql = "INSERT INTO pets (pet_name, animal_type, sex, here_before) VALUES (?, ?, ?, ?)";
        addRecord(sql, petName, animalType, sex, hereBefore);
    }

    // VIEW PETS METHOD
    private void viewPets() {
        String petsQuery = "SELECT * FROM pets";
        String[] petsHeaders = {"ID", "Pet Name", "Animal Type", "Sex", "Here Before?"};
        String[] petsColumns = {"id", "pet_name", "animal_type", "sex", "here_before"};

        viewRecords(petsQuery, petsHeaders, petsColumns);
    }

    // UPDATE PET METHOD
    public void updatePet() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Pet Name to update: ");
        String petName = sc.next();

        System.out.print("Adopter Name: ");
        String adopterName = sc.next();
        System.out.print("Contact Phone: ");
        String contactPhone = sc.next();
        System.out.print("Adoption Date (YYYY-MM-DD): ");
        String adoptionDate = sc.next();
        System.out.print("Adoption Fee: ");
        double adoptionFee = sc.nextDouble();

        String sql = "UPDATE pets SET adopter_name = ?, contact_phone = ?, adoption_date = ?, adoption_fee = ? WHERE pet_name = ?";
        addRecord(sql, adopterName, contactPhone, adoptionDate, adoptionFee, petName);

        System.out.println("Pet information updated successfully.");
    }

    // DELETE PET METHOD
    public void deletePet() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Pet Name to delete: ");
        String petName = sc.next();

        String sql = "DELETE FROM pets WHERE pet_name = ?";
        addRecord(sql, petName);

        System.out.println("Pet deleted successfully.");
    }

    // MENU METHOD
    public void menu() {
        Scanner sc = new Scanner(System.in);
        int option = 0;

        while (option != 5) {
            System.out.println("Select options:");
            System.out.println("1. Add");
            System.out.println("2. View");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Exit");

            option = sc.nextInt();

            switch (option) {
                case 1:
                    addPet();
                    break;
                case 2:
                    viewPets();
                    break;
                case 3:
                    updatePet();
                    break;
                case 4:
                    deletePet();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {
        PetAdoptionSystem system = new PetAdoptionSystem();
        system.menu();
    }
}