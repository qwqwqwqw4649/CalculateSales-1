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

		File directory = new File(args[0]);
		if (!directory.exists()) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		//キーと値が共にストリング型のマップオブジェクト
		HashMap<String, String> branchMap = new HashMap<String, String>();
		//空のマップ
		HashMap<String, Long> branchTotalMap = new HashMap<String, Long>();
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long> commodityTotalMap = new HashMap<String, Long>();

		//args[0]とbranch.lstを一つにまとめるためのコード
		String definitionFile = directory.getPath() + File.separator + "branch.lst";
		if (!read(definitionFile, branchMap, branchTotalMap, "^[0-9]{3}$","支店")) {
			return;
		}
		definitionFile = directory.getPath() + File.separator + "commodity.lst";
		if (!read(definitionFile, commodityMap, commodityTotalMap, "^\\w{8}", "商品")) {
			return;
		}

		//対象のリストを作る
		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		List<File> rcdList = new ArrayList<File>();

		// rcdファイルの抽出
		for (int i = 0; i < files.length; i++) {
			 //対象のリストを作る
			File targetFile = files[i];
			//売上ファイルだけにする
			if(targetFile.isFile() && targetFile.getName().matches("^[0-9]{8}.rcd$")) {
				rcdList.add(targetFile);
			}
		}

		// 連番チェックを行う
		for (int i = 0; i < rcdList.size(); i++) {
			int index = Integer.parseInt(rcdList.get(i).getName().split("\\.")[0]);
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
					if (total > 9999999999L) {
						System.out.println("合計金額が10桁を超えました");
						return;
					} else {
						branchTotalMap.put(earningsFile.get(0), total);
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
					if (total > 9999999999L) {
						System.out.println("合計金額が10桁を超えました");
						return;
					} else {
						commodityTotalMap.put(earningsFile.get(1), total);
					}
				} else {
					System.out.println(rcdList.get(i).getName() + "の商品コードが不正です");
					return;
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} catch (NumberFormatException e ) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if (br != null) {
					br.close();
				}
			}

		}
		definitionFile = directory.getPath() + File.separator + "branch.out";
		if (!write(branchTotalMap, branchMap, definitionFile)) {
			System.out.println("予期せぬエラーが発生しましたe");
			return;
		}
		definitionFile = directory.getPath() + File.separator + "commodity.out";
		if (!write(commodityTotalMap, commodityMap, definitionFile)) {
			System.out.println("予期せぬエラーが発生しましたf");
			return;
		}
	}

	public static boolean read(String fileName, HashMap<String, String> nameMap,
			HashMap<String, Long> totalMap, String regex, String target) throws IOException {
		BufferedReader br = null;
		File file = new File(fileName);
		if(!file.exists()) {
			System.out.println(target + "定義ファイルが存在しません");
			return false;
		}

		try {
			br = new BufferedReader(new FileReader(file));
			String str;

			while ((str = br.readLine()) != null) {
				String[] data = str.split(",");
				if(data.length == 2 && data[0].matches(regex)) {   //^[0-9]
					nameMap.put(data[0], data[1]);
					totalMap.put(data[0], 0L);
				} else {
					System.out.println(target + "定義ファイルのフォーマットが不正です");
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


	public static boolean write(HashMap<String, Long> totalMap, HashMap<String, String> nameMap, String fileName) throws IOException {
		BufferedWriter bw = null;
		File file = new File(fileName);
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
