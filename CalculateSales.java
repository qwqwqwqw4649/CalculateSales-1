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

		if (args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		File mainFile = new File(args[0]);
		if (!mainFile.exists()) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		//ファイル名を引数にしてファイルオブジェクトを作成★コマンドライン引数
		File branchFile = new File(args[0], "branch.lst");
		//fileが存在するかを真偽値で判定する
		if (!branchFile.exists()) {
			System.out.println("支店定義ファイルが存在しません");
			//if文の条件に当てはまったときに処理を停止
			return;
		}

		//キーと値が共にストリング型のマップオブジェクト
		HashMap<String, String> branchMap = new HashMap<String, String>();
		//空のマップ
		HashMap<String, Long>branchTotalMap = new HashMap<String, Long>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(branchFile));
			String str;
			// 生成したBufferdオブジェクトのreadLineメソッドを使用して文字列データを受け取る
			while((str = br.readLine()) != null) {
				//文字列の分割
				String[] branchData = str.split(",");
				//whileの処理でちゃんと条件に当てはまるか確認したい
				if(branchData.length == 2 && branchData[0].matches("^[0-9]{3}$") ) {
					//キーと値のペアを追加
					branchMap.put(branchData[0], branchData[1]);
					branchTotalMap.put(branchData[0], 0L);
				} else {
					//ifの条件分岐に当てはまらなかった場合はエラーメッセージを表示
					System.out.println("支店定義ファイルのフォーマットが不正です");
					//条件に当てはまらなかったら、それ以降の処理を停止
					return;
				}
			}
		  //tryの想定外エラー時に出力するエラー
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			//catchに飛んだら以降の処理を停止
			return;
		} finally {
			//ファイル出力のストリームを閉じる
			if (br != null) {
				br.close();
			}
		}

		File commodityFile = new File(args[0], "commodity.lst");
		if(!commodityFile.exists()) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		}

		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long>commodityTotalMap = new HashMap<String, Long>();

		try {
			br = new BufferedReader(new FileReader(commodityFile));
			String str;
			while((str = br.readLine()) != null) {
				String[] commodityData = str.split(",");
				if(commodityData.length == 2 && commodityData[0].matches("^\\w{8}")) {   //^[0-9]
					commodityMap.put(commodityData[0], commodityData[1]);
					commodityTotalMap.put(commodityData[0], 0L);
				} else {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			if (br != null) {
				br.close();
			}
		}

		//対象のリストを作る
		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		List<File> rcdList = new ArrayList<File>();

		// rcdファイルの抽出
		for (int i = 0; i < files.length; i++) {
			 //対象のリストを作る
			File file = files[i];
			//売上ファイルだけにする
			if(file.isFile() && file.getName().matches("^[0-9]{8}.rcd$")) {
				rcdList.add(file);
			}
		}

		// 連番チェックを行う
		for (int i = 0; i < rcdList.size(); i++) {
			String str = rcdList.get(i).getName();
			int index = Integer.parseInt(str.split("\\.")[0]);
			if ((i + 1) != index) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}

		// 集計をしていく
		for (int i = 0; i < rcdList.size(); i++) {
			try {
				List<String> earningsFile = new ArrayList<String>();
				String str;
				br = new BufferedReader(new FileReader(rcdList.get(i)));
				while ((str = br.readLine()) != null) {
					// ArrayLiatに1行ずつ入れていく
					earningsFile.add(str);
				}
				if (earningsFile.size() != 3) {
					String fileName = rcdList.get(i).getName();
					System.out.println(fileName + "のフォーマットが不正です");
					return;
				}
				if (branchTotalMap.containsKey(earningsFile.get(0))) {
					//Integerではダメだけど、後で編集するから進める⇒Longでないと桁数が足りないから
					long value = Long.parseLong(earningsFile.get(2));
					long total = branchTotalMap.get(earningsFile.get(0));
					total += value;
					branchTotalMap.put(earningsFile.get(0), total);
					if (total > 9999999999L) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
				} else {
					String fileName = rcdList.get(i).getName();
					System.out.println(fileName + "の支店コードが不正です");
					return;
				}
				if (commodityTotalMap.containsKey(earningsFile.get(1))) {
					long value = Long.parseLong(earningsFile.get(2));
					long total = commodityTotalMap.get(earningsFile.get(1));
					total += value;
					commodityTotalMap.put(earningsFile.get(1), total);
					if (total > 9999999999L) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
				} else {
					String fileName = rcdList.get(i).getName();
					System.out.println(fileName + "の商品コードが不正です");
					return;
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if (br != null) {
					br.close();
				}
			}
		}

		BufferedWriter bw = null;
		try {
			File branchOut = new File(args[0], "branch.out");
			bw = new BufferedWriter(new FileWriter(branchOut));
			List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(branchTotalMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for (Entry<String,Long> s : entries) {
				bw.write(s.getKey() + "," + branchMap.get(s.getKey()) + "," + s.getValue() + (System.getProperty("line.separator")));
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			if (bw != null) {
				bw.close();
			}
		}

		try {
			File commodityOut = new File(args[0], "commodity.out");
			bw = new BufferedWriter(new FileWriter(commodityOut));
			List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(commodityTotalMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
					public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
						return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for (Entry<String,Long> s : entries) {
				bw.write(s.getKey() + "," + commodityMap.get(s.getKey()) + "," + s.getValue() + (System.getProperty("line.separator")));
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			if (br != null){
				bw.close();
			}
		}
	}
}
