package sparkling_beauty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MyEn {

	public static void main(String[] args) {
		
		File input = new File("/Users/uzini/Desktop/Sparkling Beauty/my_en/Sparkling Beauty.html");
		try {
			//스파클링 뷰티 html 문서 불러오기
			Document doc = Jsoup.parse(input, "UTF-8", "https://www.laneige.com/my/en/sparkling-beauty.html");
			
			// 각 item의 href 파싱
			Elements el = doc.select(".sparkling-list-item").not(".sparkling-banner");
			String[] hrefs = new String[el.size()];
			
			// href 문자열 저장
			for(int i=0; i<el.size(); i++) {
				hrefs[i] = el.get(i).select("a").attr("href");
			}
			
			// href 접속
			int colSize = 8;
			Document itemDoc;
			String[][] itemList = new String[el.size()][colSize];
			String src;
			Elements li;
			String tag = "";

			for(int i=0; i<el.size(); i++) {
				itemDoc = Jsoup.connect(hrefs[i]).get();
				// [0] <title>
				itemList[i][0] = itemDoc.title();
				System.out.println("["+i+"] <title> : " + itemList[i][0]);
				// [1] og:title
				itemList[i][1] = itemDoc.select("meta[property^=og:title]").attr("content");
				System.out.println("["+i+"] og:title : " + itemList[i][1]);
				// [3] description
				itemList[i][2] = itemDoc.select("meta[property^=og:description]").attr("content");
				System.out.println("["+i+"] og:description : " + itemList[i][2]);
				// [4] 제목
				itemList[i][3] = replaceStr(itemDoc.select(".content_Title").html());
				System.out.println("["+i+"] 제목 : " + itemList[i][3]);
				// [5] img src
				src = itemDoc.select(".custom-sparkling-view-imgtype").select("img").attr("src");
				itemList[i][4] = src.substring(src.lastIndexOf("/")+1);
				System.out.println("["+i+"] src : " + itemList[i][4]);
				// [6] 내용 p
				itemList[i][5] = replaceStr(itemDoc.select(".sparkling-view-context").select("p").html());
				System.out.println("["+i+"] 내용 : " + itemList[i][5]);
				// [7] 태그
				li = itemDoc.select(".sparkling-hash").select("li");
				tag = "";
				for(int j=0; j<li.size(); j++) {
					tag += li.get(j).select("span").html() + ",";
				}
				itemList[i][6] = tag;
				System.out.println("["+i+"] tag " + itemList[i][6]);
 			}
			
			// 엑셀 삽입
			
	        HSSFWorkbook workbook = new HSSFWorkbook(); // 새 엑셀 생성
	        HSSFSheet sheet = workbook.createSheet("sheet1"); // 새 시트(Sheet) 생성
	        
	        HSSFRow row;
	        HSSFCell cell;
	        
	        for(int i=0; i<el.size(); i++) {
	        	row = sheet.createRow(i);
	        	for(int j=0; j<colSize; j++) {
	        		cell = row.createCell(j);
	        		cell.setCellValue(itemList[i][j]);
	        	}
	        }
	        try {
	            FileOutputStream fileoutputstream = new FileOutputStream("/Users/uzini/Desktop/Sparkling Beauty/my_en/my_en_data.xlsx");
	            workbook.write(fileoutputstream);
	            fileoutputstream.close();
	            System.out.println("엑셀파일생성성공");
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("엑셀파일생성실패");
	        }
			
			
						
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String replaceStr(String a) {
		a= a.replaceAll("\\&", "&amp;");
		a= a.replaceAll("\\&amp;nbsp;", "&nbsp;");
		a= a.replaceAll("\\&amp;amp;", "&amp;");
		a= a.replaceAll("\"", "&quot;");
		a= a.replaceAll("\'", "&#39;");
		a= a.replaceAll("\\(", "&#40;");
		a= a.replaceAll("\\)", "&#41;");
		a= a.replaceAll(";;", ";");
		a= a.replaceAll("<br>", "<br/>");
		
		return a;
	}	

}
