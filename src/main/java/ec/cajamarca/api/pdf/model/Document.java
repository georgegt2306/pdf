/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.cajamarca.api.pdf.model;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author javier
 */
@Getter
@Setter
public class Document {

    private String template;
    private List<Data> dataList;

    public Document() {
    }

}
