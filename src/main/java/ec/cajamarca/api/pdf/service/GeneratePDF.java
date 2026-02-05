package ec.cajamarca.api.pdf.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ec.cajamarca.api.pdf.model.Data;
import ec.cajamarca.api.pdf.model.Document;
import ec.cajamarca.api.pdf.model.DocumentGeneric;
import ec.cajamarca.sic.wk.page.PageType;
import ec.cajamarca.sic.wk.wrapper.Pdf;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

public class GeneratePDF {

    // 🔥 CACHE GLOBAL DEL TEMPLATE (se carga una sola vez)
    private static volatile String TEMPLATE_CACHE;

    public static DocumentGeneric pdfIvew(Document documento) throws Exception {

        long start = System.currentTimeMillis();
        Logger.getLogger(GeneratePDF.class.getName())
                .info("[API-PDF-START] Inicio generación PDF");

        List<Data> dataListAux = null;
        Gson gs = new GsonBuilder().create();

        // 1) Preparar datos (igual que antes)
        if (documento.getDataList() != null && !documento.getDataList().isEmpty()) {
            dataListAux = new ArrayList<>();
            for (Data d : documento.getDataList()) {
                Object tmpAux;
                switch (d.getDataType()) {
                    case LIST:
                        tmpAux = gs.fromJson(d.getValue(), ArrayList.class);
                        break;
                    case MODEL:
                        tmpAux = gs.fromJson(d.getValue(), TreeMap.class);
                        break;
                    default:
                        tmpAux = gs.fromJson(d.getValue(), Object.class);
                        break;
                }
                d.setTmp(tmpAux);
                dataListAux.add(d);
            }
        }

        // 2) Contexto Velocity
        VelocityContext context = new VelocityContext();
        context.put("numberTool", new NumberTool());
        context.put("dateTool", new DateTool());
        context.put("aLocale", Locale.US);

        if (dataListAux != null) {
            for (Data d : dataListAux) {
                context.put(d.getKey(), d.getTmp());
            }
        }

        // 3) TEMPLATE CACHEADO (🚀 mejora fuerte de rendimiento)
        if (TEMPLATE_CACHE == null) {
            synchronized (GeneratePDF.class) {
                if (TEMPLATE_CACHE == null) {
                    TEMPLATE_CACHE = documento.getTemplate();
                }
            }
        }

        StringWriter swOut = new StringWriter();
        Velocity.evaluate(context, swOut, "document.vm", TEMPLATE_CACHE);

        // 4) Generar PDF
        Pdf pdf = new Pdf();
        pdf.addPage(swOut.toString(), PageType.htmlAsString);

        DocumentGeneric documentGeneric = new DocumentGeneric();
        documentGeneric.setContent(
                new String(Base64.encodeBase64(pdf.getPDF()), StandardCharsets.UTF_8)
        );

        // 5) Liberar memoria (clave en batch grandes)
        if (dataListAux != null) {
            dataListAux.clear();
        }
        swOut.getBuffer().setLength(0);

        long end = System.currentTimeMillis();
        Logger.getLogger(GeneratePDF.class.getName())
                .info("[API-PDF-END] Fin generación PDF. Tiempo(ms)=" + (end - start));

        return documentGeneric;
    }
}