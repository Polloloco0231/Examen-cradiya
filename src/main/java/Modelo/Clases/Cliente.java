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
        return getId() + ";" + getNombre() + ";" + getDocumento() + ";" +
                (getCorreo() == null ? "" : getCorreo()) + ";" +
                (telefono == null ? "" : telefono);
    }

    public static Cliente deLinea(String linea) {
        String[] d = linea.split(";", -1);
        String correo = d[3].isEmpty() ? null : d[3];
        String telefono = d[4].isEmpty() ? null : d[4];
        return new Cliente(Integer.parseInt(d[0]), d[1], d[2], correo, telefono);
    }
}
