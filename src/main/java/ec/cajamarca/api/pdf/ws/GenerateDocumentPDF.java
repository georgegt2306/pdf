package ec.cajamarca.api.pdf.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ec.cajamarca.api.pdf.model.Document;
import ec.cajamarca.api.pdf.service.PdfExecutorPool;
import java.util.logging.Logger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Stateless
@Produces({MediaType.APPLICATION_JSON})
@Path("/")
public class GenerateDocumentPDF {

    private static final Logger LOGGER = Logger.getLogger(GenerateDocumentPDF.class.getName());

    @Inject
    private PdfExecutorPool pdfExecutorPool;

    @POST
    @Path("convert/html/to/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void test(
            String body,
            @Suspended AsyncResponse asyncResponse) {

        // ⏱ timeout duro del request HTTP
        asyncResponse.setTimeout(3, java.util.concurrent.TimeUnit.MINUTES);
        asyncResponse.setTimeoutHandler(ar -> {
            LOGGER.severe("Timeout generando PDF");
            ar.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Timeout generando PDF")
                    .build());
        });

        try {
            Gson gson = new GsonBuilder().create();
            Document entidad = gson.fromJson(body, Document.class);

            pdfExecutorPool.generarPDFAsync(entidad)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            LOGGER.severe("Error generando PDF: " + error.getMessage());
                            asyncResponse.resume(
                                    Response.serverError()
                                            .entity("Error generando PDF: " + error.getMessage())
                                            .build()
                            );
                        } else {
                            asyncResponse.resume(result);
                        }
                    });

        } catch (Exception e) {
            LOGGER.severe("Error parsing request: " + e.getMessage());
            asyncResponse.resume(
                    Response.serverError().entity("Error procesando solicitud").build()
            );
        }
    }

}
