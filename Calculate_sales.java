package jp.co.iccom.fujiya_shiho.calculate_sales;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class Calculate_sales {



	public static void main(String[] args) {
		
		//ファイル名を引数にしてファイルオブジェクトを作成
		File file = new File(args[0],"branch.lst");
		
		//キーと値が共にストリング型のマップオブジェクト
		HashMap<String, String> map = new HashMap<String, String>();  
		try {
			//上で生成したFailオブジェクトを引数にして、文字列を受け取るFileReaderオブジェクトを生成
			FileReader fr = new FileReader(file);  
			 //上で生成したFileオブジェクトを引数として、そこから文字列を受け取るFileオブジェクトを生成
			BufferedReader br = new BufferedReader(fr); 
		    String s;
		    // 生成したBufferdオブジェクトのreadLineメソッドを使用して文字列データを受け取る
		    while((s = br.readLine()) != null) {  
		    	//文字列の分割
		    	String[] branchData = s.split(",");  
		    	//whileの処理でちゃんと条件に当てはまるか確認したい
                if(branchData[0].matches("^[0-9]{3}$") && branchData.length == 2 ) {  

                //キーと値のペアを追加
				map.put(branchData[0], branchData[1]); 
				 //マップのkeyを受け取って、valueをコンソールへ表示
				System.out.println(map.get(branchData[0])); 
                } else {
                	//ifの条件分岐に当てはまらなかった場合はエラーメッセージを表示
                	System.out.println("支店定義ファイルのフォーマットが不正です");
                	//条件に当てはまらなかったら、それ以降の処理を停止
                	return;  
                }

//				System.out.println(map.values());
//				System.out.println(map.keySet());
//				System.out.println(map.get(branchData[0]));
//				System.out.println(map.get(branchData[1]));

//		    	String branchNo = branchData[0];
//		    	String branchName = branchData[1];

		    }
		    //ファイル出力のストリームを閉じる
		    br.close();  
		}
		//tryでファイルが存在しない時に出力するエラー
		catch (IOException e) { 
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(e);
	    }
//		catch (ArrayIndexOutOfBoundsException e) { //tryでフォーマットが不正な場合に出力するエラー
//	    	System.out.println("支店定義ファイルのフォーマットが不正です");
//	    	System.out.println(e);

	    }
       	//格納されている値を出力
//		System.out.println(map.entrySet());

	}





