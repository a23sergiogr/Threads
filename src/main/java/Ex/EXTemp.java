package Ex;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Ex.EXTemp.*;

public class EXTemp {

    static final int NUM_THREADS = 10;
    static Double[] resultados = new Double[NUM_THREADS];
    static boolean archivoCreado = false;

    static final Path path = Paths.get("src/main/resources/temperaturas_santiago.json");
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void main(String[] args){
        List<Double> list = new ArrayList<>(); //0->3649
        try {
            list = gson.fromJson( new FileReader(String.valueOf(path)), List.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        long timeStartMultipleThreads = System.nanoTime();
        List<Thread> threads = new ArrayList<>();

        int tempPorThread = 3649/NUM_THREADS + 1;
        int start = 0;
        for (int i = 0; i < NUM_THREADS; i++) {
            Runnable runnable = new medCount(list, start, tempPorThread);
            Thread th = new Thread(runnable, String.valueOf(i));
            threads.add(th);
            th.start();
            start = start + tempPorThread;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        double sum = 0;
        for(Double d : resultados)
            sum = sum + d;

        long timeEndMultipleThreads = System.nanoTime();
        System.out.println("Suma Total [" + NUM_THREADS + " Thread]: " + sum);
        System.out.println("Temperatura Media [" + NUM_THREADS + " Thread]: " + sum/list.size());


        long timeStartOneThread = System.nanoTime();
        double sum2 = 0;
        for (Double aDouble : list)
            sum2 = sum2 + aDouble;


        long timeEndOneThread = System.nanoTime();
        System.out.println("Suma Total [1 Thread]: " + sum2);
        System.out.println("Temperatura Media [1 Thread]: " + sum2/list.size());

        System.out.println("\n\nTiempo Total [" + NUM_THREADS + " Thread]: " + (timeEndMultipleThreads - timeStartMultipleThreads));
        System.out.println("Tiempo Total [1 Thread]: " + (timeEndOneThread - timeStartOneThread));
    }

    public static void createArray(){
        double[] temperaturas = new double[3650];

        // Instancia de Random para generar números aleatorios
        Random random = new Random();

        // Simulación de las temperaturas día por día
        for (int i = 0; i < temperaturas.length; i++) {
            // Determinar el día del año (0-364)
            int diaDelAno = i % 365;

            // Variables de rango de temperatura para cada estación
            double tempMin, tempMax;

            // Verano (Diciembre a Febrero)
            if (diaDelAno >= 335 || diaDelAno <= 59) {
                tempMin = 15.0;
                tempMax = 35.0;
            }
            // Otoño (Marzo a Mayo)
            else if (diaDelAno >= 60 && diaDelAno <= 151) {
                tempMin = 10.0;
                tempMax = 25.0;
            }
            // Invierno (Junio a Agosto)
            else if (diaDelAno >= 152 && diaDelAno <= 243) {
                tempMin = 0.0;
                tempMax = 15.0;
            }
            // Primavera (Septiembre a Noviembre)
            else {
                tempMin = 10.0;
                tempMax = 28.0;
            }

            // Generar una temperatura aleatoria dentro del rango
            temperaturas[i] = tempMin + (tempMax - tempMin) * random.nextDouble();
        }

        // Mostrar algunas temperaturas para verificar
        System.out.println("Temperaturas en Santiago (ejemplo de los primeros 10 días):");
        for (int i = 0; i < 10; i++) {
            System.out.printf("Día %d: %.2f°C%n", i + 1, temperaturas[i]);
        }

        // Convertir el array de temperaturas a JSON usando Gson
        String jsonTemperaturas = gson.toJson(temperaturas);

        try (FileWriter writer = new FileWriter(String.valueOf(path))) {
            writer.write(jsonTemperaturas);
            System.out.println("Temperaturas guardadas en 'temperaturas_santiago.json'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean getArchivoCreado(){
        return archivoCreado;
    }
    static void setArchivoCreado(){
        archivoCreado = true;
    }
}

class medCount implements Runnable{
    static final Path path = Paths.get("src/main/resources/temperaturas_media_por_thread.txt");

    private final List<Double> list;
    private final int start;
    private final int tempPorThread;

    public medCount(List<Double> list, int start, int tempPorThread) {
        this.list = list;
        this.start = start;
        this.tempPorThread = tempPorThread;
    }

    @Override
    public void run() {
        double med = 0;
        //try(var fw = new FileWriter("src/main/resources/temperaturas_santiago_thread_" + Thread.currentThread().getName() + ".log", false)){
            for (int i = start; i < tempPorThread+start; i++) {
                //fw.write("I: " + i + ", Med: " + med + "[" + Thread.currentThread().getName() + "]\n");
                med = (list.get(i) + med);
            }
//        } catch (IOException e){
//            System.err.println("Error al guardar info");
//        }
        resultados[Integer.parseInt(Thread.currentThread().getName())] = med;
//        try(var fw = new FileWriter(String.valueOf(path), getArchivoCreado())){
//            fw.write("Med: " + med + "[" + Thread.currentThread().getName() + "]\n");
//            setArchivoCreado();
//        } catch (IOException e){
//            System.err.println("Error al guardar info");
//        }
    }
}
