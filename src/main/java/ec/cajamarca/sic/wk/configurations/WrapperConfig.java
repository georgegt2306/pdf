package ec.cajamarca.sic.wk.configurations;

public class WrapperConfig {

    private String wkhtmltopdfCommand = System.getenv("WKHTMLTOPDF_PATH");

    public WrapperConfig(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    public String getWkhtmltopdfCommand() {
        return wkhtmltopdfCommand;
    }

    public void setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }
}
