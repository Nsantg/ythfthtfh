package org.example.threads;

import org.example.model.Medico;
import org.example.model.Atencion;
import org.example.dao.HospitalDAO;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MedicoThread extends Thread {
    private Medico medico;
    private boolean running = true;
    private Random random = new Random();
    private HospitalDAO hospitalDAO;
    private final int maxAtenciones;
    private final AtomicInteger atencionesRealizadas;
    private final CountDownLatch latch;

    public MedicoThread(Medico medico, int maxAtenciones, CountDownLatch latch) {
        this.medico = medico;
        this.hospitalDAO = new HospitalDAO();
        this.maxAtenciones = maxAtenciones;
        this.atencionesRealizadas = new AtomicInteger(0);
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            while (running && atencionesRealizadas.get() < maxAtenciones) {
                atenderPaciente();
                try {
                    // Tiempo aleatorio de espera entre pacientes (entre 1 y 3 segundos)
                    Thread.sleep(random.nextInt(2000) + 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            latch.countDown();
            System.out.printf("%s completó %d atenciones\n",
                    medico.getNombre(),
                    atencionesRealizadas.get());
        }
    }

    private void atenderPaciente() {
        int tiempoAtencion = random.nextInt(10) + 1; // 1-10 minutos
        Atencion atencion = new Atencion();
        atencion.setMedico(medico);
        atencion.setTiempoAtencion(tiempoAtencion);
        atencion.setFechaAtencion(LocalDateTime.now());

        try {
            hospitalDAO.guardarAtencion(atencion);
            atencionesRealizadas.incrementAndGet();
            System.out.printf("%s atendió paciente %d/%d (tiempo: %d min)\n",
                    medico.getNombre(),
                    atencionesRealizadas.get(),
                    maxAtenciones,
                    tiempoAtencion);
        } catch (Exception e) {
            System.err.println("Error al guardar la atención: " + e.getMessage());
        }
    }

    public void stopAtencion() {
        this.running = false;
    }
}