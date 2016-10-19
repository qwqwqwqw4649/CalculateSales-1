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
		//空のマップ
		HashMap<String, Integer>branchTotalMap = new HashMap<String, Integer>();
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
				//

				branchTotalMap.put(branchData[0], 0);


				 //マップのkeyを受け取って、valueをコンソールへ表示
//				System.out.println(branchTotalMap.get(branchData[0]));
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
		HashMap<String, Integer>commodityTotalMap = new HashMap<String, Integer>();

		try {
			FileReader fr = new FileReader(commodityFile);
			br = new BufferedReader(fr);
			String str;

			while((str = br.readLine()) != null) {
				String[] commodityData = str.split(",");
				if(commodityData.length == 2 && commodityData[0].matches("^\\w{8}")) {   //^[0-9]
					commodityMap.put(commodityData[0], commodityData[1]);
//					System.out.println(commodityMap.get(commodityData[0]));
					commodityTotalMap.put(commodityData[0], 0);
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


		//対象のリストを作る
		File dir = new File(args[0]);
	    File[] files = dir.listFiles();
	    List<File> rcdList = new ArrayList<File>();

	    // rcdファイルの抽出
	    for (int i = 0; i < files.length; i++) {
	    	 //対象のリストを作る
	    	File file = files[i];
	        //売上ファイルだけにする
	        if(file.getName().matches("^[0-9]{8}.rcd$")) {
	        	rcdList.add(file);
	        }
	    }

	    // 連番チェックを行う
	    for (int i = 0; i < rcdList.size(); i++) {
        	String str = rcdList.get(i).getName();
        	int index = Integer.parseInt(str.split("\\.")[0]);
        	if ((i + 1) == index) {
        	} else {
        		System.out.println("売上ファイル名が連番になってません");
        	}
		}

	    // 集計をしていく

	    for (int i = 0; i < rcdList.size(); i++) {
	    	FileReader fr = new FileReader(rcdList.get(i));
		    try {
		    	List<String> earningsFile = new ArrayList<String>();
		    	String str;
		    	br = new BufferedReader(fr);
		    	while ((str = br.readLine()) != null) {
		    		// ArrayLiatに1行ずつ入れていく
		    		earningsFile.add(str);

		    	}

		    	if (earningsFile.size() != 3) {
		    		System.out.println(earningsFile.size() + "のフォーマットが不正です");
		    	}

		    	if (branchTotalMap.containsKey(earningsFile.get(0))) {
		    		//Integerではダメだけど、後で編集するから進める
		    		int value = Integer.parseInt(earningsFile.get(2));
		    		int total = branchTotalMap.get(earningsFile.get(0));
		    		total += value;
		    		branchTotalMap.put(earningsFile.get(0), total);
		    		if (total > 999999999) {
		    			System.out.println("合計金額が10桁を超えました");
		    		}
		    		
		    	} else {
		    		System.out.println(earningsFile.get(0) + "の支店コードが不正です");
		    	}

		    	if (commodityTotalMap.containsKey(earningsFile.get(1))) {
		    		int value = Integer.parseInt(earningsFile.get(2));
		    		int total = commodityTotalMap.get(earningsFile.get(1));
		    		total += value;
		    		commodityTotalMap.put(earningsFile.get(1), total);
		    		if (total > 999999999) {
		    			System.out.println("合計金額が10桁を超えました");
		    		}
		    	} else {
		    		System.out.println(earningsFile.get(1) + "の商品コードが不正です");
		    	}

		     }
		    catch (IOException e) {
		    	//System.out.println(e);
		    	System.out.println("予期せぬエラーが発生しました");
		    	return;
		    }

		    finally {
		    	br.close();
		    	fr.close();
	   		}



//		    File branchOut = new File(args[0], "branch.out");
//		    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(branchOut)));
//
//		    try {
//		    	if (branchOut.createNewFile()) {
//
//		    	}
//
//		    }
//	    	catch (IOException e) {
//	    		System.out.println(e);
//	    		System.out.println("エラーです");
//	    	}
//		    finally {
//		    	pw.close();
//		    }



	    }

	    try {
	    	File branchOut = new File(args[0], "branch.out");
		    FileWriter fw = new FileWriter(branchOut);
		    BufferedWriter bw = new BufferedWriter(fw);
		   
		    List<Map.Entry<String,Integer>> entries = 
		              new ArrayList<Map.Entry<String,Integer>>(branchTotalMap.entrySet());
		        Collections.sort(entries, new Comparator<Map.Entry<String,Integer>>() {
		 
		            @Override
		            public int compare(
		                  Entry<String,Integer> entry1, Entry<String,Integer> entry2) {
		                return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());
		            }
		        });
		        
		        for (Entry<String,Integer> s : entries) {
		            bw.write(s.getKey() + "," + branchMap.get(s.getKey()) + "," + s.getValue() + "\r\n");
		        }

//		    for (String key : branchMap.keySet())
		    	//bw.write(key + "," + branchMap.get(key) + "," + branchTotalMap.get(key) + "\r\n");

		    bw.close();
	    }
	    catch (IOException e) {
	    	System.out.println(e);
	    }

	    try {
	    	File commodityOut = new File(args[0], "commodity.out");
	    	FileWriter fw = new FileWriter(commodityOut);
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	
	    	List<Map.Entry<String,Integer>> entries = 
		              new ArrayList<Map.Entry<String,Integer>>(commodityTotalMap.entrySet());
		        Collections.sort(entries, new Comparator<Map.Entry<String,Integer>>() {
		 
		            @Override
		            public int compare(
		                  Entry<String,Integer> entry1, Entry<String,Integer> entry2) {
		                return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());
		            }
		        });
		        
		        for (Entry<String,Integer> s : entries) {
		            System.out.println("s.getKey() : " + s.getKey());
		            System.out.println("s.getValue() : " + s.getValue());
		            bw.write(s.getKey() + "," + commodityMap.get(s.getKey()) + "," + s.getValue() + "\r\n");
		        }
	    	bw.close();
	    }
	    catch (IOException e) {
	    	System.out.println(e);
	    }



	}

}





