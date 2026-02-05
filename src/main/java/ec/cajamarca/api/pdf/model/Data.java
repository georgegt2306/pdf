/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.cajamarca.api.pdf.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author javier
 */
@Getter
@Setter
public class Data {

    private String key;
    private String value;
    private Object tmp;
    private DataType dataType;

    public Data() {
    }

    public Data(String key, String value, DataType dataType) {
        this.key = key;
        this.value = value;
        this.dataType = dataType;
    }

}
