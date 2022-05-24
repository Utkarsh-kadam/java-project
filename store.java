import java.io.*;
import java.lang.*;

class store
{
	private InputStreamReader ir=new InputStreamReader(System.in);
	private BufferedReader br;
	private FileOutputStream fos;
	private	PrintStream ps; 
	private FileReader frs;
	
	//function to check the existence for a file
	private boolean checkFile(String filename)
	{
		File myFile=new File(filename);
		boolean b=myFile.exists();	
		
		return b;
	}
	

	private String generate_id()
	{
		String id=" ";
		String thisLine;
		String[] fullText = new String[200];
		String[] lastLine=new String[5];
		int counter=0,nid=0,l=0,z=0;
		
		boolean b=checkFile("Item.txt");
		
		if(b==false)
		{
			id="00001";
		}
		else
		{
			try
			{
				frs=new FileReader("Item.txt");
			 	br=new BufferedReader(frs);
				while((thisLine=br.readLine()) != null)
				{
					counter=counter+1;
					fullText[counter] = thisLine;
				}
				frs.close();
 				lastLine=fullText[counter].split(",");
 				nid=Integer.parseInt(lastLine[0]);
 				nid=nid+1;
 				l=(String.valueOf(nid)).length();
 				z=5-l;
 				for(int i=1;i<=z;i++)
 				{
 					id=id+"0";	
 				}
 				id=id+String.valueOf(nid);
 				id=id.substring(1);
			}
			catch(Exception ex)
			{
				System.out.println("Could not generate the Item ID...!");
			}
		}
		return id;
	}
	
	//function to add new product
	private void addItem()
	{
		String desc;
		String id;
		double price=0;
		int stock=0,rol=0;
		
		try
 		{
			id=generate_id();
			System.out.print("\n Product ID :"+id+"\n");
			br=new BufferedReader(ir);
			
			System.out.print("\n Product Description :");
			desc=br.readLine();
			
			System.out.print("\n Unit Price : Rs.");
			price=Double.parseDouble(br.readLine());
			
			System.out.print("\n Starting stock value :");
			stock=Integer.parseInt(br.readLine());		
			
			System.out.print("\n Re-order Level :");
			rol=Integer.parseInt(br.readLine());
			
			fos=new FileOutputStream("Item.txt",true);
			ps=new PrintStream(fos);
			
			ps.println(id+","+desc+","+price+","+stock+","+rol);
			fos.close();
			System.out.println("Successfully added one product...");
		}
 		catch(Exception ex)
 		{
 			System.out.println("Error in accepting item specifications...");
 		}	
	}
	

	private void makeTransaction()
	{
		String id,desc,s="y";
		String msg,stock;
		String value[]=new String[5];
		double price=0,amount=0;
		int qty=0;
		
		try
		{
			while(s.equals("y"))
			{
				br=new BufferedReader(ir);
				
				System.out.print("\n Enter Product ID :");
				id=br.readLine();
				msg=findProduct(id);
				if(msg.equals("Product not found..."))
				{
					System.out.print("\n"+msg);
					continue;
				}
				br=new BufferedReader(ir);
				value=msg.split(",");
				if(Integer.parseInt(value[3])==Integer.parseInt(value[4]))
				{
					System.out.println("Product in Re-Order Level.");
					System.out.println("Need to generate a purchase order...");
					continue;	
				}
				desc=value[1];
				price=Double.parseDouble(value[2]);
				stock=value[3];
				
				if(Integer.parseInt(stock)==0)
				{
					System.out.println("Current stock is NIL...");
					System.out.println("Could not process any transaction on this product...");
					continue;
				}
				
				System.out.print("\n Qty to sold :");
				qty=Integer.parseInt(br.readLine());
				
				if(qty>Integer.parseInt(stock))
				{
					System.out.println("Out of stock...!");
					System.out.println("Could not sell out this product...");
					continue;
				}
				amount=qty*price;
				
				int d=updateStock(id,qty);
				if(d==1)
				{
					System.out.println("Failed to update the stock...!");	
				}
				
				frs=new FileReader("Item.txt");
				br=new BufferedReader(frs);
				
				while((msg=br.readLine()) != null)
				{
					value=msg.split(",");
					if(value[0].equals(id))
					{
						stock=value[3];
						break;
					}
				}
				
				fos=new FileOutputStream("Transaction.txt",true);
				ps=new PrintStream(fos);
				ps.println(id+","+desc+","+qty+","+amount+","+stock);
				fos.close();
				
				s="n";
			}
			System.out.println("Transaction processed successfully...");	
		}
		catch(Exception ex)
		{
			System.out.println("Could not process the transaction...!");	
		}	
	}

