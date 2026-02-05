package ec.cajamarca.sic.wk.test;

import ec.cajamarca.sic.wk.wrapper.Pdf;
import ec.cajamarca.sic.wk.page.PageType;
import ec.cajamarca.sic.wk.params.Param;
import java.io.IOException;

public class Test {

    public static void main(String... Args) {
        try {
            Pdf pdf = new Pdf();
            pdf.addPage("<!DOCTYPE html>"+"<html>"+"<head>"
                    + "<title>Test Ivan</title>"
                    + "</head>"+ "<body>"
                    + "    <p>test ivan</p>"
                    + "</body>"+ "</html>", PageType.htmlAsString);
//            String documentHTML = "e://index.html";
//            pdf.addPage("file:///" + documentHTML, PageType.file);
            pdf.saveAs("/opt/examplepdf/output.pdf");
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
