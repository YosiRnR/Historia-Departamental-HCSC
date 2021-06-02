package org.hcsc.hl7;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//import com.planbase.pdf.layoutmanager.BorderStyle;
//import com.planbase.pdf.layoutmanager.Cell;
//import com.planbase.pdf.layoutmanager.CellBuilder;
//import com.planbase.pdf.layoutmanager.CellStyle;
//import com.planbase.pdf.layoutmanager.CellStyle.Align;
//import com.planbase.pdf.layoutmanager.LineStyle;
//import com.planbase.pdf.layoutmanager.LogicalPage;
//import com.planbase.pdf.layoutmanager.LogicalPage.Orientation;
//import com.planbase.pdf.layoutmanager.Padding;
//import com.planbase.pdf.layoutmanager.PdfLayoutMgr;
//import com.planbase.pdf.layoutmanager.ScaledJpeg;
//import com.planbase.pdf.layoutmanager.TableBuilder;
//import com.planbase.pdf.layoutmanager.TextStyle;
//import com.planbase.pdf.layoutmanager.XyOffset;

import java.awt.Color;
//import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Arrays;

//import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
//import static java.awt.Color.*;
//import static com.planbase.pdf.layoutmanager.CellStyle.Align.*;


public class GeneradorInformePDF {
	
//    private static <T> List<T> vec(T... ts) { return Arrays.asList(ts); }
    
	// ATRIBUTOS //
	private PDDocument document               = null;
	private PDPageContentStream contentStream = null;
	private PDFont documentFont               = null;
	private PDFont documentFontBold           = null;	
	private ArrayList<PDPage> pages           = new ArrayList<PDPage>();
	
	private int cursorPosY = 0;
	private float pageHeight;
	private float fontLeading;
	