	private String findProduct(String pID) throws IOException
	{
		int counter=0,i=0,flag=0;
		String s;
		String cline[]=new String[255];
		String cID[]=new String[5];
		String value=" ";
		
		frs=new FileReader("Item.txt");
		br=new BufferedReader(frs);
		
		try
		{
			while((s=br.readLine()) != null)
			{
				counter=counter+1;
				cline[counter]=s;
				cID=cline[counter].split(",");
				if(cID[0].equals(pID))
				{
					for(;i<cID.length;i++)
					{
						value=value+","+cID[i];	
					}
					flag=1;
					break;
				}
			}
			switch(flag)
			{
				case 0:
					value="Product not found...";
					break;
				case 1:
					value=value.substring(2);
					break;						
			}
		}
		catch(Exception ex)
		{
			System.out.println("Could continue operation on searching the product...!");
		}
		
		return value;
	}

	private int updateStock(String pID,int qty) throws IOException
	{
		int counter=0,i=0,pos=0;
		String s;
		String cline[]=new String[255];
		String cID[]=new String[5];
		String row[]=new String[5];
		String desc,price,rol;
		int stock=0,status=0;
		
		try
		{
			frs=new FileReader("Item.txt");
			br=new BufferedReader(frs);		
			
			while((s=br.readLine()) != null)
			{
				counter=counter+1;
				cline[counter]=s;			
				cID=s.split(",");
				if(cID[0].equals(pID))
 				{
 					pos=counter;
 					row=s.split(",");
 				}	
			}
			
			fos=new FileOutputStream("Item.txt",false);
			ps=new PrintStream(fos);
			
			for(i=0;i<cline.length;i++)
			{			
				if(i==pos)
				{
					desc=row[1];
					price=row[2];
					stock=Integer.parseInt(row[3])-qty;
					rol=row[4];
					ps.println(pID+","+desc+","+price+","+stock+","+rol);
				}
				else
				{
					if(cline[i]!=null)
					{
						ps.println(cline[i]);
					}
				}
			}
			fos.close();	
			status=0;
		}
		catch(Exception ex)
		{
			status=1;	
		}
		return status;	
	}

	private void displayReport()
	{
		String id,desc,qty,value,stock,s;
		String cline[]=new String[5];
		
		try
		{
			frs=new FileReader("Transaction.txt");
			br=new BufferedReader(frs);		
			
			System.out.println("\nID   Description   Qty Sold   Amount   C.Stock");
			System.out.println("----------------------------------------------");
		
			while((s=br.readLine()) != null)
			{
				cline=s.split(",");
				id=cline[0];
				desc=cline[1];
				qty=cline[2];
				value=cline[3];
				stock=cline[4];
				
				System.out.println(id+"   "+desc+"              "+qty+"   "+"Rs."+value+"   "+stock);
			}	
		}
		catch(Exception ex)
		{
			System.out.println("Some exceptions occured...");
			System.out.println("Could not generate the daily transaction report...!");	
		}
	}
	
	public static void main(String args[]) throws IOException
	{
		store inv=new store();
		int ch=0;
		String s;
		InputStreamReader ir=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(ir);
		
		System.out.println("S T O C K    M A N A G E M E N T    S Y S T E M");
		System.out.println("-----------------------------------------------");
		System.out.println("1. ADD NEW PRODUCT");
		System.out.println("2. SELL PRODUCT");
		System.out.println("3. VIEW DAILY TRANSACTION REPORT");
		System.out.println("4. Exit");
		System.out.println("---------------------------------");

		begin:
		while(ch!=4)
		{
			System.out.print("\n");
			System.out.print("\n Enter a choice and Press ENTER to continue[1-4]:");
			ch=Integer.parseInt(br.readLine());
			
			if(ch>4||ch<1)
			{
				System.out.println("This doesn't appear to be a valid option...!");
				continue begin;	
			}
			
			if(ch==1)
			{
				s="y";
				while(s.equals("y")||s.equals("Y"))
				{
					inv.addItem();
					System.out.print("\n Add another[y/n]:");
					s=br.readLine();						
				}
				continue begin;
			}
			else
			if(ch==2)
			{
				s="y";
				while(s.equals("y")||s.equals("Y"))
				{
					inv.makeTransaction();			
					System.out.print("\n Sell another[y/n]:");
					s=br.readLine();						
				}
			}
			else
			if(ch==3)
			{
				inv.displayReport();		
			}	
		}
	}
}
