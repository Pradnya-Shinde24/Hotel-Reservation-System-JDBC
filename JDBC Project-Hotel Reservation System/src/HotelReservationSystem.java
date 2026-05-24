import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
	
	public static final String url = "jdbc:mysql://localhost:3306/hotel_db";
	public static final String username = "root";
	public static final String passward = "Pradnya@24";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(ClassNotFoundException e){
			System.out.println(e.getMessage());
		}
		
		try {
			Connection connection = DriverManager.getConnection(url,username,passward);
			
			while(true) {
//				System.out.println();
				System.out.println("Hotel Management System");
				
				System.out.println("1. Reserve a room");
				System.out.println("2. View reservation");
				System.out.println("3. Get a room number");
				System.out.println("4. Update reservation");
				System.out.println("5. Delete reservation");
				System.out.println("6. Exit");
				
				Scanner scanner = new Scanner(System.in);
				System.out.println("Choose an option: ");
				int choice = scanner.nextInt();
				
				switch(choice) {
				   case 1:
					   reserveRoom(connection, scanner);
					   break;
				   case 2 :
					   viewReservation(connection);
					   break;
				   case 3 :
					   getRoomNo(connection, scanner);
					   break;
				   case 4 :
					   updateReservation(connection, scanner);
					   break;
				   case 5 :
					   deleteReservation(connection, scanner);
					   break;
				   case 6 :
					   exit();
					   scanner.close();
					   return;
				   default:
					   System.out.println("Invalid choice. Tryagain.");
					
				}
				
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
		

	}
	
	private static void reserveRoom(Connection connection, Scanner scanner) {
		
		try {
			System.out.println("Enter guest name: ");
			String guestName = scanner.next();
			scanner.nextLine();
			
			System.out.println("Enter room number: ");
			int roomNumber = scanner.nextInt();
			
			System.out.println("Enter contact number: ");
			String contactNumbar = scanner.next();
			
			String sql =" Insert into reservations(guest_name, room_number, contact_number)" + "values('"+guestName+"',"+roomNumber+",'"+contactNumbar+"')";
			
			try(Statement statement = connection.createStatement()){
				int affectRows = statement.executeUpdate(sql);
				
				if(affectRows > 0) {
					System.out.println("Reservation Successfully!");
				}else {
					System.out.println("Reservation Failed.");
				}
			}	
		}
		catch(SQLException e) {
		  e.printStackTrace();
		}
		
	}
	
    private static void viewReservation(Connection connection) throws SQLException{
    	
    	String sql = "select reservation_id, guest_name, room_number, contact_number, reservation_date from reservations";
    	
    	try(Statement statement = connection.createStatement();
    		ResultSet resultset = statement.executeQuery(sql)){
    		
    		System.out.println("Current Reservations:");
    		System.out.println("-----------------+-----------------+---------------+------------------+---------------------------+");
    		System.out.println("| Reservation ID | Guest Name     | Room Number  | Contact Number    | Reservation date           |");
    		System.out.println("-----------------+-----------------+---------------+------------------+---------------------------+");
    		
    		while(resultset.next()) {
    			int reservationId = resultset.getInt("reservation_id");
    			String guestName = resultset.getString("guest_name");
    			int roomNumber = resultset.getInt("room_number");
    			String contactNumber = resultset.getString("contact_number");
    			String reservationDate = resultset.getTimestamp("reservation_date").toString();
    			
    			System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s | \n", reservationId, guestName, roomNumber, contactNumber, reservationDate);
    			
    		}
    		System.out.println("-----------------+-----------------+---------------+------------------+----------------------------+");
    	}    	
		
	}
    
    private static void getRoomNo(Connection connection, Scanner scanner) {
    	
    	try {
    		System.out.println("Enter reservation ID: ");
			int reservationId = scanner.nextInt();
			
			System.out.println("Enter guest name: ");
			String guestName = scanner.next();
			
			String sql = "select room_number from reservations where reservation_id = " + reservationId + " and guest_name = '" + guestName + "'";
			
			try(Statement statement = connection.createStatement();
    		ResultSet resultset = statement.executeQuery(sql)){
			   
				if(resultset.next()) {
					int roomNumber = resultset.getInt("room_number");
					System.out.println("Room number for reservation ID " + reservationId + " and Guest " + guestName + " is: " + roomNumber);
				    
				}else {
					System.out.println("Reservation not found for the given ID and guest name.");
				}
				
    		
    	   }
		
	   }catch(SQLException e) {
   		   e.printStackTrace();
    	
   	   }
    }
    	
    private static void updateReservation(Connection connection, Scanner scanner) {
    	
    	try {
    		System.out.println("Enter reservation ID to update: ");
			int reservationId = scanner.nextInt();
			scanner.nextLine();
			
			if(!reservationExists(connection, reservationId)) {
				System.out.println("Reservation not found for the given Id. ");
				return;
			}
			System.out.println("Enter new guest name: ");
			String newGuestName = scanner.nextLine();
			System.out.println("Enter new room number: ");
			int newRoomNumber = scanner.nextInt();
			System.out.println("Enter new contact number: ");
			String newContactNumber = scanner.next();
			
			String sql = "update reservations set guest_name = '" + newGuestName + "' , " + "room_number =" + newRoomNumber + " , " +
			"contact_number = '" + newContactNumber + "' " + "where reservation_id = " +reservationId;
			
			try(Statement statement = connection.createStatement()){
				
				int affectedRows = statement.executeUpdate(sql);
				
				if(affectedRows > 0) {
					System.out.println("Reservation update successfully!");
				}else {
					System.out.println("Reservation update failed.");
				}
			}		
			
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
		
	}
    private static void deleteReservation(Connection connection, Scanner scanner) {
		
    	try {
    		System.out.println("Enter reservation Id to delete");
    		int reservationId = scanner.nextInt();
    		
    		if(!reservationExists(connection, reservationId)) {
				System.out.println("Reservation not found for the given Id. ");
				return;
			}
    		
    		String sql = " delete from reservations where reservation_id = " +reservationId;
    		
            try(Statement statement = connection.createStatement()){
				
				int affectedRows = statement.executeUpdate(sql);
				
				if(affectedRows > 0) {
					System.out.println("Reservation deleted successfully!");
				}else {
					System.out.println("Reservation deletion failed.");
				}
			}		
    		
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
	}
    
    private static boolean reservationExists(Connection connection, int reservationId) {
    	try {
    		String sql = "select reservation_id from reservations where reservation_id = " + reservationId;
    		
    		try(Statement statement = connection.createStatement();
    	    	ResultSet resultset = statement.executeQuery(sql)){
    			
    			return resultset.next();
    			
    		}
    	}catch(SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
		
	}
    
    public static void exit() throws InterruptedException{
    	System.out.println("Existing System");
    	int i = 5;
    	while(i!=0) {
    		System.out.print(".");
    		Thread.sleep(450);
    		i--;
    	}
    	System.out.println();
    	System.out.println("Thank You For Using Hotel Reservation System!!!");
    }

}
