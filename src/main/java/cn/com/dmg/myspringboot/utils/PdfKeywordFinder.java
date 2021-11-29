package cn.com.dmg.myspringboot.utils;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * pdf查找关键字
 * @author 万源文
 */
public class PdfKeywordFinder {


	public static void main(String[] args) throws IOException {
		String keyword = "签章";
		String pdfUrl="C:\\Users\\zhum\\Desktop\\TECH_WORD_192_168_5_161_20211125164339481_6290.pdf";
		List<float[]> positions = findKeywordPositions(pdfUrl, keyword);
		System.out.println("total:" + positions.size());
		for (float[] position : positions) {
			System.out.print("pageNum: " + (int) position[0]);
			System.out.print("\tx: " + position[1]);
			System.out.println("\ty: " + position[2]);
		}
	}

	/**
	 * 在pdf查找关键字坐标
	 * @param pdfUrl pdf的路径
	 * @param keyword 关键字
	 * @return 关键字坐标集合，集合里面float数组对应的是[页数,x坐标,y坐标]
	 * @throws IOException
	 */
	public static List<float[]> findKeywordPositions(String pdfUrl, String keyword) throws IOException {
		File pdfFile = new File(pdfUrl);
		byte[] pdfData = new byte[(int) pdfFile.length()];
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(pdfFile);
			inputStream.read(pdfData);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return findKeywordPositions(pdfData,keyword);
	}

	/**
	 * 在pdf查找关键字坐标
	 * @param pdfData pdf的字节数组
	 * @param keyword 关键字
	 * @return 关键字坐标集合，集合里面float数组对应的是[页数,x坐标,y坐标]
	 * @throws IOException
	 */
	public static List<float[]> findKeywordPositions(byte[] pdfData, String keyword) throws IOException {
		List<float[]> result = new ArrayList<>();
		List<PdfPageContentPositions> pdfPageContentPositions = getPdfContentPositionsList(pdfData);

		for (PdfPageContentPositions pdfPageContentPosition : pdfPageContentPositions) {
			List<float[]> charPositions = findPositions(keyword, pdfPageContentPosition);
			if (charPositions.size() < 1) {
				continue;
			}
			result.addAll(charPositions);
		}
		return result;
	}

	private static List<PdfPageContentPositions> getPdfContentPositionsList(byte[] pdfData) throws IOException {
		PdfReader reader = new PdfReader(pdfData);
		List<PdfPageContentPositions> result = new ArrayList<>();
		int pages = reader.getNumberOfPages();
		for (int pageNum = 1; pageNum <= pages; pageNum++) {
			float width = reader.getPageSize(pageNum).getWidth();
			float height = reader.getPageSize(pageNum).getHeight();
			PdfRenderListener pdfRenderListener = new PdfRenderListener(pageNum, width, height);
			//解析pdf，定位位置
			PdfContentStreamProcessor processor = new PdfContentStreamProcessor(pdfRenderListener);
			PdfDictionary pageDic = reader.getPageN(pageNum);
			PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
			try {
				processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNum), resourcesDic);
			} catch (IOException e) {
				reader.close();
				throw e;
			}
			String content = pdfRenderListener.getContent();
			List<CharPosition> charPositions = pdfRenderListener.getCharPositions();
			List<float[]> positionsList = new ArrayList<>();
			for (CharPosition charPosition : charPositions) {
				float[] positions = new float[] { charPosition.getPageNum(), charPosition.getX(), charPosition.getY() };
				positionsList.add(positions);
			}
			PdfPageContentPositions pdfPageContentPositions = new PdfPageContentPositions();
			pdfPageContentPositions.setContent(content);
			pdfPageContentPositions.setPositions(positionsList);
			result.add(pdfPageContentPositions);
		}
		reader.close();
		return result;
	}


	private static List<float[]> findPositions(String keyword, PdfPageContentPositions pdfPageContentPositions) {
		List<float[]> result = new ArrayList<>();
		String content = pdfPageContentPositions.getContent();
		List<float[]> charPositions = pdfPageContentPositions.getPositions();
		for (int pos = 0; pos < content.length();) {
			int positionIndex = content.indexOf(keyword, pos);
			if (positionIndex == -1) {
				break;
			}
			float[] positions = charPositions.get(positionIndex);
//			for(float f:positions){
//				System.out.println(f);
//			}
			result.add(positions);
			pos = positionIndex + 1;
		}
		return result;
	}


	private static class PdfPageContentPositions {
		private String content;
		private List<float[]> positions;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public List<float[]> getPositions() {
			return positions;
		}

		public void setPositions(List<float[]> positions) {
			this.positions = positions;
		}
	}


	private static class PdfRenderListener implements RenderListener {
		private int pageNum;
		private float pageWidth;
		private float pageHeight;
		private StringBuilder contentBuilder = new StringBuilder();
		private List<CharPosition> charPositions = new ArrayList<>();


		public PdfRenderListener(int pageNum, float pageWidth, float pageHeight) {
			this.pageNum = pageNum;
			this.pageWidth = pageWidth;
			this.pageHeight = pageHeight;
		}

		@Override
		public void beginTextBlock() {
		}

		@Override
		public void renderText(TextRenderInfo renderInfo) {
			List<TextRenderInfo> characterRenderInfos = renderInfo.getCharacterRenderInfos();
			for (TextRenderInfo textRenderInfo : characterRenderInfos) {
				String word = textRenderInfo.getText();
				if (word.length() > 1) {
					word = word.substring(word.length() - 1, word.length());
				}
				Rectangle2D.Float rectangle = textRenderInfo.getAscentLine().getBoundingRectange();
				float x = (float) rectangle.getMinX();
				float y = (float) rectangle.getMaxY();
//				float xPercent = Math.round(x / pageWidth * 10000) / 10000f;
//				float yPercent = Math.round(y / pageHeight * 10000) / 10000f;

				CharPosition charPosition = new CharPosition(pageNum, x, y);
				charPositions.add(charPosition);
				contentBuilder.append(word);
			}
		}

		@Override
		public void endTextBlock() {
		}


		@Override
		public void renderImage(ImageRenderInfo renderInfo) {
		}

		public String getContent() {
			return contentBuilder.toString();
		}

		public List<CharPosition> getCharPositions() {
			return charPositions;
		}
	}

	private static class CharPosition {
		private int pageNum = 0;
		private float x = 0;
		private float y = 0;

		public CharPosition(int pageNum, float x, float y) {
			this.pageNum = pageNum;
			this.x = x;
			this.y = y;
		}

		public int getPageNum() {
			return pageNum;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		@Override
		public String toString() {
			return "[pageNum=" + this.pageNum + ",x=" + this.x + ",y=" + this.y + "]";
		}
	}
}
