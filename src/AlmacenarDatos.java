import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlmacenarDatos {
    private static double[] ciudades;

    private static double[][] distancias;

    public static double[] getCiudades() {
        return ciudades;
    }

    public static double[][] getDistancias() {
        return distancias;
    }

    public AlmacenarDatos() {}

    public static void inicializacionDatos(String nombreArchivo) {
        cargarDatosACiudades(nombreArchivo);
        calcularDistancias();
    }

    private static void cargarDatosACiudades(String nombreArchivo) {
        String nombre = "data/"+ nombreArchivo;
        try (FileReader fr = new FileReader(nombre)) {
            BufferedReader br = new BufferedReader(fr);
            int tamCiudades = 0;
            String linea;
            for(int i = 0; i < 6; ++i){
                linea = br.readLine();
                String primerString = linea.split(":")[0].replace(" ", "");
                if(primerString.equals("DIMENSION")) {
                    tamCiudades = Integer.parseInt(linea.split(":")[1].replace(" ", ""));
                }
            }
            ciudades = new double[tamCiudades*2];
            linea = br.readLine();
            while(!linea.equals("EOF")){
                String[] split = linea.split(" ");
                //Eliminamos los espacios en blanco del vector
                ArrayList<Double> vectorCompactado = new ArrayList<>();
                for(int j = 0; j < split.length; ++j){
                    if(!split[j].equals("")){
                        vectorCompactado.add(Double.parseDouble(split[j]));
                    }
                }
                //Almacenamos las 2 coordenadas que contiene cada línea en su respectiva posición en el vector y en la casilla que le sucede
                int posicion = (int) Math.floor(vectorCompactado.get(0));
                --posicion;
                if(posicion != 0) posicion = posicion*2;
                ciudades[posicion] = vectorCompactado.get(1);
                ciudades[posicion + 1] = vectorCompactado.get(2);
                try {
                    linea = br.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(AlmacenarDatos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void calcularDistancias(){
        int tamDistancias = ciudades.length/2;
        distancias = new double[tamDistancias][tamDistancias];
        for(int i = 0; i < tamDistancias; i++){
            for(int j = i; j < tamDistancias; j++){
                if(i == j){
                    distancias[i][j] = Double.POSITIVE_INFINITY;
                }else{
                    distancias[i][j] = distancias[j][i] = Math.sqrt(Math.pow(ciudades[i*2] - ciudades[j*2], 2) + Math.pow(ciudades[(i*2) + 1] - ciudades[(j*2) + 1], 2));
                }
            }
        }
    }
}