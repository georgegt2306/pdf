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
public class DocumentGeneric {

    String content;
    String name;
    String type;

    public DocumentGeneric() {
    }

    public DocumentGeneric(String content, String name, String type) {
        this.content = content;
        this.name = name;
        this.type = type;
    }

}
