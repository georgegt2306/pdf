package ec.cajamarca.api.pdf.service;

import ec.cajamarca.api.pdf.model.Document;
import ec.cajamarca.api.pdf.model.DocumentGeneric;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import java.util.concurrent.CompletableFuture;

@Singleton
@Startup
public class PdfExecutorPool {

    @Resource(name = "concurrent/PdfExecutor")
    private ManagedExecutorService executor;

    public CompletableFuture<DocumentGeneric> generarPDFAsync(Document doc) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return GeneratePDF.pdfIvew(doc);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
}