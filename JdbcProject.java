
package com.example.demo.controller;

import java.sql.*;
import java.util.*;

public class DataController {

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);

		String url = "jdbc:mysql://localhost:3306/jdbc_database";
		String uname = "root";
		String psw = "GaneshM@123";

		// Establishing the connection
		Connection con = DriverManager.getConnection(url, uname, psw);

		System.out.println("Enter 1 for Add Book 2 for Borrow ");
		int n = sc.nextInt();
		if (n == 1) {
			update(con); // 0 for default value, not used in the 'Add Book' case
		} else if (n == 2) {
			Borrow(con);
		} else {
			System.out.println("Enter valid input");
		}
	}


	// update method
	public static void update(Connection con) throws SQLException 
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the title of the book");
		String title=sc.nextLine();
		System.out.println("Enter the author of book");
		String author=sc.nextLine();
		System.out.println("Enter year of publishing Book");
		int year=sc.nextInt();
		System.out.println("Enter no. of copies do want to Add in Library");
		int avlcopies=sc.nextInt();
		System.out.println("Enter the 6 Digit book Id");
		int bookid=sc.nextInt();

		//Checking whether the BookId present or not if present the update 

		String sql="select * from book where bookid=?";
		PreparedStatement pstmt=con.prepareStatement(sql);

		pstmt.setInt(1,bookid);

		ResultSet resultSet = pstmt.executeQuery();
		
		while(resultSet.next())
		{
			System.out.println(resultSet.getInt(bookid));
		}

		int countBook=countBooks(con,bookid);



		if (countBook>0) 
		{


			int newAvlCopies = countBook + avlcopies;

			String Query = "UPDATE book SET availableCopies = ? WHERE bookid = ?";

			PreparedStatement pstmt1 = con.prepareStatement(Query);

			pstmt1.setInt(1, newAvlCopies);
			pstmt1.setInt(2, bookid);

			pstmt1.executeUpdate();

			System.out.println("Available Books Successfully Updated");

		}
		else
		{


			String query="insert into book values(?,?,?,?,?)";


			PreparedStatement qrystmt = con.prepareStatement(query);
			//updating book table by adding new book
			qrystmt.setInt(5,bookid);
			qrystmt.setString(1,title);
			qrystmt.setString(2,author);
			qrystmt.setInt(3,year);
			qrystmt.setInt(4,avlcopies);


			qrystmt.executeUpdate();
			System.out.println("Successfully Added");


		}
		//query


	}






	public static void Borrow(Connection con) throws SQLException 
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the title of Book you want to take");
		String title=sc.nextLine();

		//checking wheher book is present or not
		boolean bookAvailable=titleBook(con,title);

		if(bookAvailable==false)
		{
			System.out.println("Book is not available");

		}
		// if Book is availble
		else
		{
			showTable(con,title);
			System.out.println("Enter the Author name ");
			String author = sc.nextLine();

			System.out.println("Enter the 6 Digit Book Id");
			int bookid = sc.nextInt();
			sc.nextLine(); // Consume the newline character left after reading bookid

			System.out.println("Enter the Student Name");
			String stdname = sc.nextLine();

			//System.out.println("STUDENT IS " + stdname);

			System.out.println("Enter the  6 Digit Student Id");
			int stdid = sc.nextInt();

			System.out.println("Enter no. of copies do you want to take");
			int nocpy = sc.nextInt();

			//finding no.of copies
			int count=countBooks(con,bookid);
			//System.out.println("borrowing books count "+count);

			if(count<nocpy)// If available copies less then user requirement ask user enter valid input
			{
				System.out.println("Sory available copies are only: "+count);
			}
			else
			{
				String sql="insert into borrow values(?,?,?,?,?,?)";

				PreparedStatement pstmt=con.prepareStatement(sql);
				//Student Details Tables
				pstmt.setString(4,stdname);
				pstmt.setString(1,title);
				pstmt.setString(2,author);
				pstmt.setInt(3,bookid);

				pstmt.setInt(5, stdid);
				pstmt.setInt(6,nocpy);

				pstmt.executeUpdate();
				System.out.println("Books Taken successfully");



				//updating the Book Table

				String query="update book set availableCopies=availableCopies-? where bookid=? and title=?";

				PreparedStatement qry1= con.prepareStatement(query);
				// book 
				qry1.setInt(1, nocpy);
				qry1.setInt(2, bookid);
				qry1.setString(3, title);

				qry1.executeUpdate();
				System.out.println("Upated the books  e after Borrow");

			}




		}

	}


	// Method for check how many no.of books are there by BookId

	private static int countBooks(Connection con, int bookid) {

		String sql="select availableCopies as count from book where bookid=?";

		int c=0;
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1,bookid);

			ResultSet resultSet1 = pstmt.executeQuery();


			if (resultSet1.next()) {
				c = resultSet1.getInt("count");
			}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return c;
	}


	// Checkin the title of book is available or not
	public static boolean titleBook(Connection con, String title) throws SQLException 
	{
		String sql="select count(*) as count from book where Lower(title)=Lower(?)";
		PreparedStatement pstmt=con.prepareStatement(sql);

		pstmt.setString(1,title);

		ResultSet rs=pstmt.executeQuery();

		int c=0;
		if (rs.next()) {
			c = rs.getInt("count");
		}

		if(c>0) return true;

		//System.out.println(c+" no. books are there");
		return false;
	}

	public static void showTable(Connection con,String title) throws SQLException
	{
		String sql="select * from book where title=?";

		PreparedStatement pstmt=con.prepareStatement(sql);

		pstmt.setString(1, title);
		ResultSet rs=pstmt.executeQuery();
		System.out.println(title+" book Details:");
		System.out.println();

		while(rs.next())
		{
			System.out.println(rs.getString(1)+" "+rs.getString(2)+" "+rs.getInt(5));
		}
		System.out.println();

	}


}

