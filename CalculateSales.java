package jp.co.iccom.fujiya_shiho.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CalculateSales {
		Map<String, String> branchmap = new HashMap<>();
	Map<String, Long> salesmap = new HashMap<>();
	BufferedReader br =null;
	BufferedWriter bw = null;{

	//		支店定義ファイルのある場所を指定する
	try {
		File branchfile=new File (args[0],"branch.lst");
//			支店定義ファイルがないときの例外処理
			if(!branchfile.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return ;
			}

		br = new BufferedReader(new FileReader(branchfile));
		String line;
		while ((line =br.readLine())!= null) {
			//				1行づつ読み込んだデータをカンマで分割する[0]にコード[1]に支店がある
			String[] items = line.split(",");

			branchmap.put(items[0], items[1]);

			if(items.length != 2 || !items[0].matches("\\d{3}") || items[1].isEmpty()){
				System.out.println("支店定義ファイルのフォーマットが不正です");
				return ;
			}
		}
	} finally {
		if(br != null) {
			br.close();
		}
	}
	//		売上ファイルを数字8桁.rcdを指定し読み込む
	try {
		FilenameFilter filter = new FilenameFilter() {
			 public boolean accept(File file,String str) {
				if(str.matches("\\d{8}.rcd")){ //ファイルの条件を指定/					
					return true;
				} else {
					return false;
				}
			}
		};

		File []files = new File(args[0]).listFiles(filter);
		for(int i=0; i<files.length; ++i){
			br = new BufferedReader(new FileReader(files[i]));
			
				int index = Integer.parseInt(files[i].getName().split("\\.")[0]);
				if ((i + 1) != index) {
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			String line1;//コード
			String line2;//売上
			line1 = br.readLine();
			line2 = br.readLine();
			long line3 = Long.parseLong(line2);

			if (salesmap.containsKey(line1)) {
				salesmap.put(line1, salesmap.get(line1) + line3);
			} else {
				salesmap.put(line1, line3);
			}

//			if (files - i != 1) {
//				System.out.println("ファイルが連番になっていません");
//				return;
//			}
			for(Map.Entry<String, Long> sale : salesmap.entrySet()) {
				if(salesmap.get(sale.getKey())> 10000000000L) {
					System.out.println("合計金額が10桁を超えました。");
					return ;
				}
			}
		}
	}finally {
		try {
		br.close();
		}catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました。");
		}
	}
	try {
		File file = new File(args[0],"branch.out");
		bw = new BufferedWriter (new FileWriter(file));
		for(Map.Entry<String, Long> sales : salesmap.entrySet()) {
			String data=(sales.getKey() + "," + branchmap.get(sales.getKey()) + "," + salesmap.get(sales.getKey()));
			bw.write(data);
			bw.newLine();
			
			if(salesmap.get(branchmap.get(0)) == null){
				System.out.println( branchmap + "の支店コードが不正です");
				return;
			}
		}
	} catch (IOException e) {
		System.out.println("予期せぬエラーが発生しました。");
	} finally {
		try {
			bw.close();
		}catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました。");
		}
	}
	}
}

/*
	①支店定義ファイルのある場所を指定する

	②フォルダ内にある支店定義ファイルを1行ずつ読み込む

	③読み込んだファイル内のデータをcode,支店名に分けてMapに入れ込み保持する

	④	結果的に割愛

	⑤売上ファイル 数字8桁.rcdを指定し読み込む

	⑥読み込んだファイル内データからcode.売上を抽出する

	⑦抽出した売り上げを該当する支店の合計金額に加算する

	⑧売上ファイルの数だけ繰り返す ここは⑦にて割愛

	⑨支店定義ファイルを読み込んだフォルダへ出力する　書き出すときにデータを合わせる

	⑩エラー内容

	⑤の際にファイルが連番になっていなければエラー

	⑤の際に売り上げファイルの中身が3行以上の場合エラー
	⑨の際に該当の支店がなければエラー
*/
