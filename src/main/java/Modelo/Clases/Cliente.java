package Modelo.Clases;

public class Cliente extends Persona{

    private String telefono;

    public Cliente(int id, String nombre, String documento, String correo, String telefono) {
        super(id, nombre, documento, correo);
        this.telefono = telefono;
    }

    public Cliente(String nombre, String documento, String correo, String telefono) {
        super(nombre, documento, correo);
        this.telefono = telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String obtenerResumen() {
        return getNombre() + " - Tel: " + telefono;
    }

    @Override
    public String toString() {
        return super.toString() + " telefono='" + telefono + "'";
    }

    public String aLinea() {
        return getId() + ";" + getNombre() + ";" + getDocumento() + ";" + getCorreo() + ";" + telefono ;
    }

    public static Empleado deLinea(String linea) {
        String[] d = linea.split(";");
        return new Empleado(Integer.parseInt(d[0]), d[1], d[2], d[3], d[4], Double.parseDouble(d[5]));
    }
}
