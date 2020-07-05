package org.atwjsw.service;

import lombok.Data;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@Data
@SessionScoped
public class MySession implements Serializable {

    private String email;

}