	public final int HCSC_LOGO_HEIGHT   = 50;
	private final int CONTENT_FONT_SIZE = 12;
	
//	private final float pMargin = 40;
//	
//	private final Padding textCellPadding = Padding.of((float)2.5);
//	
//	private final TextStyle titleInfoText = TextStyle.of(PDType1Font.TIMES_ROMAN, (float)8, BLACK);
//    private final CellStyle titleHeadCell = CellStyle.of(BOTTOM_RIGHT, textCellPadding, null,
//															BorderStyle.NO_BORDERS);
//    private final CellStyle titleFootCellL = CellStyle.of(BOTTOM_LEFT, textCellPadding, null,
//    												BorderStyle.NO_BORDERS);
//    private final CellStyle titleFootCellR = CellStyle.of(BOTTOM_RIGHT, textCellPadding, null,
//													BorderStyle.NO_BORDERS);
//    
//    private final CellStyle titleTextCell = CellStyle.of(MIDDLE_LEFT, textCellPadding, new Color(220, 220, 220),
//    											BorderStyle.NO_BORDERS);
//	
//	private final TextStyle headingText = TextStyle.of(PDType1Font.TIMES_BOLD, (float)14, BLACK);
//    private final CellStyle headingCell = CellStyle.of(MIDDLE_LEFT, textCellPadding, new Color(220, 220, 220),
//                         						BorderStyle.NO_BORDERS);
//    
//    final TextStyle regularText = TextStyle.of(PDType1Font.TIMES_ROMAN, (float)12, BLACK);
//    final CellStyle regularCell = CellStyle.of(MIDDLE_LEFT, textCellPadding, null,
//    								BorderStyle.builder()
//    									.left(LineStyle.of(BLACK))
//    									.right(LineStyle.of(BLACK))
//    									.bottom(LineStyle.of(BLACK))
//    									.top(LineStyle.of(BLACK))
//    									.build());
//
//	
//	public ByteArrayOutputStream generarInformePDF(JsonObject jsonData, ServletContext servletContext) throws IOException {
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		
//		PdfLayoutMgr pageMgr = PdfLayoutMgr.newRgbPageMgr();
//		
//        LogicalPage lp = pageMgr.logicalPageStart(Orientation.PORTRAIT);
//        
//        float y = (float) lp.yPageTop();
//        
//        File logoFile      = new File(servletContext.getRealPath("/img/logoSmall.jpg"));
//        BufferedImage logo = ImageIO.read(logoFile);
//		lp.putRow(pMargin - 35, y + 35, Cell.builder(CellStyle.DEFAULT, 500)
//					.add(ScaledJpeg.of(logo, 157, this.HCSC_LOGO_HEIGHT)).build());
//		
//		y -= 42;
//		lp.putRow(pMargin - 35, y + 35, Cell.builder(CellStyle.of(MIDDLE_LEFT, null, null, BorderStyle.NO_BORDERS), 200)
//				.add(TextStyle.of(PDType1Font.TIMES_ROMAN, (float)8, BLACK), vec(
//						"Calle Prof. Martín Lagos, s/n",
//						"28040 Madrid España",
//						"Tel.: 91 330 30 00",
//						"www.madrid.org/hospitalclinicosancarlos")).build());
//
//		lp.putRow(pMargin, y + 30, Cell.builder(CellStyle.of(MIDDLE_CENTER, null, null, BorderStyle.NO_BORDERS), lp.pageWidth())
//				.add(TextStyle.of(PDType1Font.TIMES_ROMAN, (float)18, BLACK), vec(
//						"INFORME CLÍNICO DE CONSULTA EXTERNA",
//						"SERVICIO DE PSIQUIATRÍA")).build());
//		
//        y -= 40;
//        float col1X    = pMargin - 20;
//        float col2X    = pMargin + 220;
//        float colWidth = 200;
//        float headInterLine = 15;
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("NHC: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("numerohc").getAsString())).build());
//        lp.putCell(col2X, y, Cell.builder(titleHeadCell, 120).add(titleInfoText, vec("Dispositivo Asistencial: ")).build());
//        lp.putCell(col2X + 115, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("centro").getAsString())).build());
//        y -= headInterLine;
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("Nombre: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("nombre").getAsString() + " "
//        						+ jsonData.get("apellido1").getAsString() + " "
//        						+ jsonData.get("apellido2").getAsString())).build());
//        lp.putCell(col2X, y, Cell.builder(titleHeadCell, 120).add(titleInfoText, vec("Médico: ")).build());
//        lp.putCell(col2X + 115, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("nombreFacultativo").getAsString())).build());
//        y -= headInterLine;
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("Domicilio: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("domicilio").getAsString())).build());
//        lp.putCell(col2X, y, Cell.builder(titleHeadCell, 120).add(titleInfoText, vec("Fecha: ")).build());
//        lp.putCell(col2X + 115, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("fecha").getAsString())).build());
//        y -= headInterLine;
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("CP: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("codpostal").getAsString())).build());        
//        y -= headInterLine;
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("Fecha Nac.: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("fecnac").getAsString())).build());
//        y -= headInterLine;        
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("Sexo: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("sexo").getAsString())).build());
//        y -= headInterLine;        
//        lp.putCell(col1X, y, Cell.builder(titleHeadCell, 60).add(titleInfoText, vec("Edad: ")).build());
//        lp.putCell(col1X + 55, y, Cell.builder(titleTextCell, colWidth).add(titleInfoText,
//        				vec(jsonData.get("edad").getAsString())).build());
//        
//        y -= 40;
//        y  = lp.putRow(pMargin, y, Cell.builder(headingCell, 150)
//        		.add(headingText, vec("ANTECEDENTES")).build());
//		
//        String antecedentesString = jsonData.get("antecedentes").getAsString();
//        String[] antecedentesSplitted = antecedentesString.split("\n");
//        for (int i = 0; i < antecedentesSplitted.length; i++) {
//        	if (antecedentesSplitted[i].isEmpty()) antecedentesSplitted[i] = null;
//        	else antecedentesSplitted[i] = antecedentesSplitted[i].replaceAll("\\p{C}", "");
//        }
//		
//        y = lp.putRow(pMargin, y, Cell.builder(regularCell, lp.pageWidth() - (2 * pMargin))
////        		.add(regularText, vec(antecedentesSplitted)).build());
//		.add(regularText, vec("Hola", null, null, "Mundo")).build());
//        
//        if (y > 30) y -= 50;
//        else y -= 20;
//        y  = lp.putRow(pMargin, y, Cell.builder(headingCell, 200)
//        		.add(headingText, vec("ENFERMEDAD ACTUAL")).build());
//		
//        String enfermedadActualString = jsonData.get("historiaactual").getAsString();
//        String[] enfermedadActualSplitted = enfermedadActualString.split("\n");
//        for (int i = 0; i < enfermedadActualSplitted.length; i++) {
//        	enfermedadActualSplitted[i] = enfermedadActualSplitted[i].replaceAll("\\p{C}", "");
//        }
//		
//        y = lp.putRow(pMargin, y, Cell.builder(regularCell, lp.pageWidth() - (2 * pMargin))
//        		.add(regularText, vec(enfermedadActualSplitted)).build());
//        
//        if (y > 30) y -= 50;
//        else y -= 20;
//        y  = lp.putRow(pMargin, y, Cell.builder(headingCell, 250)
//        		.add(headingText, vec("EVOLUCIÓN Y COMENTARIOS")).build());
//		
//        String evolComentariosString = jsonData.get("evolucioncomentarios").getAsString();
//        String[] evolComentariosSplitted = evolComentariosString.split("\n");
//        for (int i = 0; i < evolComentariosSplitted.length; i++) {
//        	evolComentariosSplitted[i] = evolComentariosSplitted[i].replaceAll("\\p{C}", "");
//        }
//		
//        y = lp.putRow(pMargin, y, Cell.builder(regularCell, lp.pageWidth() - (2 * pMargin))
//        		.add(regularText, vec(evolComentariosSplitted)).build());
//        
//        float pp = lp.printAreaHeight();
//        if (y < -pp) y -= 50;        
//        else y -= 20;
//        y  = lp.putRow(pMargin, y, Cell.builder(headingCell, 320)
//        		.add(headingText, vec("TRATAMIENTOS - RECOMENDACIONES")).build());	
//        
//		String tratamientosRecomendacionesString = jsonData.get("tratamientosrecomendaciones").getAsString();
//		String[] tratamientosRecomendacionesSplitted = tratamientosRecomendacionesString.replaceAll("\\p{C}", "").split("\n");
//		
//		JsonArray tratamientos = jsonData.get("tratamientos").getAsJsonArray();
//		String[] tratamientosSplitted = new String[tratamientos.size()];
//		for (int i = 0; i < tratamientos.size(); i++) {
//			String result =
//					tratamientos.get(i).getAsJsonObject().get("TratName").getAsString() + " " + 
//					tratamientos.get(i).getAsJsonObject().get("ValorText").getAsString() + "    " +
//					"#Fecha Inicio: " + 
//					tratamientos.get(i).getAsJsonObject().get("FecIni").getAsString() + "    " +
//					"#Fecha Fin: " +
//					tratamientos.get(i).getAsJsonObject().get("FecFin").getAsString();
//			
//			tratamientosSplitted[i] = result;
//		}
//		
//		XyOffset tablePos = lp.tableBuilder(XyOffset.of(pMargin, y))
//			.addCellWidth(lp.pageWidth() - (2 * pMargin))
//			.textStyle(regularText)
//			.partBuilder().cellStyle(regularCell)
//			.rowBuilder()
//			.cellBuilder().align(MIDDLE_LEFT).add(regularText, vec(tratamientosRecomendacionesSplitted)).buildCell()
//			.buildRow()
//			.rowBuilder()
//			.cellBuilder().align(MIDDLE_LEFT).add(regularText, vec(tratamientosSplitted)).buildCell().buildRow()
//			.buildPart()
//			.buildTable();
//		
//		y = tablePos.y();
//		
//        if (y > 30) y -= 50;        
//        else y -= 20;
//        y  = lp.putRow(pMargin, y, Cell.builder(headingCell, 150)
//        				.add(headingText, vec("DIAGNOSTICOS")).build());
//        
//		JsonArray diagnosticos = jsonData.get("diagnosticos").getAsJsonArray();
//
//		/** NOMBRE DEL DIAGNOSTICO **/
//		String[] diagSplitted = new String[diagnosticos.size()];
//		for (int i = 0; i < diagnosticos.size(); i++) {			
//			
//			String item = diagnosticos.get(i).getAsJsonObject().get("DiagName").getAsString();
//
//			diagSplitted[i] = item;
//		}
//		
//        y = lp.putRow(pMargin, y, Cell.builder(regularCell, lp.pageWidth() - (2 * pMargin))
//        		.add(regularText, vec(diagSplitted)).build());
//		
//
//        lp.putRow(pMargin, y, Cell.builder(titleFootCellL, lp.pageWidth() - (2 * pMargin))
//        		.add(titleInfoText, vec("Responsable del informe: " + jsonData.get("nombreFacultativo").getAsString())).build());
//        lp.putRow(pMargin, y, Cell.builder(titleFootCellR, lp.pageWidth() - (2 * pMargin))
//        		.add(titleInfoText, vec("Madrid a: " + jsonData.get("fechahoy").getAsString())).build());
//			       
//        lp.commit();
//        
//        pageMgr.save(outStream);
//		
//		return outStream;
//	}
	
	// METODOS //
	public float getPosY(float posY, float sizeY) {
		return this.pageHeight - (sizeY + posY);
	}
	
	public PDPage obtenerPaginaActual() {
		return this.pages.get(this.pages.size() - 1);
	}
	
	public ByteArrayOutputStream generarInformePDF(JsonObject jsonData, ServletContext servletContext) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		this.document = new PDDocument();
		this.pages.add(new PDPage());
		PDPage currentPage = this.obtenerPaginaActual();
		this.document.addPage(currentPage);
		this.pageHeight = currentPage.getMediaBox().getHeight();
		
		this.documentFont     = PDType0Font.load(this.document, new File(servletContext.getRealPath("/pdf/times.ttf")));
		this.documentFontBold = PDType0Font.load(this.document, new File(servletContext.getRealPath("/pdf/timesbd.ttf")));
		
		this.dibujarLogoEnCabecera(servletContext);
		this.cursorPosY += 25;
		this.dibujarDatosPaciente(jsonData);
		this.dibujarDatosFacultativos(jsonData);
		this.dibujarAntecedentes(jsonData);
		this.dibujarEnfermedadActual(jsonData);
		this.dibujarEvolucionComentarios(jsonData);
		this.dibujarTratamientosRecomendaciones(jsonData);
		this.dibujarDiagnosticos(jsonData);
		this.dibujarFooter(jsonData);
		
		this.contentStream.close();
		this.document.save(outStream);
		this.document.close();
		
		return outStream;
	}
	
	
	public void dibujarLogoEnCabecera(ServletContext servletContext) throws IOException {
		
		String[] headData =
			{
				"Calle Prof. Martín Lagos, s/n",
		        "28040 Madrid España",
		        "Tel: 91 330 30 00",
		        "www.madrid.org/hospitalclinicosancarlos"
		    };
		
		String imgPath       = servletContext.getRealPath("/img/logoSmall.jpg");
		PDImageXObject pdImg = PDImageXObject.createFromFile(imgPath, document );
		
		PDPage currentPage = this.obtenerPaginaActual();
		
		this.contentStream = new PDPageContentStream(this.document, currentPage);
		this.contentStream.drawImage(pdImg, 0, this.getPosY(0, this.HCSC_LOGO_HEIGHT), 127, this.HCSC_LOGO_HEIGHT);

		this.contentStream.beginText();
		this.cursorPosY = this.HCSC_LOGO_HEIGHT;
		// Establece el tamaño de la fuente y el interlineado (espacio entre lineas)
		float fontSize    = 6.5f;
		float fontLeading = fontSize;
		this.contentStream.setFont(this.documentFont, fontSize);
		this.contentStream.setLeading(fontLeading);
		
		this.contentStream.newLineAtOffset(6, (int) this.getPosY(cursorPosY, 0));
		
		for (String item : headData) {
			this.contentStream.showText(item);
			this.contentStream.newLine();
			this.cursorPosY += fontLeading;
		}
		this.contentStream.endText();
		
		this.contentStream.setFont(this.documentFont, 14);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(210, this.getPosY(55, 0));
		this.contentStream.showText("SERVICIO DE PSIQUIATRÍA");
		this.contentStream.endText();
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(160, this.getPosY(40, 0));
		this.contentStream.showText("INFORME CLÍNICO DE CONSULTA EXTERNA");
		this.contentStream.endText();
	}

	private final int RECT_WIDTH  = 180;
	private final int RECT_HEIGHT = 10;
	
	private final int LEFT_COLUMN_X  = 75;
	private final int RIGHT_COLUMN_X = 380;
	
	private final int MARGIN_LEFT  = 40;
	
	private void dibujarDatosPaciente(JsonObject jsonData) throws IOException {
		
		String[] datosPaciente =
			{
				"NHC:", "Nombre:", "Domicilio:", "CP:", "Fecha Nac.:", "Sexo:", "Edad:"	
			};
		
		int savedCursorPosY = this.cursorPosY;
		
		float fontSize    = 8;
		float fontLeading = 12;
		this.contentStream.setFont(this.documentFont, fontSize);
		this.contentStream.setLeading(fontLeading);
		
		for (String item : datosPaciente) {
			float stringWidth = (this.documentFont.getStringWidth(item) / 1000.0f) * 8;
			
			this.contentStream.beginText();
			this.contentStream.newLineAtOffset(LEFT_COLUMN_X - stringWidth, (int)this.getPosY((float)this.cursorPosY, 0));
			
			this.contentStream.showText(item);
			
			this.cursorPosY += fontLeading;
			
			this.contentStream.endText();
		}
		
		int rectCellLeading = 2;
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		for (int i = 0; i < 7; i++) {
			this.contentStream.addRect(LEFT_COLUMN_X + 2, this.getPosY((float)(savedCursorPosY) + rectCellLeading, 0), RECT_WIDTH, RECT_HEIGHT);
			this.contentStream.fill();
			rectCellLeading += fontLeading;
		}
		
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(LEFT_COLUMN_X + 4, this.getPosY(savedCursorPosY, 0));
		this.contentStream.showText(jsonData.get("numerohc").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("nombre").getAsString() + " "
					+ jsonData.get("apellido1").getAsString() + " " + jsonData.get("apellido2").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("domicilio").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("codpostal").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("fecnac").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("sexo").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("edad").toString());
		this.contentStream.endText();
		
		this.cursorPosY = savedCursorPosY;
	}
	
	private void dibujarDatosFacultativos(JsonObject jsonData) throws IOException {
		String[] datosFacultativos =
			{
				"Dispositivo Asistencial:", "Médico:", "Fecha:"
			};
		
		int savedCursorPosY = this.cursorPosY;
		
		float fontSize    = 8;
		float fontLeading = 12;
		this.contentStream.setFont(this.documentFont, fontSize);
		this.contentStream.setLeading(fontLeading);
		
		for (String item : datosFacultativos) {
			float stringWidth = (this.documentFont.getStringWidth(item) / 1000.0f) * 8;
			
			this.contentStream.beginText();
			this.contentStream.newLineAtOffset(RIGHT_COLUMN_X - stringWidth, this.getPosY((float)this.cursorPosY, 0));
			
			this.contentStream.showText(item);
			this.contentStream.newLine();
			
			this.cursorPosY += fontLeading;
			
			this.contentStream.endText();
		}
		
		int rectCellLeading = 2;
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		for (int i = 0; i < 3; i++) {
			this.contentStream.addRect(RIGHT_COLUMN_X + 2, this.getPosY((float)(savedCursorPosY) + rectCellLeading, 0), RECT_WIDTH, RECT_HEIGHT);
			this.contentStream.fill();
			rectCellLeading += fontLeading;
		}
		
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(RIGHT_COLUMN_X + 4, this.getPosY((float)savedCursorPosY, 0));
		this.contentStream.showText(jsonData.get("centro").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("nombreFacultativo").getAsString());
		this.contentStream.newLine();
		this.contentStream.showText(jsonData.get("fecha").getAsString());
		this.contentStream.endText();	
		
		this.cursorPosY = savedCursorPosY;
	}

	private void dibujarAntecedentes(JsonObject jsonData) throws IOException {
		this.cursorPosY += 100;
		
		String antecedentesString = jsonData.get("antecedentes").getAsString();
		
		String[] antecedentesSplitted = antecedentesString.split("\n");
		
		ArrayList<String> antecedentesLines = new ArrayList<String>();
		
		for (String item : antecedentesSplitted) {
			List<String> currentLine = this.getLines(item, 516, documentFont, CONTENT_FONT_SIZE);
			antecedentesLines.addAll(currentLine);
		}
		
		int LINE_HEIGHT = CONTENT_FONT_SIZE;
		this.fontLeading = LINE_HEIGHT;
		this.contentStream.setLeading(LINE_HEIGHT);
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		this.contentStream.addRect(MARGIN_LEFT - 2.5f, this.getPosY(this.cursorPosY, 1), 5 + documentFontBold.getStringWidth("ANTECEDENTES") / 1000 * CONTENT_FONT_SIZE, 10);
		this.contentStream.fill();
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(MARGIN_LEFT, this.getPosY(this.cursorPosY, 0));
		this.contentStream.setFont(documentFontBold, CONTENT_FONT_SIZE);
		this.contentStream.showText("ANTECEDENTES");
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		this.contentStream.endText();
		
		int rectY    = (int)this.getPosY(this.cursorPosY, 0) - CONTENT_FONT_SIZE / 2;
		float height = ((antecedentesLines.size()) * LINE_HEIGHT) + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2; 
		this.contentStream.addRect(MARGIN_LEFT - 2, rectY, 522, -height);
		this.contentStream.stroke();

		this.contentStream.setNonStrokingColor(Color.BLACK);
		
		this.cursorPosY = (int)this.drawText((float)(MARGIN_LEFT + 2), this.cursorPosY + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2,
											CONTENT_FONT_SIZE, false, antecedentesLines, true);
		
		this.cursorPosY += 35;
	}
	
	private void dibujarEnfermedadActual(JsonObject jsonData) throws IOException
	{
		String enfermedadActualString = jsonData.get("historiaactual").getAsString();
		String[] enfermedadActualSplitted = enfermedadActualString.split("\n");
		
		ArrayList<String> enfermedadActualLines = new ArrayList<String>();
		
		for (String item : enfermedadActualSplitted) {
			List<String> currentLine = this.getLines(item, 516, documentFont, CONTENT_FONT_SIZE);
			enfermedadActualLines.addAll(currentLine);
		}
		
		float height = ((enfermedadActualLines.size()) * this.fontLeading) + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2; 
		
		if (this.cursorPosY >= this.pageHeight || (this.cursorPosY + height + 10 >= this.pageHeight)) {
			PDPage anotherPage = new PDPage();
			this.pages.add(anotherPage);
			//anotherPage.setUserUnit(72);
			this.document.addPage(anotherPage);
			this.contentStream.close();
			this.contentStream = new PDPageContentStream(this.document, anotherPage);
			this.cursorPosY = 20;
		}

		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		this.contentStream.addRect(MARGIN_LEFT - 2.5f, this.getPosY(this.cursorPosY, 1), 5 + documentFontBold.getStringWidth("ENFERMEDAD ACTUAL") / 1000 * CONTENT_FONT_SIZE, 10);
		this.contentStream.fill();
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(MARGIN_LEFT, this.getPosY(this.cursorPosY, 0));
		this.contentStream.setFont(documentFontBold, CONTENT_FONT_SIZE);
		this.contentStream.showText("ENFERMEDAD ACTUAL");
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		this.contentStream.endText();
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		int rectY    = (int)this.getPosY(this.cursorPosY, 0) - CONTENT_FONT_SIZE / 2;
		this.contentStream.addRect(MARGIN_LEFT - 2, rectY, 522, -height);
		this.contentStream.stroke();

		this.contentStream.setNonStrokingColor(Color.BLACK);
		
		this.cursorPosY = (int)this.drawText((float)(MARGIN_LEFT + 2), this.cursorPosY + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2,
											CONTENT_FONT_SIZE, false, enfermedadActualLines, true);
//		if (rectY - height < 0) {
//			this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight + 10, 522, rectY - height - 40);			
//			this.contentStream.stroke();
//		}
		
		this.cursorPosY += 35;		
	}
	
	private void dibujarEvolucionComentarios(JsonObject jsonData) throws IOException {
		String evolucionComentariosString = jsonData.get("evolucioncomentarios").getAsString();
		String[] evolucionComentariosSplitted = evolucionComentariosString.split("\n");
		
		ArrayList<String> evolucionComentariosLines = new ArrayList<String>();
		
		for (String item : evolucionComentariosSplitted) {
			List<String> currentLine = this.getLines(item, 516, documentFont, CONTENT_FONT_SIZE);
			evolucionComentariosLines.addAll(currentLine);
		}
		
		float height = ((evolucionComentariosLines.size()) * this.fontLeading) + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2;
		
		if (this.cursorPosY >= this.pageHeight || (this.cursorPosY + height + 10 >= this.pageHeight)) {
			PDPage anotherPage = new PDPage();
			this.pages.add(anotherPage);
			//anotherPage.setUserUnit(72);
			this.document.addPage(anotherPage);
			this.contentStream.close();
			this.contentStream = new PDPageContentStream(this.document, anotherPage);
			this.cursorPosY = 20;
		}
		
		
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		this.contentStream.addRect(MARGIN_LEFT - 2.5f, this.getPosY(this.cursorPosY, 1), 5 + documentFontBold.getStringWidth("EVOLUCIÓN Y COMENTARIOS") / 1000 * CONTENT_FONT_SIZE, 10);
		this.contentStream.fill();
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(MARGIN_LEFT, this.getPosY(this.cursorPosY, 0));
		this.contentStream.setFont(documentFontBold, CONTENT_FONT_SIZE);
		this.contentStream.showText("EVOLUCIÓN Y COMENTARIOS");
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		this.contentStream.endText();
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		float rectY  = this.getPosY(this.cursorPosY, 0) - CONTENT_FONT_SIZE / 2;
		this.contentStream.addRect(MARGIN_LEFT - 2, rectY, 522, -height);
		this.contentStream.stroke();

		this.contentStream.setNonStrokingColor(Color.BLACK);
		
		this.cursorPosY = (int)this.drawText((float)(MARGIN_LEFT + 2), this.cursorPosY + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2,
									CONTENT_FONT_SIZE, false, evolucionComentariosLines, true);
		
//		if (rectY - height < 0) {
//			this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight + 10, 522, rectY - height - 40);			
//			this.contentStream.stroke();
//			height -= this.pageHeight;
//		}

		this.cursorPosY += 35;		
	}
	
	private void dibujarTratamientosRecomendaciones(JsonObject jsonData) throws IOException {
		String tratamientosRecomendacionesString = jsonData.get("tratamientosrecomendaciones").getAsString();
		String[] tratamientosRecomendacionesSplitted = tratamientosRecomendacionesString.split("\n");
		
		ArrayList<String> tratamientosRecomendacionesLines = new ArrayList<String>();
		
		for (String item : tratamientosRecomendacionesSplitted) {
			List<String> currentLine = this.getLines(item, 516, documentFont, CONTENT_FONT_SIZE);
			tratamientosRecomendacionesLines.addAll(currentLine);
		}
		
		JsonArray tratamientos = jsonData.get("tratamientos").getAsJsonArray();

//		if (this.cursorPosY >= this.pageHeight) {
//			PDPage anotherPage = new PDPage();
//			this.pages.add(anotherPage);
//			//anotherPage.setUserUnit(72);
//			this.document.addPage(anotherPage);
//			this.contentStream.close();
//			this.contentStream = new PDPageContentStream(this.document, anotherPage);
//			this.cursorPosY = 30;
//		}
		
		// El +15 es por el espacio que se añade para separar los 2 contenidos de los tratamientos //
		float height = ((tratamientosRecomendacionesLines.size() + tratamientos.size()) * this.fontLeading) + CONTENT_FONT_SIZE + 15;
		
		// El +10 es por el alto de la cabecera del recuadro
		if ((this.cursorPosY >= this.pageHeight) || (this.cursorPosY + height + 10 >= this.pageHeight)) {
			PDPage anotherPage = new PDPage();
			this.pages.add(anotherPage);
			//anotherPage.setUserUnit(72);
			this.document.addPage(anotherPage);
			this.contentStream.close();
			this.contentStream = new PDPageContentStream(this.document, anotherPage);
			this.cursorPosY = 30;
		}

		float rectY  = this.getPosY(this.cursorPosY, 0) - CONTENT_FONT_SIZE / 2;
		
		for(int i = 0; i < tratamientos.size() + 2; i++) {
			tratamientosRecomendacionesLines.add("");
		}

		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		this.contentStream.addRect(MARGIN_LEFT - 2.5f, this.getPosY(this.cursorPosY, 1), 5 + documentFontBold.getStringWidth("TRATAMIENTOS - RECOMENDACIONES") / 1000 * CONTENT_FONT_SIZE, 10);
		this.contentStream.fill();
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(MARGIN_LEFT, this.getPosY(this.cursorPosY, 0));
		this.contentStream.setFont(documentFontBold, CONTENT_FONT_SIZE);
		this.contentStream.showText("TRATAMIENTOS - RECOMENDACIONES");
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		this.contentStream.endText();
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
										
		this.contentStream.addRect(MARGIN_LEFT - 2, rectY, 522, -height);
		this.contentStream.stroke();

		this.contentStream.setNonStrokingColor(Color.BLACK);
		
		
		this.cursorPosY = (int)this.drawText((float)(MARGIN_LEFT + 2), this.cursorPosY + CONTENT_FONT_SIZE + CONTENT_FONT_SIZE / 2,
											CONTENT_FONT_SIZE, false, tratamientosRecomendacionesLines, true);
		this.cursorPosY -= (tratamientos.size() + 1) * fontLeading;//15;
		
		/** NOMBRE DEL TRATAMIENTO **/
		this.contentStream.setNonStrokingColor(Color.RED);
		ArrayList<String> datas = new ArrayList<String>();;
		for (int index = 0; index < tratamientos.size(); index++) {
			datas.add(tratamientos.get(index).getAsJsonObject().get("TratName").getAsString());
		}
		this.drawText((float)(MARGIN_LEFT + 90), (float)this.cursorPosY,
								CONTENT_FONT_SIZE, true, datas, false);

		/** NUMERO DE SESIONES **/
		this.contentStream.setNonStrokingColor(Color.BLACK);
		datas.clear();
		for (int index = 0; index < tratamientos.size(); index++) {
			datas.add(tratamientos.get(index).getAsJsonObject().get("ValorText").getAsString());
		}
		this.drawText((float)(MARGIN_LEFT + 130), (float)this.cursorPosY, CONTENT_FONT_SIZE, false, datas, false);

		/** FECHAS LABELS **/
		this.contentStream.setNonStrokingColor(Color.RED);
		for (int index = 0; index < tratamientos.size(); index++) {
			this.contentStream.beginText();
			this.contentStream.newLineAtOffset(MARGIN_LEFT + 210, this.getPosY(this.cursorPosY, 0) - (index * this.fontLeading));
			this.contentStream.showText("#Fecha Inicio:                         #Fecha Fin:");
			this.contentStream.newLine();
			this.contentStream.endText();
		}
		
		/** FECHAS INICIO **/
		this.contentStream.setNonStrokingColor(Color.BLACK);
		datas.clear();
		for (int index = 0; index < tratamientos.size(); index++) {
			datas.add(tratamientos.get(index).getAsJsonObject().get("FecIni").getAsString());
		}
		this.drawText((float)(MARGIN_LEFT + 282), (float)this.cursorPosY, CONTENT_FONT_SIZE, false, datas, false);

		/** FECHAS FIN **/
		datas.clear();
		for (int index = 0; index < tratamientos.size(); index++) {
			datas.add(tratamientos.get(index).getAsJsonObject().get("FecFin").getAsString());
		}
		this.cursorPosY = (int)this.drawText((float)(MARGIN_LEFT + 414), (float)this.cursorPosY,
												CONTENT_FONT_SIZE, false, datas, false);

//		if (rectY - height < 0) {
//			this.contentStream.setStrokingColor(new Color(255, 255, 0));
//			int altura = (int)(height - (((int)(height / pageHeight)) * pageHeight));
//			this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight-altura, 522, altura);			
//			this.contentStream.stroke();
//		}
//		this.contentStream.setStrokingColor(new Color(0, 0, 0));

		this.cursorPosY += 35;		
	}

	private void dibujarDiagnosticos(JsonObject jsonData) throws IOException {
		JsonArray diagnosticos = jsonData.get("diagnosticos").getAsJsonArray();

		/** NOMBRE DEL DIAGNOSTICO **/
		ArrayList<String> datasDiag = new ArrayList<String>();
		for (int index = 0; index < diagnosticos.size(); index++) {			
			String item = diagnosticos.get(index).getAsJsonObject().get("DiagName").getAsString();
			List<String> currentLine = this.getLines(item, 516, documentFont, CONTENT_FONT_SIZE);
			datasDiag.addAll(currentLine);
		}
		
		if (this.cursorPosY > this.pageHeight) {
			PDPage anotherPage = new PDPage();
			this.pages.add(anotherPage);
			//anotherPage.setUserUnit(72);
			this.document.addPage(anotherPage);
			this.contentStream.close();
			this.contentStream = new PDPageContentStream(this.document, anotherPage);
			this.cursorPosY = 20;
		}
		
		float height = (datasDiag.size() * this.fontLeading) + CONTENT_FONT_SIZE;

		float rectY  = this.getPosY(this.cursorPosY, 0) - CONTENT_FONT_SIZE / 2;		

		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
		this.contentStream.addRect(MARGIN_LEFT - 2.5f, this.getPosY(this.cursorPosY, 1), 5 + documentFontBold.getStringWidth("DIAGNÓSTICOS") / 1000 * CONTENT_FONT_SIZE, 10);
		this.contentStream.fill();
		this.contentStream.setNonStrokingColor(Color.BLACK);
		this.contentStream.beginText();
		this.contentStream.newLineAtOffset(MARGIN_LEFT, this.getPosY(this.cursorPosY, 0));
		this.contentStream.setFont(documentFontBold, CONTENT_FONT_SIZE);
		this.contentStream.showText("DIAGNÓSTICOS");
		this.contentStream.setFont(documentFont, CONTENT_FONT_SIZE);
		this.contentStream.endText();
		
		this.contentStream.setNonStrokingColor(new Color(220, 220, 220));
										
		this.contentStream.addRect(MARGIN_LEFT - 2, rectY, 522, -height);
		this.contentStream.stroke();

		this.contentStream.setNonStrokingColor(Color.BLACK);

		this.cursorPosY += 15;
		
		this.cursorPosY = (int)this.drawText((float)(MARGIN_LEFT + 2),
				this.cursorPosY + CONTENT_FONT_SIZE / 2, CONTENT_FONT_SIZE, false, datasDiag, true);


//		if (rectY - height < 0) {
//			this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight + 10, 522, rectY - height - 40);			
//			this.contentStream.stroke();
//		}
	}

	private void dibujarFooter(JsonObject jsonData) throws IOException {
		this.cursorPosY += 10;
		
		this.drawTextAt(MARGIN_LEFT - 3 + 2, this.cursorPosY, 8, "Responsable del informe: " + jsonData.get("nombreFacultativo").getAsString());
		this.drawTextAt(MARGIN_LEFT + 450 - 2, this.cursorPosY, 8, "Madrid a: " + jsonData.get("fechahoy").getAsString());				
	}
	
	private float drawText(float xPos, float yPos, float fontSize, boolean align, ArrayList<String> text, boolean withRect) throws IOException {
		int lineCount = 0;
		for (String item : text) {
			lineCount++;
			if (yPos + fontLeading > this.pageHeight) {
				PDPage anotherPage = new PDPage();
				this.pages.add(anotherPage);
				//anotherPage.setUserUnit(72);
				this.document.addPage(anotherPage);
				this.contentStream.close();
				this.contentStream = new PDPageContentStream(this.document, anotherPage);
				
				yPos = 30;
				
				if (withRect) {
					int altura = (int)((text.size() - lineCount) * fontSize + 20 + fontLeading);
					if (altura > this.pageHeight) altura = (int)this.pageHeight;
					this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight-altura, 522, altura);
					this.contentStream.stroke();
				}
//				else {
//					int altura = (int)((text.size() - lineCount) * fontSize + 20 + fontLeading);
//					if (altura > this.pageHeight) altura = (int)this.pageHeight;
//					this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight-altura, 522, altura);
//					this.contentStream.stroke();					
//					this.contentStream.setNonStrokingColor(new Color(255, 0, 0));
//					this.contentStream.addRect(MARGIN_LEFT - 2, this.pageHeight-altura, 522, 10);
//					this.contentStream.fill();					
//					this.contentStream.setNonStrokingColor(new Color(0, 0, 0));
//				}
			}
		
			this.contentStream.setFont(documentFont, fontSize);
			this.contentStream.setLeading(this.fontLeading);			
			
			float width = 0;
			if (align) width = (documentFont.getStringWidth(item) / 1000.0f) * 8 ;
			this.contentStream.beginText();
			this.contentStream.newLineAtOffset(xPos - width, this.getPosY(yPos, 0));
			this.contentStream.showText(item);
//			this.contentStream.newLine();
			this.contentStream.endText();
			yPos += this.fontLeading;
		}
		
		return yPos;
	}
	
	private float drawTextAt(float xPos, float yPos, float fontSize, String text) throws IOException {
		
		if (yPos < fontLeading + 5) {
			PDPage anotherPage = new PDPage();
			this.pages.add(anotherPage);
			//anotherPage.setUserUnit(72);
			this.document.addPage(anotherPage);
			this.contentStream.close();
			this.contentStream = new PDPageContentStream(this.document, anotherPage);
			
			yPos = 20;
		}

		this.contentStream.setFont(documentFont, fontSize);
		this.contentStream.setLeading(this.fontLeading);			
		
		this.contentStream.beginText();
		
		this.contentStream.newLineAtOffset(xPos, this.getPosY(yPos, 0));
		this.contentStream.showText(text);
		
		this.contentStream.endText();
		
		return yPos + this.fontLeading;
	}	
		
	private List<String> getLines(String text, int paragraphWidth, PDFont font, int fontSize) throws IOException {
		List<String> result = new ArrayList<String>();
		
		String[] split = text.split("(?<=\\W)");
        int[] possibleWrapPoints = new int[split.length];
        possibleWrapPoints[0] = split[0].length();
        for ( int i = 1 ; i < split.length ; i++ ) {
            possibleWrapPoints[i] = possibleWrapPoints[i-1] + split[i].length();
        }

        int start = 0;
        int end   = 0;
        for ( int i : possibleWrapPoints ) {
        	String cleanText = text.substring(start,i);
        	cleanText = cleanText.replaceAll("\\p{C}", "");//replace("\u0080", "");
            float width = font.getStringWidth(cleanText) / 1000 * fontSize;
            if ( start < end && width > paragraphWidth ) {
                result.add(text.substring(start,end).replaceAll("\\p{C}", ""));//replace("\u0080", ""));
                start = end;
            }
            end = i;
        }
        // Last piece of text
        result.add(text.substring(start).replaceAll("\\p{C}", ""));//replace("\u0080", ""));

        return result;
	}
}
