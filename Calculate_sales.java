package jp.co.iccom.fujiya_shiho.calculate_sales;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class Calculate_sales {



	public static void main(String[] args) {

		File file = new File(args[0],"branch.lst");  //ファイル名を引数にしてファイルオブジェクトを作成
		HashMap<String, String> map = new HashMap<String, String>();  //キーと値が共にストリング型のマップオブジェクト
		try {
			FileReader fr = new FileReader(file);  //上で生成したFailオブジェクトを引数にして、文字列を受け取るFileReaderオブジェクトを生成
			BufferedReader br = new BufferedReader(fr);  //上で生成したFileオブジェクトを引数として、そこから文字列を受け取るFileオブジェクトを生成
		    String s;
		    while((s = br.readLine()) != null) {  // 生成したBufferdオブジェクトのreadLineメソッドを使用して文字列データを受け取る
		    	String[] branchData = s.split(",");  //文字列の分割
		    	
                if(branchData[0].matches("^[0-9]{3}$") && branchData.length == 2 ) {
                	
                
				map.put(branchData[0], branchData[1]);  //キーと値のペアを追加

                }

//				System.out.println(map.values());
//				System.out.println(map.keySet());
//				System.out.println(map.get(branchData[0]));
//				System.out.println(map.get(branchData[1]));

//		    	String branchNo = branchData[0];
//		    	String branchName = branchData[1];

		    }
		    br.close();  //ファイル出力のストリームを閉じる
		}
		catch (IOException e) { //tryでファイルが存在しない時に出力するエラー
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(e);
	    }
		catch (ArrayIndexOutOfBoundsException e) { //tryでフォーマットが不正な場合に出力するエラー
	    	System.out.println("支店定義ファイルのフォーマットが不正です");
	    	System.out.println(e);

	    }

		 System.out.println(map.entrySet()); //格納されている値を出力

	}



}

