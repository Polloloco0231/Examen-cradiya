package Modelo.Clases;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private int clienteId;
    private int empleadoId;
    private double monto;
    private double interes;
    private int cuotas;
    private double montoTotal;
    private double cuotaMensual;
    private double saldoPendiente;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private EstadoPrestamo estado;

    public Prestamo() {
    }

    public Prestamo(int id, int clienteId, int empleadoId, double monto, double interes, int cuotas, double montoTotal, double cuotaMensual, double saldoPendiente, LocalDate fechaInicio, LocalDate fechaVencimiento, EstadoPrestamo estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.empleadoId = empleadoId;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.montoTotal = montoTotal;
        this.cuotaMensual = cuotaMensual;
        this.saldoPendiente = saldoPendiente;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public Prestamo(int clienteId, int empleadoId, double monto, double interes, int cuotas, LocalDate fechaInicio) {
        this.clienteId = clienteId;
        this.empleadoId = empleadoId;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.montoTotal = monto + (monto * interes / 100);
        this.cuotaMensual = montoTotal / cuotas;
        this.saldoPendiente = montoTotal;
        this.fechaVencimiento = fechaInicio.plusMonths(cuotas);
        this.estado = EstadoPrestamo.ACTIVO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaMensual(double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }


    public void registrarAbono(double abono) {
        saldoPendiente -= abono;
        if (saldoPendiente <= 0) {
            saldoPendiente = 0;
            estado = EstadoPrestamo.PAGADO;
        }
    }

    public boolean estaVencido() {
        return estado == EstadoPrestamo.ACTIVO
                && fechaVencimiento.isBefore(LocalDate.now());
    }



    public String aLinea() {
        return id + ";" + clienteId + ";" + empleadoId + ";" + monto + ";" + interes + ";" +
                cuotas + ";" + montoTotal + ";" + cuotaMensual + ";" + saldoPendiente + ";" +
                fechaInicio + ";" + fechaVencimiento + ";" + estado;
    }

    public static Prestamo deLinea(String linea) {
        String[] d = linea.split(";");
        return new Prestamo(
                Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]),
                Double.parseDouble(d[3]), Double.parseDouble(d[4]), Integer.parseInt(d[5]),
                Double.parseDouble(d[6]), Double.parseDouble(d[7]), Double.parseDouble(d[8]),
                LocalDate.parse(d[9]), LocalDate.parse(d[10]), EstadoPrestamo.valueOf(d[11]));
    }

    @Override
    public String toString() {
        return "Prestamo{id=" + id + ", clienteId=" + clienteId + ", empleadoId=" + empleadoId +
                ", monto=" + monto + ", total=" + montoTotal + ", cuota=" + cuotaMensual +
                ", saldo=" + saldoPendiente + ", vence=" + fechaVencimiento +
                ", estado=" + estado + '}';
    }
}
