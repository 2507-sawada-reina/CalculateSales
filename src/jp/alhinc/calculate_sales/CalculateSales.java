package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		//listFilesを使⽤してfilesという配列に、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の情報を格納します。
		File[] files = new File(args[0]).listFiles();

		//売上ファイルを格納する List(ArrayList) を宣⾔
		List<File> rcdFiles = new ArrayList<>();

		//filesの数だけ繰り返すことで、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の数だけ繰り返されます。
		for(int i = 0; i < files.length ; i++) {
			String fileName = files[i].getName();

			//matches を使⽤してファイル名が「数字8桁.rcd」なのか判定します。
			if(fileName.matches("^[0-9]{8}.rcd$")) {
			    //trueの場合、リストに追加
				rcdFiles.add(files[i]);
			}
		}


		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
		for(int i = 0; i < rcdFiles.size(); i++) {


			BufferedReader br = null;

			try {
				//売上ファイルの中身を読み込みます。
				//ファイルを開く　args[0]（\Desktop\売上集計課題）にあるn番目のrcdFilesの名前を指定、file型のオブジェクトを作る
				File file = rcdFiles.get(i);
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);

				String line;
				//売上ファイルの読んだ中身を保持するlist　読んだものはstring型
				List<String> fileContents = new ArrayList<>();
				// nullになるまで.readLineメソッドを使って、一行ずつ読み込む→読んだものはlineに代入
				while((line = br.readLine()) != null) {
					//売上ファイルの1行目には支店コード、2行目には売上金額が入っています。これを保持するために、listに追加。
					fileContents.add(line);
				}


			//売上ファイルから読み込んだ売上金額をMapに加算していくために、型の変換を行います。
			long fileSale = Long.parseLong(fileContents.get(1));

			//読み込んだ売上⾦額を加算します。
			Long saleAmount = branchSales.get(fileContents.get(0)) + fileSale;

			//加算した売上⾦額をMapに追加します。
			branchSales.put(fileContents.get(0), saleAmount);


			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}
		}






		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}//mainメソッド

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {

		BufferedReader br = null;

		try {

			//ファイルを開く　path（args[0]＝\Desktop\売上集計課題）にあるfileName（支店定義ファイル）を指定、file型のオブジェクトを作る
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// nullになるまで.readLineメソッドを使って、一行ずつ読み込む→読んだものはlineに代入
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				//split を使って「,」(カンマ)で分割すると、
			    //items[0] には⽀店コード、items[1] には⽀店名が格納されます。
			    String[] items = line.split(",");
			    //Mapに追加する2つの情報を putの引数として指定します。
			    branchNames.put(items[0],items[1] );
			    branchSales.put(items[0], 0L);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)

		return true;
	}

}
