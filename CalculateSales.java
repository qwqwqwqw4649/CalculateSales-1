package jp.co.iccom.fujiya_shiho.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CalculateSales {
	public static void main(String[] args) throws IOException {

		//ファイル名を引数にしてファイルオブジェクトを作成★コマンドライン引数
		File branchFile = new File(args[0], "branch.lst");
		//fileが存在するかを真偽値で判定する
		if(!branchFile.exists()) {
			System.out.println("支店定義ファイルが存在しません");
			//if文の条件に当てはまったときに処理を停止
			return;
		}

		//キーと値が共にストリング型のマップオブジェクト
		HashMap<String, String> branchMap = new HashMap<String, String>();
		BufferedReader br = null;
		try {
			//上で生成したFailオブジェクトを引数にして、文字列を受け取るFileReaderオブジェクトを生成
			FileReader fr = new FileReader(branchFile);
			 //上で生成したFileオブジェクトを引数として、そこから文字列を受け取るFileオブジェクトを生成
			br = new BufferedReader(fr);
		    String str;
		    // 生成したBufferdオブジェクトのreadLineメソッドを使用して文字列データを受け取る
		    while((str = br.readLine()) != null) {
		    	//文字列の分割
		    	String[] branchData = str.split(",");
		    	//whileの処理でちゃんと条件に当てはまるか確認したい
                if(branchData.length == 2 && branchData[0].matches("^[0-9]{3}$") ) {

                //キーと値のペアを追加
				branchMap.put(branchData[0], branchData[1]);
				 //マップのkeyを受け取って、valueをコンソールへ表示
				System.out.println(branchMap.get(branchData[0]));
                } else {
                	//ifの条件分岐に当てはまらなかった場合はエラーメッセージを表示
                	System.out.println("支店定義ファイルのフォーマットが不正です");
                	//条件に当てはまらなかったら、それ以降の処理を停止
                	return;
                }
		    }
		}
		//tryの想定外エラー時に出力するエラー
		catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			//catchに飛んだら以降の処理を停止
			return;
	    }
		finally {
			//ファイル出力のストリームを閉じる
            br.close();
		}


		File commodityFile = new File(args[0], "commodity.lst");
		if(!commodityFile.exists()) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		}

		HashMap<String, String> commodityMap = new HashMap<String, String>();

		try {
			FileReader fr = new FileReader(commodityFile);
			br = new BufferedReader(fr);
			String str;

			while((str = br.readLine()) != null) {
				String[] commodityData = str.split(",");
				if(commodityData.length == 2 && commodityData[0].matches("^\\w{8}")) {   //^[0-9]
					commodityMap.put(commodityData[0], commodityData[1]);
					System.out.println(commodityMap.get(commodityData[0]));
				} else {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
			}
		}
		catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		}
		finally {
			br.close();
		}

		//拡張子rcd、ファイル名が8桁連番のファイルを検索
		//dir=ディレクトリ
//		File dir = new File(args[0]);
//		String filelist[] = dir.list();
//		for(int i = 0; i < filelist.length; i++) {
//			if("filelist".maches("rcd")) {
//			System.out.println(filelist[i])
//
//
//

			
		File dir = new File(args[0]);
	    File[] files = dir.listFiles();
	    for (int i = 0; i < files.length; i++) {
	        File file = files[i];

       	 	System.out.println(i + ":    " + file.getName());

       	 	//file.getNameはString型になってる!!
	        if(file.getName().matches("^[0-9]{8}.rcd$")) {
	        	String str = file.getName();
	        	System.out.println(str.substring(0,8));
	        	int j = Integer.parseInt(str);
	        	
	        	//List<File> earnings  = new ArrayList<File>();
	        	//earnings.add(file);
	        	//File list = (earnings) .get(j);
	        	
	        	for(j += 1; j < 5; j++) {
	        		FileReader fr = new FileReader(str);
	        		
		        	
	        		
	        		
	        	}
	        	
	        	//System.out.println(file.getName());
	        	
	        } else {
	        	System.out.println("売上ファイルが連番になっていません");
	        }


		}
	}

}





