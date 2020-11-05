package recortador;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Main {

	public static void setLectura(String[] lectura) {
		Main.lectura = lectura;
	}

	static LinkedList<String> listaImagenes = new LinkedList<>();

	static String directorioImagenes;

	static String directorioActual = "";

	public static String[] getSonido() {
		return sonido;
	}

	public static void setSonido(String[] sonido) {
		Main.sonido = sonido;
	}

	static String[] sonido = Metodos.leerFicheroArray("Config/sonido.txt", 2);

	static String os = System.getProperty("os.name");

	private static String[] lectura = Metodos.leerFicheroArray("Config/Config.txt", 2);

	public static String[] getLectura() {
		return lectura;
	}

	public static String getOs() {
		return os;
	}

	public static String getSeparador() {
		return separador;
	}

	static String separador = Metodos.saberSeparador(os);

	public static String getDirectorioActual() {
		return directorioActual;
	}

	public static void main(String[] args) {

		Metodos.crearCarpetas();

		LinkedList<String> listaImagenes = new LinkedList<>();

		try {
			directorioActual = new File(".").getCanonicalPath() + separador;
			directorioImagenes = directorioActual + "Config" + separador + "imagenes";
		} catch (IOException e) {
			//
		}
		try {

			listaImagenes = Metodos.directorio(directorioActual + "Config" + separador + "imagenes_para_recortar", ".");

			if (!listaImagenes.isEmpty()) {

				Metodos.renombrarArchivos(listaImagenes,
						directorioActual + "Config" + separador + "imagenes_para_recortar" + separador);
			}

			new PhotoFrame().setVisible(true);
		}

		catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
