module br.com.mauricioborges.graficos {
    requires javafx.controls;
    requires javafx.fxml;
     
    opens br.com.mauricioborges.graficos.gui to javafx.fxml;
  
    exports br.com.mauricioborges.graficos.gui;
    exports br.com.mauricioborges.graficos.math;
}
