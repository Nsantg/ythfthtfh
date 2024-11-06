package org.example.main;

import org.example.model.*;
import org.example.enums.*;
import org.example.threads.*;
import org.example.dao.*;
import java.util.*;
import java.util.concurrent.*;

public class HospitalSimulation {
    private List<MedicoThread> medicoThreads = new ArrayList<>();
    private HospitalDAO hospitalDAO;
    private static final int SIMULATION_TIME_MINUTES = 5; // Tiempo de simulación en minutos
    private static final int MAX_ATENCIONES_POR_MEDICO = 20; // Máximo de atenciones por médico

    public HospitalSimulation() {
        this.hospitalDAO = new HospitalDAO();
    }

    public void iniciarSimulacion() {
        try {
            // Crear médicos si no existen
            if (contarMedicos() == 0) {
                crearMedicosIniciales();
            }

            // Limpiar atenciones anteriores
            hospitalDAO.limpiarAtenciones();

            // Iniciar threads para cada médico
            List<Medico> medicos = hospitalDAO.listarMedicos();
            CountDownLatch latch = new CountDownLatch(medicos.size());

            for (Medico medico : medicos) {
                MedicoThread thread = new MedicoThread(medico, MAX_ATENCIONES_POR_MEDICO, latch);
                medicoThreads.add(thread);
                thread.start();
            }

            System.out.println("Simulación iniciada por " + SIMULATION_TIME_MINUTES + " minutos o hasta " +
                    MAX_ATENCIONES_POR_MEDICO + " atenciones por médico...");

            // Esperar a que termine el tiempo o todas las atenciones
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                detenerSimulacion();
                System.out.println("\n¡Tiempo de simulación terminado!");
            }, SIMULATION_TIME_MINUTES, TimeUnit.MINUTES);

            // Esperar a que todos los médicos terminen sus atenciones
            try {
                latch.await();
                detenerSimulacion();
                System.out.println("\n¡Todos los médicos han completado sus atenciones!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            executor.shutdownNow();
            mostrarEstadisticas();
        } catch (Exception e) {
            System.err.println("Error al iniciar la simulación: " + e.getMessage());
        }
    }

    private void detenerSimulacion() {
        for (MedicoThread thread : medicoThreads) {
            thread.stopAtencion();
        }
        medicoThreads.clear();
    }

    public void borrarDatos() {
        try {
            hospitalDAO.limpiarAtenciones();
            System.out.println("Datos borrados exitosamente");
        } catch (Exception e) {
            System.err.println("Error al borrar los datos: " + e.getMessage());
        }
    }

    private void crearMedicosIniciales() {
        try {
            for (Especialidad esp : Especialidad.values()) {
                Medico medico = new Medico();
                medico.setNombre("Dr. " + esp.name().toLowerCase());
                medico.setEspecialidad(esp);
                hospitalDAO.guardarMedico(medico);
            }
        } catch (Exception e) {
            System.err.println("Error al crear médicos iniciales: " + e.getMessage());
        }
    }

    private long contarMedicos() {
        try {
            return hospitalDAO.listarMedicos().size();
        } catch (Exception e) {
            System.err.println("Error al contar médicos: " + e.getMessage());
            return 0;
        }
    }

    public void mostrarEstadisticas() {
        try {
            System.out.println("\n=== ESTADÍSTICAS FINALES ===");
            List<Medico> medicos = hospitalDAO.listarMedicos();
            for (Medico medico : medicos) {
                List<Atencion> atenciones = hospitalDAO.listarAtencionesPorMedico(medico);

                // Calcular tiempo total y promedio
                int tiempoTotal = atenciones.stream()
                        .mapToInt(Atencion::getTiempoAtencion)
                        .sum();

                double tiempoPromedio = atenciones.isEmpty() ? 0 :
                        tiempoTotal / (double) atenciones.size();

                System.out.println("\nEstadísticas del " + medico.getNombre() +
                        " (" + medico.getEspecialidad() + "):");
                System.out.println("- Pacientes atendidos: " + atenciones.size());
                System.out.println("- Tiempo total de atención: " + tiempoTotal + " minutos");
                System.out.println("- Tiempo promedio por paciente: " +
                        String.format("%.2f", tiempoPromedio) + " minutos");
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar estadísticas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        HospitalSimulation simulation = new HospitalSimulation();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== SIMULACIÓN HOSPITAL ===");
            System.out.println("1. Iniciar nueva simulación");
            System.out.println("2. Ver estadísticas");
            System.out.println("3. Borrar datos");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        simulation.iniciarSimulacion();
                        break;
                    case 2:
                        simulation.mostrarEstadisticas();
                        break;
                    case 3:
                        simulation.borrarDatos();
                        System.out.println("Datos borrados!");
                        break;
                    case 4:
                        running = false;
                        break;
                    default:
                        System.out.println("Opción inválida!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido");
                scanner.nextLine(); // Limpiar el buffer
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar el buffer
            }
        }
        scanner.close();
        System.exit(0);
    }
}