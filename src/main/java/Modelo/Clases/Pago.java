package Modelo.Clases;

import java.time.LocalDate;

public class Pago {
    private int id;
    private int prestamoId;
    private LocalDate fechaPago;
    private double monto;

    public Pago(int id, int prestamoId, LocalDate fechaPago, double monto) {
        this.id = id;
        this.prestamoId = prestamoId;
        this.fechaPago = fechaPago;
        this.monto = monto;
    }

    public Pago(int prestamoId, LocalDate fechaPago, double monto) {
        this.prestamoId = prestamoId;
        this.fechaPago = fechaPago;
        this.monto = monto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }


    public String aLinea() {
        return id + ";" + prestamoId + ";" + fechaPago + ";" + monto;
    }

    public static Pago deLinea(String linea) {
        String[] d = linea.split(";");
        return new Pago(Integer.parseInt(d[0]), Integer.parseInt(d[1]),
                LocalDate.parse(d[2]), Double.parseDouble(d[3]));
    }

    @Override
    public String toString() {
        return "Pago{id=" + id + ", prestamoId=" + prestamoId +
                ", fecha=" + fechaPago + ", monto=" + monto + '}';
    }
}
