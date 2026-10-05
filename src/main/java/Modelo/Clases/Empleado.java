package Modelo.Clases;

public class Empleado extends Persona{

    private String rol;
    private double salario;

    public Empleado(int id, String nombre, String documento, String correo, String rol, double salario) {
        super(id, nombre, documento, correo);
        this.rol = rol;
        this.salario = salario;
    }

    public Empleado(String nombre, String documento, String correo, String rol, double salario) {
        super(nombre, documento, correo);
        this.rol = rol;
        this.salario = salario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public String obtenerResumen() {
        return getNombre() + " - " + rol + " - $" + salario;
    }

    @Override
    public String toString() {
        return super.toString() + " rol=" + rol + ", salario=" + salario;
    }

    public String aLinea() {
        return getId() + ";" + getNombre() + ";" + getDocumento() + ";" +
                (getCorreo() == null ? "" : getCorreo()) + ";" + rol + ";" + salario;
    }

    public static Empleado deLinea(String linea) {
        String[] d = linea.split(";", -1);
        String correo = d[3].isEmpty() ? null : d[3];
        return new Empleado(Integer.parseInt(d[0]), d[1], d[2], correo, d[4],
                Double.parseDouble(d[5]));
    }
}
