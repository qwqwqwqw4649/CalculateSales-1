package jp.co.iccom.fujiya_shiho.calculate_sales;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class Branch {



	public static void main(String[] args) {
		File file = new File(args[0],"branch.lst");
		    try {
			    FileReader fr = new FileReader(file);
			    BufferedReader br = new BufferedReader(fr);
			    String s;
			    while((s = br.readLine()) != null) {
			    	
			    	System.out.println(s);
			    }
			    br.close();
		} catch (IOException e) {
			System.out.println("支店定義ファイルが存在しません");

		        }




		}



	}

