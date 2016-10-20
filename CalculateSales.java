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

		//キーと値が共にストリング型のマップオブジェクト
		HashMap<String, String> branchMap = new HashMap<String, String>();
		//空のマップ
		HashMap<String, Long>branchTotalMap = new HashMap<String, Long>();

		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long>commodityTotalMap = new HashMap<String, Long>();

		if (!read(args[0], "branch.lst", branchMap, branchTotalMap, "^[0-9]{3}$","支店")) {
			return;
		}
		if (!read(args[0], "commodity.lst", commodityMap, commodityTotalMap, "^\\w{8}", "商品")) {
			return;
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
		BufferedReader br = null;
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

//		read(args[0], "branch.lst", branchMap, branchTotalMap, "^[0-9]{3}$","支店");
//		read(args[0], "commodity.lst", commodityMap, commodityTotalMap, "^\\w{8}", "商品");
//		write(branchTotalMap, branchMap, args[0], "branch.out");
//		write(commodityTotalMap, commodityMap, args[0], "commodity.out");
		if (!write(branchTotalMap, branchMap, args[0], "branch.out")) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		if (!write(commodityTotalMap, commodityMap, args[0], "commodity.out")) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}

	public static boolean read(String path, String fileName, HashMap<String, String> nameMap,
			HashMap<String, Long> totalMap, String conditions, String error) throws IOException {
		BufferedReader br = null;
		File file = new File(path,fileName);
		if(!file.exists()) {
			System.out.println(error + "定義ファイルが存在しません");
			return false;
		}

		try {
			br = new BufferedReader(new FileReader(file));
			String str;

			while((str = br.readLine()) != null) {
				String[] data = str.split(",");
				if(data.length == 2 && data[0].matches(conditions)) {   //^[0-9]
					nameMap.put(data[0], data[1]);
					totalMap.put(data[0], 0L);
				} else {
					System.out.println(error + "定義ファイルのフォーマットが不正です");
					return false;
				}
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
			if(br != null) {
				br.close();
			}
		}
		return true;
	}


	public static boolean write(HashMap<String, Long> totalMap, HashMap<String, String> nameMap, String path, String fileName) throws IOException {
		BufferedWriter bw = null;
		File file = new File(path, fileName);
		try {
			bw = new BufferedWriter(new FileWriter(file));
			List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(totalMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for (Entry<String,Long> s : entries) {
				bw.write(s.getKey() + "," + nameMap.get(s.getKey()) + "," + s.getValue() + (System.getProperty("line.separator")));
			}
		} catch (IOException e) {
			return false;
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
		return true;
	}
}
