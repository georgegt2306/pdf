package ec.cajamarca.sic.wk.configurations;

public class WrapperConfigBuilder {

    private String wkhtmltopdfCommand = System.getenv("WKHTMLTOPDF_PATH");

    public WrapperConfigBuilder setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
        return this;
    }

    public WrapperConfig build() {
        return new WrapperConfig(wkhtmltopdfCommand);
    }
}
