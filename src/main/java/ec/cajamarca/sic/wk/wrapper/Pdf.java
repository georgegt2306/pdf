package ec.cajamarca.sic.wk.wrapper;

import ec.cajamarca.sic.wk.configurations.WrapperConfig;
import ec.cajamarca.sic.wk.configurations.WrapperConfigBuilder;
import ec.cajamarca.sic.wk.page.Page;
import ec.cajamarca.sic.wk.page.PageType;
import ec.cajamarca.sic.wk.params.Param;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

public class Pdf implements PdfService {

    private final static Logger LOGGER = Logger.getLogger(Pdf.class.getName());
    private static final String STDINOUT = "-";

    /**
     * Ruta a utilizar por defecto en caso de no encontrar el WKHTML configurado
     * en el sistema
     */
    private static final String WKHTMLTOPDF_PATH_DEFAULT = "/usr/local/bin/wkhtmltopdf";

    private WrapperConfig wrapperConfig;

    private List<Param> params;

    private List<Page> pages;

    private boolean hasToc = false;

    public Pdf(WrapperConfig wrapperConfig) {
        this.wrapperConfig = wrapperConfig;
        this.params = new ArrayList<Param>();
        this.pages = new ArrayList<Page>();
    }

    public Pdf() {
        this(new WrapperConfigBuilder().build());
    }

    public void addPage(String source, PageType type) {
        this.pages.add(new Page(source, type));
    }

    public void addToc() {
        this.hasToc = true;
    }

    public void addParam(Param param) {
        params.add(param);
    }

    public void addParam(Param... params) {
        for (Param param : params) {
            addParam(param);
        }
    }

    public void saveAs(String path)
            throws IOException, InterruptedException {
        saveAs(path, getPDF());
    }

    private File saveAs(String path, byte[] document)
            throws IOException {
        File file = new File(path);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(document);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        return file;
    }

    public byte[] getPDF()
            throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(getCommandAsArray());

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                OutputStream stdInStream = process.getOutputStream();
                stdInStream.write(page.getSource().getBytes("UTF-8"));
                stdInStream.flush();
                stdInStream.close();
            }
        }

        StreamEater outputStreamEater = new StreamEater(process.getInputStream());
        outputStreamEater.start();

        StreamEater errorStreamEater = new StreamEater(process.getErrorStream());
        errorStreamEater.start();

        outputStreamEater.join();
        errorStreamEater.join();
        process.waitFor();

        if (process.exitValue() != 0) {
            throw new RuntimeException("Process ("
                    + getCommand() + ") exited with status code " + process.exitValue() + ":\n" + new String(errorStreamEater.getBytes()));
        }

        if (outputStreamEater.getError() != null) {
            throw outputStreamEater.getError();
        }

        if (errorStreamEater.getError() != null) {
            throw errorStreamEater.getError();
        }

        return outputStreamEater.getBytes();
    }

    private String[] getCommandAsArray() {
        List<String> commandLine = new ArrayList<String>();

        // Verificamos si esta configurado el WKHTML
        if (wrapperConfig.getWkhtmltopdfCommand() == null) {
            // Obtenemos la ruta sea de las variables configuradas por Virgo y/o el path configurado por defecto
            wrapperConfig.setWkhtmltopdfCommand(System.getProperty("WKHTMLTOPDF_PATH", WKHTMLTOPDF_PATH_DEFAULT));
            LOGGER.log(
                    Level.WARNING,
                    "WKHTMLTOPDF_PATH no se encuentra configurado como variable de entorno o sistema. Por defecto se utilizara la ruta: "
                    + wrapperConfig.getWkhtmltopdfCommand());
        }

        commandLine.add(wrapperConfig.getWkhtmltopdfCommand());

        if (hasToc) {
            commandLine.add("toc");
        }

        for (Param p : params) {
            commandLine.add(p.getKey());

            String value = p.getValue();

            if (value != null) {
                commandLine.add(p.getValue());
            }
        }

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                commandLine.add(STDINOUT);
            } else {
                commandLine.add(page.getSource());
            }

        }
        commandLine.add(STDINOUT);
        String[] comando = commandLine.toArray(new String[commandLine.size()]);
        LOGGER.log(Level.INFO, "COMANDO COMPLETO : " + Arrays.toString(comando));
        return comando;
    }

    public String getCommand() {
        return StringUtils.join(getCommandAsArray(), " ");
    }

    private class StreamEater
            extends Thread {

        private InputStream stream;
        private ByteArrayOutputStream bytes;

        private IOException error;

        public StreamEater(InputStream stream) {
            this.stream = stream;

            bytes = new ByteArrayOutputStream();
        }

        public void run() {
            try {
                int bytesRead = stream.read();
                while (bytesRead >= 0) {
                    bytes.write(bytesRead);
                    bytesRead = stream.read();
                }

                stream.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error en Stream Eater ", e);
                error = e;
            }
        }

        public IOException getError() {
            return error;
        }

        public byte[] getBytes() {
            return bytes.toByteArray();
        }
    }
}
