package com.example.puzzles.tools;

import java.math.BigInteger;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

public class BookDocumentWriter {

    private static final int BOOK_TITLE_FONT_SIZE = 28;
    protected static final String FONT_FAMILY = "Arial";

    public XWPFDocument createDocument() throws Exception {
        XWPFDocument doc = new XWPFDocument();        
        setPageFormat(doc);
        addTitlePage(doc);
        return doc;
    }

    private void setPageFormat(XWPFDocument doc) {
        // Set page size to 8.5 x 11 inches (US Letter)
        CTBody body = doc.getDocument().getBody();
        if (body.isSetSectPr()) {
            CTSectPr sectPr = body.getSectPr();
            if (sectPr.isSetPgSz()) {
                sectPr.getPgSz().setW(BigInteger.valueOf(12240)); // 8.5 * 1440
                sectPr.getPgSz().setH(BigInteger.valueOf(15840)); // 11 * 1440
            } else {
                CTPageSz pageSz = sectPr.addNewPgSz();
                pageSz.setW(BigInteger.valueOf(12240));
                pageSz.setH(BigInteger.valueOf(15840));
            }
        } else {
            CTSectPr sectPr = body.addNewSectPr();
            CTPageSz pageSz = sectPr.addNewPgSz();
            pageSz.setW(BigInteger.valueOf(12240));
            pageSz.setH(BigInteger.valueOf(15840));
        }
    }

    private void addTitlePage(XWPFDocument doc) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setFontFamily(FONT_FAMILY);
        run.setBold(true);
        run.setFontSize(BOOK_TITLE_FONT_SIZE);
        run.setText("DON’T PANIC");
        run.addBreak();
        run.addBreak();
        XWPFRun subtitle = title.createRun();
        subtitle.setFontFamily(FONT_FAMILY);
        subtitle.setFontSize(20);
        subtitle.setText(PuzzleProperties.getProperty("label.bookTitle"));
        subtitle.addBreak();
        subtitle.addBreak();
        XWPFRun author = title.createRun();
        author.setFontFamily(FONT_FAMILY);
        author.setFontSize(16);
        author.setText(PuzzleProperties.getProperty("label.authorName"));
        author.addBreak();
        author.addBreak();
        XWPFRun tagline = title.createRun();
        tagline.setFontFamily(FONT_FAMILY);
        tagline.setFontSize(14);
        tagline.setText(PuzzleProperties.getProperty("label.bookTagLine"));
        tagline.addBreak();
        tagline.addBreak();
        XWPFRun series = title.createRun();
        series.setFontFamily(FONT_FAMILY);
        series.setFontSize(12);
        series.setText(PuzzleProperties.getProperty("label.bookSeries"));
        series.addBreak(BreakType.PAGE);
    }
   
    public void endDocument(XWPFDocument doc) throws Exception {
        addPageNumbers(doc); // Add page numbers in the footer
    }

    private void addPageNumbers(XWPFDocument doc) {
        XWPFHeaderFooterPolicy footerPolicy = doc.createHeaderFooterPolicy();
        XWPFFooter footer = footerPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph para = footer.createParagraph();
        para.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = para.createRun();
        run.setFontFamily(FONT_FAMILY);
        run.setFontSize(10);
        run.setText("Page ");
        run.getCTR().addNewFldChar().setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);
        XWPFRun pageNumRun = para.createRun();
        pageNumRun.getCTR().addNewInstrText().setStringValue("PAGE");
        XWPFRun endRun = para.createRun();
        endRun.getCTR().addNewFldChar().setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);

        // Set different first page for the section so the footer (page number) starts on page 2
        CTSectPr sectPr = doc.getDocument().getBody().getSectPr();
        if (sectPr == null) {
            sectPr = doc.getDocument().getBody().addNewSectPr();
        }
        //CTPageMar pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        sectPr.addNewTitlePg(); // This enables 'Different First Page'
    }
}

